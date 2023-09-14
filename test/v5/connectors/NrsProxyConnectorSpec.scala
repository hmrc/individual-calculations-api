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

package v5.connectors

import api.connectors.ConnectorSpec
import mocks.{MockAppConfig, MockHttpClient}
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.http.UpstreamErrorResponse

import scala.concurrent.Future

class NrsProxyConnectorSpec extends ConnectorSpec {

  val nino: String         = "AA111111A"
  val notableEvent: String = "test-event"
  val body: JsValue        = Json.obj("field" -> "value")

  class Test extends MockHttpClient with MockAppConfig {

    val connector: NrsProxyConnector = new NrsProxyConnector(
      http = mockHttpClient,
      appConfig = mockAppConfig
    )

    MockAppConfig.mtdNrsProxyBaseUrl returns baseUrl
  }

  "submit" should {
    "return a success response" in new Test {
      val outcome: Either[UpstreamErrorResponse, Unit] = Right(())

      MockHttpClient
        .post(
          url = s"$baseUrl/mtd-api-nrs-proxy/$nino/$notableEvent",
          config = dummyHeaderCarrierConfig,
          body = body
        )
        .returns(Future.successful(Right(())))

      val result: Either[UpstreamErrorResponse, Unit] = await(connector.submitAsync(nino, notableEvent, body))
      result shouldBe outcome
    }
  }

}
