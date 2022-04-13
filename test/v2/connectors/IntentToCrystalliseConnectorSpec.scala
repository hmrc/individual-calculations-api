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

package v2.connectors

import mocks.{MockAppConfig, MockHttpClient}
import uk.gov.hmrc.http.HeaderCarrier
import v2.models.domain.{DownstreamTaxYear, EmptyJsonBody, Nino}
import v2.models.outcomes.ResponseWrapper
import v2.models.request.intentToCrystallise.IntentToCrystalliseRequest
import v2.models.response.intentToCrystallise.IntentToCrystalliseResponse

import scala.concurrent.Future

class IntentToCrystalliseConnectorSpec extends ConnectorSpec {

  val nino: String               = "AA111111A"
  val taxYear: DownstreamTaxYear = DownstreamTaxYear.fromMtd(taxYear = "2021-22")
  val calculationId: String      = "4557ecb5-fd32-48cc-81f5-e6acd1099f3c"

  val request: IntentToCrystalliseRequest = IntentToCrystalliseRequest(
    Nino(nino),
    taxYear
  )

  val response: IntentToCrystalliseResponse = IntentToCrystalliseResponse(calculationId)

  class Test extends MockHttpClient with MockAppConfig {

    val connector: IntentToCrystalliseConnector = new IntentToCrystalliseConnector(
      http = mockHttpClient,
      appConfig = mockAppConfig
    )

    MockAppConfig.downstreamBaseUrl returns baseUrl
    MockAppConfig.downstreamToken returns "downstream-token"
    MockAppConfig.downstreamEnvironment returns "downstream-environment"
    MockAppConfig.downstreamEnvironmentHeaders returns Some(allowedDownstreamHeaders)
  }

  "IntentToCrystalliseConnector" when {
    ".submitIntent" should {
      "return a success upon HttpClient success" in new Test {
        val outcome = Right(ResponseWrapper(correlationId, response))

        implicit val hc: HeaderCarrier = HeaderCarrier(otherHeaders = otherHeaders ++ Seq("Content-Type" -> "application/json"))
        val requiredDesHeadersPost: Seq[(String, String)] = requiredDownstreamHeaders ++ Seq("Content-Type" -> "application/json")

        MockedHttpClient
          .post(
            url = s"$baseUrl/income-tax/nino/$nino/taxYear/${taxYear.value}/tax-calculation?crystallise=true",
            config = dummyHeaderCarrierConfig,
            body = EmptyJsonBody,
            requiredHeaders = requiredDesHeadersPost,
            excludedHeaders = Seq("AnotherHeader" -> "HeaderValue")
          )
          .returns(Future.successful(outcome))

        await(connector.submitIntentToCrystallise(request)) shouldBe outcome
      }
    }
  }

}
