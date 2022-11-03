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

import mocks.{MockAppConfig, MockHttpClient}
import play.api.libs.json.Json
import v3.models.domain.{Nino, TaxYear}
import v3.models.outcomes.ResponseWrapper
import v3.models.request.TriggerCalculationRequest
import v3.models.response.triggerCalculation.TriggerCalculationResponse

import scala.concurrent.Future

class TriggerCalculationConnectorSpec extends ConnectorSpec {

  val ninoString: String                   = "AA123456A"
  val nino: Nino                           = Nino(ninoString)
  val downstreamTaxYear                    = "2019"
  val taxYear: TaxYear                     = TaxYear.fromDownstream(downstreamTaxYear)
  val response: TriggerCalculationResponse = TriggerCalculationResponse("someCalcId")

  class Test extends MockHttpClient with MockAppConfig {

    val connector: TriggerCalculationConnector = new TriggerCalculationConnector(
      http = mockHttpClient,
      appConfig = mockAppConfig
    )

    MockAppConfig.desBaseUrl returns baseUrl
    MockAppConfig.desToken returns "des-token"
    MockAppConfig.desEnvironment returns "des-environment"
    MockAppConfig.desEnvironmentHeaders returns Some(allowedDesHeaders)
  }

  "connector" when {
    "triggering an in year calculation" must {
      makeRequestWith(finalDeclaration = false, "false")
    }

    "triggering for a final declaration" must {
      makeRequestWith(finalDeclaration = true, "true")
    }

    def makeRequestWith(finalDeclaration: Boolean, expectedCrystalliseParam: String): Unit =
      s"send a request with crystallise='$expectedCrystalliseParam' and return the calculation id" in new Test {
        val request: TriggerCalculationRequest = TriggerCalculationRequest(nino, taxYear, finalDeclaration)
        val outcome                            = Right(ResponseWrapper(correlationId, response))

        MockHttpClient
          .post(
            url = s"$baseUrl/income-tax/nino/$ninoString/taxYear/$downstreamTaxYear/tax-calculation?crystallise=$expectedCrystalliseParam",
            config = dummyHeaderCarrierConfig,
            body = Json.parse("{}"),
            requiredHeaders = requiredDesHeaders
          )
          .returns(Future.successful(outcome))

        await(connector.triggerCalculation(request)) shouldBe outcome
      }
  }

}
