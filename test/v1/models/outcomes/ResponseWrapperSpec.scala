/*
 * Copyright 2019 HM Revenue & Customs
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

package v1.models.outcomes

import play.api.http.Status._
import support.UnitSpec
import v1.models.errors.{DownstreamError, ErrorWrapper, MtdError, MtdErrors}

class ResponseWrapperSpec extends UnitSpec {

  "ResponseWrapper" when {
    "mapped" should {
      "map the response data correctly" in {
        ResponseWrapper("id", "someString").map(_.toLowerCase) shouldBe ResponseWrapper("id", "somestring")
      }
    }

    "mapped to an Either" should {
      "wrap a successful response correctly" in {
        val mappedResponse = ResponseWrapper("id", "someString").mapToEither { case "someString" => Right("aSuccess") }
        mappedResponse shouldBe Right(ResponseWrapper("id", "aSuccess"))
      }

      "wrap an error response correctly" in {
        val err = MtdErrors(IM_A_TEAPOT, DownstreamError, None)
        val mappedResponse = ResponseWrapper("id", "someString").mapToEither { case "someString" => Left(err) }
        mappedResponse shouldBe Left(ErrorWrapper(Some("id"), err))
      }
    }

    "toErrorWhen" should {

      val errors = MtdErrors(BAD_REQUEST, MtdError("CODE", "message"))

      "return a success when the error condition is not met" in {
        val responseWrapper = ResponseWrapper("id", "nonempty")
        responseWrapper.toErrorWhen { case "" => errors } shouldBe Right(responseWrapper)
      }

      "return errors when the error condition is met" in {
        ResponseWrapper("id", "").toErrorWhen { case "" => errors } shouldBe Left(ErrorWrapper(Some("id"), errors))
      }
    }
  }
}