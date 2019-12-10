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

  val wrappedResponse: ResponseWrapper[String] = ResponseWrapper(correlationId = "id", responseData = "someString")

  def wrapResponse(responseData: String): ResponseWrapper[String] = wrappedResponse.copy(responseData = responseData)

  "ResponseWrapper" when {
    "mapped" should {
      "map the response data correctly" in {
        wrappedResponse.map(_.toLowerCase) shouldBe wrapResponse(responseData = "somestring")
      }
    }

    "mapped to an Either" should {
      "wrap a successful response correctly" in {
        val mappedResponse = wrappedResponse.mapToEither { case "someString" => Right("aSuccess") }
        mappedResponse shouldBe Right(wrapResponse(responseData = "aSuccess"))
      }

      "wrap an error response correctly" in {
        val err = MtdErrors(IM_A_TEAPOT, DownstreamError, None)
        val mappedResponse = wrappedResponse.mapToEither { case "someString" => Left(err) }
        mappedResponse shouldBe Left(ErrorWrapper(Some("id"), err))
      }
    }

    "toErrorWhen" should {
      val errors = MtdErrors(statusCode = BAD_REQUEST, error = MtdError("CODE", "message"))

      "return a success when the error condition is not met" in {
        wrappedResponse.toErrorWhen { case "" => errors } shouldBe Right(wrappedResponse)
      }

      "return errors when the error condition is met" in {
        wrapResponse(responseData = "").toErrorWhen { case "" => errors } shouldBe Left(ErrorWrapper(Some("id"), errors))
      }
    }
  }
}