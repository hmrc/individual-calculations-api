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

package v3.connectors

import mocks.{ MockAppConfig, MockHttpClient }
import play.api.libs.json.{ JsString, Json, Reads }
import uk.gov.hmrc.http.HeaderCarrier
import v3.connectors.httpparsers.StandardHttpParser.SuccessCode
import v3.handler.RequestDefn
import v3.models.errors.{ BackendErrorCode, BackendErrors }

import scala.concurrent.Future

class StandardConnectorSpec extends ConnectorSpec {

  class Test extends MockHttpClient with MockAppConfig {
    MockAppConfig.downstreamBaseUrl returns baseUrl
    MockAppConfig.downstreamToken returns "Bearer downstream-token"
    MockAppConfig.downstreamEnvironment returns "downstream-environment"
    MockAppConfig.downstreamEnvironmentHeaders returns Some(allowedDownstreamHeaders)

    val connector: StandardConnector = new StandardConnector(mockAppConfig, mockHttpClient)
  }

  case class Response(data: String)

  object Response {
    implicit val reads: Reads[Response] = Json.reads[Response]
  }

  val queryParams: Seq[(String, String)] = Seq("n" -> "v")
  val requestDefn: RequestDefn.Get       = RequestDefn.Get("/some/uri", queryParams)

  implicit val successCode: SuccessCode = SuccessCode(200)

  val test: Seq[String] = Seq("test1", "test2")

  "StandardConnector" should {

    implicit val hc: HeaderCarrier = HeaderCarrier().withExtraHeaders(headers = "Authorization" -> "Bearer user-token")

    "return a successful response" when {

      "request is a get" in new Test {
        val expected = Right(Response("someData"))

        MockedHttpClient
          .get(
            url = s"$baseUrl/some/uri",
            queryParameters = queryParams,
            config = dummyHeaderCarrierConfig,
            requiredHeaders = requiredBackendHeaders
          )
          .returns(Future.successful(expected))

        await(connector.doRequest[Response](requestDefn)) shouldBe expected
      }

      "request is a post" in new Test {
        val body: JsString                    = JsString("some value")
        val postRequestDefn: RequestDefn.Post = RequestDefn.Post("/some/uri", body)

        val expected = Right(Response("someData"))

        MockedHttpClient
          .post(
            url = s"$baseUrl/some/uri",
            config = dummyHeaderCarrierConfig,
            body = body,
            requiredHeaders = requiredBackendHeaders
          )
          .returns(Future.successful(expected))

        await(connector.doRequest[Response](postRequestDefn)) shouldBe expected
      }
    }

    "return an backend error response" when {

      "an error response is returned from the backend" in new Test {
        val expected = Left(BackendErrors.single(BAD_REQUEST, BackendErrorCode("BACKEND ERROR CODE")))

        MockedHttpClient
          .get(
            url = s"$baseUrl/some/uri",
            queryParameters = queryParams,
            config = dummyHeaderCarrierConfig,
            requiredHeaders = requiredBackendHeaders
          )
          .returns(Future.successful(expected))

        await(connector.doRequest[Response](requestDefn)) shouldBe expected
      }
    }

    "return an exception" when {

      "when an unexpected error is returned" in new Test {
        MockedHttpClient
          .get(
            url = s"$baseUrl/some/uri",
            queryParameters = queryParams,
            config = dummyHeaderCarrierConfig,
            requiredHeaders = requiredBackendHeaders
          )
          .returns(Future.failed(new Exception("unexpected exception")))

        the[Exception] thrownBy await(connector.doRequest[Response](requestDefn)) should have message "unexpected exception"
      }
    }
  }
}
