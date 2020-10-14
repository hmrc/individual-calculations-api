/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package v1.support

import play.api.http.Status._
import support.UnitSpec
import utils.Logging
import v1.controllers.EndpointLogContext
import v1.models.errors._
import v1.models.outcomes.ResponseWrapper

class BackendResponseMappingSupportSpec extends UnitSpec {

  implicit val logContext: EndpointLogContext = EndpointLogContext("ctrl", "ep")
  val mapping: BackendResponseMappingSupport = new BackendResponseMappingSupport with Logging {}

  val correlationId = "someCorrelationId"

  object Error1 extends MtdError("msg", "code1")

  object Error2 extends MtdError("msg", "code2")

  object ErrorBvrMain extends MtdError("msg", "bvrMain")

  object ErrorBvr extends MtdError("msg", "bvr")

  object ErrorPassthrough extends MtdError("PASS_THROUGH", "message")

  // WLOG
  val backendStatus = 455
  val mtdStatus     = 466

  val passThroughErrors = List(ErrorPassthrough)

  val errorCodeMap: PartialFunction[String, (Int, MtdError)] = {
    case "ERR1" => (mtdStatus, Error1)
    case "ERR2" => (mtdStatus, Error2)
    case "DS"   => (INTERNAL_SERVER_ERROR, DownstreamError)
  }

  "mapping backend errors" when {
    "single error" when {
      "the error code is in the map provided" must {
        "use the mapping and wrap" in {
          mapping.mapBackendErrors(passThroughErrors, errorCodeMap)(
            ResponseWrapper(correlationId, BackendErrors.single(backendStatus, BackendErrorCode("ERR1")))) shouldBe
            ErrorWrapper(Some(correlationId), Error1, None, mtdStatus)
        }
      }

      "the error code is to be passed through" must {
        "pass it through" in {
          mapping.mapBackendErrors(passThroughErrors, errorCodeMap)(
            ResponseWrapper(correlationId, BackendErrors.single(backendStatus, BackendErrorCode("PASS_THROUGH")))) shouldBe
            ErrorWrapper(Some(correlationId), ErrorPassthrough, None, backendStatus)
        }
      }

      "the error code is not in the map provided or passed though" must {
        "default to DownstreamError and wrap" in {
          mapping.mapBackendErrors(passThroughErrors, errorCodeMap)(
            ResponseWrapper(correlationId, BackendErrors.single(backendStatus, BackendErrorCode("UNKNOWN")))) shouldBe
            ErrorWrapper(Some(correlationId), DownstreamError, None, INTERNAL_SERVER_ERROR)
        }
      }
    }

    "multiple errors" when {
      "the error codes is in the map provided" must {
        "use the mapping and wrap with main error type of BadRequest" in {
          mapping.mapBackendErrors(passThroughErrors, errorCodeMap)(
            ResponseWrapper(correlationId, BackendErrors(backendStatus, List(BackendErrorCode("ERR1"), BackendErrorCode("ERR2"))))) shouldBe
            ErrorWrapper(Some(correlationId), BadRequestError, Some(Seq(Error1, Error2)), BAD_REQUEST)
        }
      }

      "the error code is to be passed through" must {
        "pass it through (but as bad request)" in {
          mapping.mapBackendErrors(passThroughErrors, errorCodeMap)(
            ResponseWrapper(correlationId, BackendErrors(backendStatus, List(BackendErrorCode("PASS_THROUGH"), BackendErrorCode("ERR2"))))) shouldBe
            ErrorWrapper(Some(correlationId), BadRequestError, Some(Seq(ErrorPassthrough, Error2)), BAD_REQUEST)
        }
      }

      "the error code is not in the map provided or passed through" must {
        "default main error to DownstreamError ignore other errors" in {
          mapping.mapBackendErrors(passThroughErrors, errorCodeMap)(
            ResponseWrapper(correlationId,
                            BackendErrors(backendStatus,
                                          List(BackendErrorCode("ERR1"), BackendErrorCode("PASS_THROUGH"), BackendErrorCode("UNKNOWN"))))) shouldBe
            ErrorWrapper(Some(correlationId), DownstreamError, None, INTERNAL_SERVER_ERROR)
        }
      }

      "one of the mapped errors is DownstreamError" must {
        "wrap the errors with main error type of DownstreamError" in {
          mapping.mapBackendErrors(passThroughErrors, errorCodeMap)(
            ResponseWrapper(
              correlationId,
              BackendErrors(backendStatus, List(BackendErrorCode("ERR1"), BackendErrorCode("PASS_THROUGH"), BackendErrorCode("DS"))))) shouldBe
            ErrorWrapper(Some(correlationId), DownstreamError, None, INTERNAL_SERVER_ERROR)
        }
      }
    }

    "the error code is an OutboundError" must {
      "return the error as is (in an ErrorWrapper) with a 400" in {
        mapping.mapBackendErrors(passThroughErrors, errorCodeMap)(ResponseWrapper(correlationId, OutboundError(backendStatus, ErrorBvrMain))) shouldBe
          ErrorWrapper(Some(correlationId), ErrorBvrMain, None, backendStatus)
      }
    }

    "the error code is an OutboundError with multiple errors" must {
      "return the error as is (in an ErrorWrapper)" in {
        mapping.mapBackendErrors(passThroughErrors, errorCodeMap)(
          ResponseWrapper(correlationId, OutboundError(backendStatus, ErrorBvrMain, Some(Seq(ErrorBvr))))) shouldBe
          ErrorWrapper(Some(correlationId), ErrorBvrMain, Some(Seq(ErrorBvr)), backendStatus)
      }
    }
  }

}
