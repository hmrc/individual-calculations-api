/*
 * Copyright 2023 HM Revenue & Customs
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

package api.controllers

import api.models.errors.{ErrorWrapper, NotFoundError}
import play.api.Logger
import play.api.http.Status.NOT_FOUND
import play.api.mvc.Results
import support.UnitSpec
import utils.Logging

class BaseControllerSpec extends UnitSpec {

  trait MockLogging extends Logging {
    override lazy val logger: Logger = Logger("test-logger")
  }

  class TestController extends BaseController with MockLogging {}

  "BaseController" should {

    "add headers to response" in new TestController {
      val correlationId   = "testCorrelationId"
      val responseHeaders = Seq("X-Other-Header" -> "testHeaderValue")

      val result = Response(Results.Ok).withApiHeaders(correlationId, responseHeaders: _*)

      result.header.headers.get("X-CorrelationId") shouldBe Some("testCorrelationId")
      result.header.headers.get("X-Other-Header") shouldBe Some("testHeaderValue")
      result.header.headers.get("X-Content-Type-Options") shouldBe Some("nosniff")
    }

    "return the correct error response" in new TestController {
      val correlationId                      = "testCorrelationId"
      val notFoundErrorWrapper: ErrorWrapper = ErrorWrapper(correlationId, NotFoundError)
      val endpointLogContext                 = EndpointLogContext("cn", "en")

      val result = errorResult(notFoundErrorWrapper)(endpointLogContext)

      result.header.status shouldBe NOT_FOUND
      result.header.headers.get("X-CorrelationId") shouldBe Some("testCorrelationId")
      result.header.headers.get("X-Content-Type-Options") shouldBe Some("nosniff")
    }

  }

}
