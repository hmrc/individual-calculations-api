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

import uk.gov.hmrc.http.HttpReads
import v1.mocks.{MockAppConfig, MockHttpClient}
import v1.models.outcomes.ResponseWrapper

import scala.concurrent.Future

class BaseConnectorSpec extends ConnectorSpec {

  // WLOG
  case class Result(value: Int)

  // WLOG
  val body = "body"

  val outcome = Right(ResponseWrapper(correlationId, Result(2)))

  val url = "some/url?param=value"
  val absoluteUrl = s"$baseUrl/$url"

  implicit val httpReads: HttpReads[BackendOutcome[Result]] = mock[HttpReads[BackendOutcome[Result]]]

  class Test extends MockHttpClient with MockAppConfig {
    val connector: BaseConnector = new BaseConnector {
      val http = mockHttpClient
      val appConfig = mockAppConfig
    }
    MockedAppConfig.backendBaseUrl returns baseUrl
  }

  "post" must {
    "posts with the required backend headers and returns the result" in new Test {
      MockedHttpClient
        .post(absoluteUrl, body, "Authorization" -> s"Bearer user-token")
        .returns(Future.successful(outcome))

      await(connector.post(body, url)) shouldBe outcome
    }
  }

  "get" must {
    "get with the required backend headers and return the result with query parameters" in new Test {
      MockedHttpClient
        .get(absoluteUrl, Seq(("key", "value")), "Authorization" -> s"Bearer user-token")
        .returns(Future.successful(outcome))

      await(connector.get(url, Seq(("key", "value")))) shouldBe outcome
    }
  }
}
