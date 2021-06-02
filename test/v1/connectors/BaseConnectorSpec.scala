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

import config.AppConfig
import mocks.{MockAppConfig, MockHttpClient}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpReads}
import v1.models.outcomes.ResponseWrapper
import v1.models.response.common.DesResponse

import scala.concurrent.Future

class BaseConnectorSpec extends ConnectorSpec {

  //WLOG
  case class Result(value: Int)

  //WLOG
  val body: String = "body"
  val outcome: Either[Nothing, ResponseWrapper[Any]] = Right(ResponseWrapper(correlationId, Result(2)))

  val url: String = "some/url?param=value"
  val absoluteUrl: String = s"$baseUrl/$url"

  implicit val httpReads: HttpReads[BackendOutcome[Result]] = mock[HttpReads[BackendOutcome[Result]]]

  class Test(desEnvironmentHeaders: Option[Seq[String]]) extends MockHttpClient with MockAppConfig {
    val connector: BaseConnector = new BaseConnector {
      val http: HttpClient = mockHttpClient
      val appConfig: AppConfig = mockAppConfig
    }

    MockAppConfig.desBaseUrl returns baseUrl
    MockAppConfig.desToken returns "des-token"
    MockAppConfig.desEnvironment returns "des-environment"
    MockAppConfig.desEnvironmentHeaders returns desEnvironmentHeaders
  }

  "BaseConnector" when {
    val requiredHeaders: Seq[(String, String)] = Seq(
      "Environment" -> "des-environment",
      "Authorization" -> s"Bearer des-token",
      "User-Agent" -> "self-assessment-accounts-api",
      "CorrelationId" -> correlationId,
      "Gov-Test-Scenario" -> "DEFAULT"
    )

    val excludedHeaders: Seq[(String, String)] = Seq(
      "AnotherHeader" -> "HeaderValue"
    )

    "making a HTTP request to a downstream service (i.e DES)" must {
      desTestHttpMethods(dummyDesHeaderCarrierConfig, requiredHeaders, excludedHeaders, Some(allowedDesHeaders))

      "exclude all `otherHeaders` when no external service header allow-list is found" should {
        val requiredHeaders: Seq[(String, String)] = Seq(
          "Environment" -> "des-environment",
          "Authorization" -> s"Bearer des-token",
          "User-Agent" -> "self-assessment-accounts-api",
          "CorrelationId" -> correlationId,
        )

        desTestHttpMethods(dummyDesHeaderCarrierConfig, requiredHeaders, otherHeaders, None)
      }
    }
  }

  def desTestHttpMethods(config: HeaderCarrier.Config,
                         requiredHeaders: Seq[(String, String)],
                         excludedHeaders: Seq[(String, String)],
                         desEnvironmentHeaders: Option[Seq[String]]): Unit = {

    "complete the request successfully with the required headers" when {
      "Get" in new Test(desEnvironmentHeaders) {
        MockedHttpClient
          .get(absoluteUrl, Seq(("key", "value")), config, requiredHeaders, excludedHeaders)
          .returns(Future.successful(outcome))

        await(connector.get(url, Seq(("key", "value")))) shouldBe outcome
      }

      "Post" in new Test(desEnvironmentHeaders) {
        implicit val hc: HeaderCarrier = HeaderCarrier(otherHeaders = otherHeaders ++ Seq("Content-Type" -> "application/json"))
        val requiredHeadersPost: Seq[(String, String)] = requiredHeaders ++ Seq("Content-Type" -> "application/json")

        MockedHttpClient.post(absoluteUrl, config, body, requiredHeadersPost, excludedHeaders)
          .returns(Future.successful(outcome))

        await(connector.post(body, url)) shouldBe outcome
      }

      "DesPost" in new Test(desEnvironmentHeaders) {
        class DesResult(override val value: Int) extends Result(value) with DesResponse
        val desUrl: Uri[DesResult] = Uri[DesResult](url)

        implicit val hc: HeaderCarrier = HeaderCarrier(otherHeaders = otherHeaders ++ Seq("Content-Type" -> "application/json"))
        val requiredHeadersPost: Seq[(String, String)] = requiredHeaders ++ Seq("Content-Type" -> "application/json")

        MockedHttpClient.post(absoluteUrl, config, body, requiredHeadersPost, excludedHeaders)
          .returns(Future.successful(outcome))

        await(connector.desPost(body, desUrl)) shouldBe outcome
      }
    }
  }
}