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

package v1.connectors

import config.AppConfig
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads}
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import v1.mocks.{MockAppConfig, MockHttpClient}
import v1.models.outcomes.ResponseWrapper
import v1.models.response.common.DesResponse

import scala.concurrent.Future

class BaseConnectorSpec extends ConnectorSpec {

  class Test extends MockHttpClient with MockAppConfig {

    case class Result(value: Int)

    val body: String = "body"
    val outcome: Either[Nothing, ResponseWrapper[Any]] = Right(ResponseWrapper(correlationId, Result(2)))
    val url: String = "some/url?param=value"
    val absoluteUrl: String = s"$baseUrl/$url"

    val connector: BaseConnector = new BaseConnector {
      val http: HttpClient = mockHttpClient
      val appConfig: AppConfig = mockAppConfig
    }

    implicit val httpReads: HttpReads[BackendOutcome[Result]] = mock[HttpReads[BackendOutcome[Result]]]
  }

  "post" must {
    "posts with the required backend headers and returns the result" in new Test {
      MockedAppConfig.backendBaseUrl returns baseUrl

      MockedHttpClient
        .post(absoluteUrl, body, "Authorization" -> s"Bearer user-token")
        .returns(Future.successful(outcome))

      await(connector.post(body, url)) shouldBe outcome
    }
  }

  "desPost" must {
    "posts with the required des headers and returns the result" in new Test {
      implicit val hc: HeaderCarrier = HeaderCarrier()

      val desHeaders: Seq[(String, String)] = Seq(
        "Authorization" -> s"Bearer des-token",
        "Environment" -> "des-env"
      )

      class DesResult(override val value: Int) extends Result(value) with DesResponse
      val desUrl: Uri[DesResult] = Uri[DesResult](url)

      implicit val desHttpReads: HttpReads[BackendOutcome[DesResult]] = mock[HttpReads[BackendOutcome[DesResult]]]

      MockedAppConfig.desToken returns "des-token"
      MockedAppConfig.desEnvironment returns "des-env"
      MockedAppConfig.desBaseUrl returns baseUrl

      MockedHttpClient
        .post(absoluteUrl, body, desHeaders:_*)
        .returns(Future.successful(outcome))

      await(connector.desPost(body, desUrl)) shouldBe outcome
    }
  }

  "get" must {
    "get with the required backend headers and return the result with query parameters" in new Test {
      MockedAppConfig.backendBaseUrl returns baseUrl

      MockedHttpClient
        .get(absoluteUrl, Seq(("key", "value")), requiredHeaders :_*)
        .returns(Future.successful(outcome))

      await(connector.get(url, Seq(("key", "value")))) shouldBe outcome
    }
  }
}
