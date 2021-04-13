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

import mocks.{MockAppConfig, MockHttpClient}
import v1.models.domain.CrystallisationRequestBody

import scala.concurrent.Future

class NrsProxyConnectorSpec extends ConnectorSpec {

  val nino: String = "AA111111A"
  val calculationId: String = "4557ecb5-fd32-48cc-81f5-e6acd1099f3c"

  val request: CrystallisationRequestBody = CrystallisationRequestBody(calculationId)

  class Test extends MockHttpClient with MockAppConfig {

    val connector: NrsProxyConnector = new NrsProxyConnector(
      http = mockHttpClient,
      appConfig = mockAppConfig
    )

    MockedAppConfig.mtdNrsProxyBaseUrl returns baseUrl
  }

  "NrsProxyConnector" when {
    "submit with valid data" should {
      "be successful" in new Test {

        MockedHttpClient
          .post(
            url = s"$baseUrl/mtd-api-nrs-proxy/$nino/itsa-crystallisation",
            body = request
          ).returns(Future.successful((): Unit))

        await(connector.submit(nino, request)) shouldBe (():Unit)
      }
    }
  }
}
