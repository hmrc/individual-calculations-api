/*
 * Copyright 2022 HM Revenue & Customs
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

package v2.support

import play.api.http.Status._
import support.UnitSpec
import utils.Logging
import v2.controllers.EndpointLogContext
import v2.models.errors._
import v2.models.outcomes.ResponseWrapper

class BackendResponseMappingSupportSpec extends UnitSpec {

  implicit val logContext: EndpointLogContext = EndpointLogContext("ctrl", "ep")
  val mapping: BackendResponseMappingSupport = new BackendResponseMappingSupport with Logging {}

  val correlationId: String = "someCorrelationId"

  object Error1 extends MtdError("msg", "code1")

  object Error2 extends MtdError("msg", "code2")

  object ErrorBvrMain extends MtdError("msg", "bvrMain")

  object ErrorBvr extends MtdError("msg", "bvr")

  object ErrorPassthrough extends MtdError("PASS_THROUGH", "message")

  // WLOG
  val backendStatus: Int = 455
  val mtdStatus: Int = 466

  val passThroughErrors = List(ErrorPassthrough)

  val errorCodeMap: PartialFunction[String, (Int, MtdError)] = {
    case "ERR1" => (mtdStatus, Error1)
    case "ERR2" => (mtdStatus, Error2)
    case "DS"   => (INTERNAL_SERVER_ERROR, DownstreamError)
  }

  val downstreamErrorCodeMap : PartialFunction[String, MtdError] = {
    case "ERR1" => Error1
    case "ERR2" => Error2
    case "DS" => DownstreamError
  }

  "mapping backend errors" when {
    "single error" when {
      "the error code is in the map provided" must {
        "use the mapping and wrap" in {
          mapping.mapBackendErrors(passThroughErrors, errorCodeMap)(
            ResponseWrapper(correlationId, BackendErrors.single(backendStatus, BackendErrorCode("ERR1")))) shouldBe
            ErrorWrapper(correlationId, Error1, None, mtdStatus)
        }
      }

      "the error code is to be passed through" must {
        "pass it through" in {
          mapping.mapBackendErrors(passThroughErrors, errorCodeMap)(
            ResponseWrapper(correlationId, BackendErrors.single(backendStatus, BackendErrorCode("PASS_THROUGH")))) shouldBe
            ErrorWrapper(correlationId, ErrorPassthrough, None, backendStatus)
        }
      }

      "the error code is not in the map provided or passed though" must {
        "default to DownstreamError and wrap" in {
          mapping.mapBackendErrors(passThroughErrors, errorCodeMap)(
            ResponseWrapper(correlationId, BackendErrors.single(backendStatus, BackendErrorCode("UNKNOWN")))) shouldBe
            ErrorWrapper(correlationId, DownstreamError, None, INTERNAL_SERVER_ERROR)
        }
      }
    }

    "multiple errors" when {
      "the error code is in the map provided" must {
        "use the mapping and wrap with main error type of BadRequest" in {
          mapping.mapBackendErrors(passThroughErrors, errorCodeMap)(
            ResponseWrapper(correlationId, BackendErrors(backendStatus, List(BackendErrorCode("ERR1"), BackendErrorCode("ERR2"))))) shouldBe
            ErrorWrapper(correlationId, BadRequestError, Some(Seq(Error1, Error2)), BAD_REQUEST)
        }
      }

      "the error code is to be passed through" must {
        "pass it through (but as bad request)" in {
          mapping.mapBackendErrors(passThroughErrors, errorCodeMap)(
            ResponseWrapper(correlationId, BackendErrors(backendStatus, List(BackendErrorCode("PASS_THROUGH"), BackendErrorCode("ERR2"))))) shouldBe
            ErrorWrapper(correlationId, BadRequestError, Some(Seq(ErrorPassthrough, Error2)), BAD_REQUEST)
        }
      }

      "the error code is not in the map provided or passed through" must {
        "default main error to DownstreamError ignore other errors" in {
          mapping.mapBackendErrors(passThroughErrors, errorCodeMap)(
            ResponseWrapper(correlationId,
              BackendErrors(backendStatus,
                List(BackendErrorCode("ERR1"), BackendErrorCode("PASS_THROUGH"), BackendErrorCode("UNKNOWN"))))) shouldBe
            ErrorWrapper(correlationId, DownstreamError, None, INTERNAL_SERVER_ERROR)
        }
      }

      "one of the mapped errors is DownstreamError" must {
        "wrap the errors with main error type of DownstreamError" in {
          mapping.mapBackendErrors(passThroughErrors, errorCodeMap)(
            ResponseWrapper(
              correlationId,
              BackendErrors(backendStatus, List(BackendErrorCode("ERR1"), BackendErrorCode("PASS_THROUGH"), BackendErrorCode("DS"))))) shouldBe
            ErrorWrapper(correlationId, DownstreamError, None, INTERNAL_SERVER_ERROR)
        }
      }
    }

    "the error code is an OutboundError" must {
      "return the error as is (in an ErrorWrapper) with a 400" in {
        mapping.mapBackendErrors(passThroughErrors, errorCodeMap)(ResponseWrapper(correlationId, OutboundError(backendStatus, ErrorBvrMain))) shouldBe
          ErrorWrapper(correlationId, ErrorBvrMain, None, backendStatus)
      }
    }

    "the error code is an OutboundError with multiple errors" must {
      "return the error as is (in an ErrorWrapper)" in {
        mapping.mapBackendErrors(passThroughErrors, errorCodeMap)(
          ResponseWrapper(correlationId, OutboundError(backendStatus, ErrorBvrMain, Some(Seq(ErrorBvr))))) shouldBe
          ErrorWrapper(correlationId, ErrorBvrMain, Some(Seq(ErrorBvr)), backendStatus)
      }
    }
  }

  "mapping DES errors" when {
    "single error" when {
      "the error code is in the map provided" must {
        "use the mapping and wrap" in {
          val error: BackendErrors = BackendErrors.single(BAD_REQUEST, BackendErrorCode("ERR1"))

          mapping.mapDownstreamErrors(downstreamErrorCodeMap)(ResponseWrapper(correlationId, error)) shouldBe
            ErrorWrapper(correlationId, Error1, None, BAD_REQUEST)
        }
      }

      "the error code is not in the map provided" must {
        "default to DownstreamError and wrap" in {
          val error: BackendError = BackendErrors.single(INTERNAL_SERVER_ERROR, BackendErrorCode("UNKNOWN"))

          mapping.mapDownstreamErrors (downstreamErrorCodeMap)(ResponseWrapper(correlationId, error)) shouldBe
            ErrorWrapper(correlationId, DownstreamError, None, INTERNAL_SERVER_ERROR)
        }
      }
    }

    "multiple errors" when {
      "the error codownstream is in the map provided" must {
        "use the mapping and wrap with main error type of BadRequest" in {
          val errors: BackendErrors = BackendErrors(BAD_REQUEST, List(BackendErrorCode("ERR1"), BackendErrorCode("ERR2")))

          mapping.mapDownstreamErrors(downstreamErrorCodeMap)(ResponseWrapper(correlationId, errors)) shouldBe
            ErrorWrapper(correlationId, BadRequestError, Some(Seq(Error1, Error2)), BAD_REQUEST)
        }
      }

      "the error code is not in the map provided" must {
        "default main error to DownstreamError ignore other errors" in {
          val errors: BackendErrors = BackendErrors(INTERNAL_SERVER_ERROR, List(BackendErrorCode("ERR1"), BackendErrorCode("UNKNOWN")))

          mapping.mapDownstreamErrors(downstreamErrorCodeMap)(ResponseWrapper(correlationId, errors)) shouldBe
            ErrorWrapper(correlationId, DownstreamError, None, INTERNAL_SERVER_ERROR)
        }
      }

      "one of the mapped errors is DownstreamError" must {
        "wrap the errors with main error type of DownstreamError" in {
          val errors: BackendErrors = BackendErrors(INTERNAL_SERVER_ERROR, List(BackendErrorCode("ERR1"), BackendErrorCode("DS")))

          mapping.mapDownstreamErrors(downstreamErrorCodeMap)(ResponseWrapper(correlationId, errors)) shouldBe
            ErrorWrapper(correlationId, DownstreamError, None, INTERNAL_SERVER_ERROR)
        }
      }
    }

    "the error code is an OutboundError" must {
      "return the error as is (in an ErrorWrapper)" in {
        mapping.mapDownstreamErrors(downstreamErrorCodeMap)(ResponseWrapper(correlationId, OutboundError(BAD_REQUEST, ErrorBvrMain))) shouldBe
          ErrorWrapper(correlationId, ErrorBvrMain, None, BAD_REQUEST)
      }
    }

    "the error code is an OutboundError with multiple errors" must {
      "return the error as is (in an ErrorWrapper)" in {
        mapping.mapDownstreamErrors(downstreamErrorCodeMap)(ResponseWrapper(correlationId, OutboundError(BAD_REQUEST, ErrorBvrMain, Some(Seq(ErrorBvr))))) shouldBe
          ErrorWrapper(correlationId, ErrorBvrMain, Some(Seq(ErrorBvr)), BAD_REQUEST)
      }
    }
  }
}