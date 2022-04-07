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

import config.AppConfig
import mocks.{MockAppConfig, MockHttpClient}
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, HttpClient}
import v3.models.outcomes.ResponseWrapper
import v3.models.response.common.DownstreamResponse

import scala.concurrent.Future

class BaseConnectorSpec extends ConnectorSpec {

  // WLOG
  case class Result(value: Int)

  // WLOG
  val body: String                                   = "body"
  val outcome: Either[Nothing, ResponseWrapper[Any]] = Right(ResponseWrapper(correlationId, Result(2)))

  val url: String         = "some/url?param=value"
  val absoluteUrl: String = s"$baseUrl/$url"

  implicit val httpReads: HttpReads[BackendOutcome[Result]] = mock[HttpReads[BackendOutcome[Result]]]

  class Test extends MockHttpClient with MockAppConfig {

    val connector: BaseConnector = new BaseConnector {
      val http: HttpClient     = mockHttpClient
      val appConfig: AppConfig = mockAppConfig
    }

  }

  "BaseConnector" when {
    "making a HTTP request to downstream" must {
      val requiredHeaders: Seq[(String, String)] = Seq(
        "Environment"       -> "downstream-environment",
        "Authorization"     -> "Bearer downstream-token",
        "User-Agent"        -> "individual-calculations-api",
        "CorrelationId"     -> correlationId,
        "Gov-Test-Scenario" -> "DEFAULT"
      )

      val excludedHeaders: Seq[(String, String)] = Seq(
        "AnotherHeader" -> "HeaderValue"
      )

      downstreamTestHttpMethods(dummyHeaderCarrierConfig, requiredHeaders, excludedHeaders, Some(allowedDownstreamHeaders))

      "exclude all `otherHeaders` when no external service header allow-list is found" should {
        val requiredHeaders: Seq[(String, String)] = Seq(
          "Environment"   -> "downstream-environment",
          "Authorization" -> "Bearer downstream-token",
          "User-Agent"    -> "individual-calculations-api",
          "CorrelationId" -> correlationId
        )

        downstreamTestHttpMethods(dummyHeaderCarrierConfig, requiredHeaders, otherHeaders, None)
      }
    }

    "making a HTTP request to the backend" must {
      val requiredHeaders: Seq[(String, String)] = Seq(
        "Authorization" -> "Bearer user-token",
        "CorrelationId" -> correlationId
      )

      backendTestHttpMethods(dummyHeaderCarrierConfig, requiredHeaders)
    }
  }

  def downstreamTestHttpMethods(config: HeaderCarrier.Config,
                                requiredHeaders: Seq[(String, String)],
                                excludedHeaders: Seq[(String, String)],
                                downstreamEnvironmentHeaders: Option[Seq[String]]): Unit = {

    "complete the request successfully with the required headers" when {

      "DownstreamPost" in new Test {
        class DownstreamResult(override val value: Int) extends Result(value) with DownstreamResponse

        val downstreamUrl: Uri[DownstreamResult]                            = Uri[DownstreamResult](url)
        implicit val httpReads: HttpReads[BackendOutcome[DownstreamResult]] = mock[HttpReads[BackendOutcome[DownstreamResult]]]

        implicit val hc: HeaderCarrier                 = HeaderCarrier(otherHeaders = otherHeaders ++ Seq("Content-Type" -> "application/json"))
        val requiredHeadersPost: Seq[(String, String)] = requiredHeaders ++ Seq("Content-Type" -> "application/json")

        MockAppConfig.downstreamBaseUrl returns baseUrl
        MockAppConfig.downstreamToken returns "downstream-token"
        MockAppConfig.downstreamEnvironment returns "downstream-environment"
        MockAppConfig.downstreamEnvironmentHeaders returns downstreamEnvironmentHeaders

        MockedHttpClient
          .post(absoluteUrl, config, body, requiredHeadersPost, excludedHeaders)
          .returns(Future.successful(outcome))

        await(connector.post(body, downstreamUrl.value)) shouldBe outcome
      }
    }
  }

  def backendTestHttpMethods(config: HeaderCarrier.Config, requiredHeaders: Seq[(String, String)]): Unit = {

    "complete the request successfully with the required headers" when {
      implicit val hc: HeaderCarrier = HeaderCarrier().withExtraHeaders(headers = "Authorization" -> "Bearer user-token")

      "Get" in new Test {
        MockAppConfig.downstreamBaseUrl returns baseUrl
        MockAppConfig.downstreamToken returns "Bearer downstream-token"
        MockAppConfig.downstreamEnvironment returns "downstream-environment"
        MockAppConfig.downstreamEnvironmentHeaders returns Some(allowedDownstreamHeaders)

        MockedHttpClient
          .get(absoluteUrl, Seq(("key", "value")), config, requiredHeaders)
          .returns(Future.successful(outcome))

        await(connector.get(url, Seq(("key", "value")))) shouldBe outcome
      }

      "Post" in new Test {
        MockAppConfig.downstreamBaseUrl returns baseUrl
        MockAppConfig.downstreamToken returns "Bearer downstream-token"
        MockAppConfig.downstreamEnvironment returns "downstream-environment"
        MockAppConfig.downstreamEnvironmentHeaders returns Some(allowedDownstreamHeaders)

        MockedHttpClient
          .post(absoluteUrl, config, body, requiredHeaders)
          .returns(Future.successful(outcome))

        await(connector.post(body, url)) shouldBe outcome
      }
    }
  }

}
