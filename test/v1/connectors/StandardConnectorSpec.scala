/*
 * Copyright 2021 HM Revenue & Customs
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

package v1.connectors

import play.api.libs.json.{JsString, Json, Reads}
import v1.connectors.httpparsers.StandardHttpParser.SuccessCode
import v1.handler.RequestDefn
import v1.mocks.{MockAppConfig, MockHttpClient}
import v1.models.errors.{BackendErrorCode, BackendErrors}

import scala.concurrent.Future

class StandardConnectorSpec extends ConnectorSpec {

  class Test extends MockHttpClient with MockAppConfig {
    MockedAppConfig.backendBaseUrl returns baseUrl

    val connector: StandardConnector = new StandardConnector(mockAppConfig, mockHttpClient)
  }

  case class Response(data: String)

  object Response {
    implicit val reads: Reads[Response] = Json.reads[Response]
  }

  val queryParams: Seq[(String, String)] = Seq("n" -> "v")
  val requestDefn = RequestDefn.Get("/some/uri", queryParams)

  implicit val successCode: SuccessCode = SuccessCode(200)

  val test: Seq[String] = Seq("test1", "test2")

  "StandardConnector" should {

    "return a successful response" when {

      "request is a get" in new Test {
        val expected = Right(Response("someData"))

        MockedHttpClient
          .get(s"$baseUrl/some/uri",  queryParams)
          .returns(Future.successful(expected))

        await(connector.doRequest[Response](requestDefn)) shouldBe expected
      }

      "request is a post" in new Test {
        val body = JsString("some value")
        val postRequestDefn = RequestDefn.Post("/some/uri",body)

        val expected = Right(Response("someData"))

        MockedHttpClient
          .post(s"$baseUrl/some/uri", body)
          .returns(Future.successful(expected))

        await(connector.doRequest[Response](postRequestDefn)) shouldBe expected
      }
    }

    "return an backend error response" when {

      "an error response is returned from the backend" in new Test {
        val expected = Left(BackendErrors.single(BAD_REQUEST, BackendErrorCode("BACKEND ERROR CODE")))

        MockedHttpClient
          .get(s"$baseUrl/some/uri",  queryParams)
          .returns(Future.successful(expected))

        await(connector.doRequest[Response](requestDefn)) shouldBe expected
      }
    }

    "return an exception" when {

      "when an unexpected error is returned" in new Test {
        MockedHttpClient
          .get(s"$baseUrl/some/uri",  queryParams)
          .returns(Future.failed(new Exception("unexpected exception")))

        the[Exception] thrownBy await(connector.doRequest[Response](requestDefn)) should have message "unexpected exception"
      }
    }
  }
}
