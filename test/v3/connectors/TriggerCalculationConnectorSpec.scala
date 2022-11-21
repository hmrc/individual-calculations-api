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

import play.api.libs.json.Json
import v3.models.domain.{Nino, TaxYear}
import v3.models.outcomes.ResponseWrapper
import v3.models.request.TriggerCalculationRequest
import v3.models.response.triggerCalculation.TriggerCalculationResponse

import scala.concurrent.Future

class TriggerCalculationConnectorSpec extends ConnectorSpec {

  val ninoString: String                   = "AA123456A"
  val nino: Nino                           = Nino(ninoString)
  val nonTysTaxYear: TaxYear               = TaxYear.fromMtd("2018-19")
  val tysTaxYear: TaxYear                  = TaxYear.fromMtd("2023-24")
  val response: TriggerCalculationResponse = TriggerCalculationResponse("someCalcId")

  trait Test { _: ConnectorTest =>

    val connector: TriggerCalculationConnector = new TriggerCalculationConnector(
      http = mockHttpClient,
      appConfig = mockAppConfig
    )

  }

  "connector" when {
    "triggering an in year calculation" must {
      makeRequestWith(finalDeclaration = false, "false")
    }

    "triggering for a final declaration" must {
      makeRequestWith(finalDeclaration = true, "true")
    }

    def makeRequestWith(finalDeclaration: Boolean, expectedCrystalliseParam: String): Unit =
      s"send a request with crystallise='$expectedCrystalliseParam' and return the calculation id" in new DesTest with Test {
        val request: TriggerCalculationRequest = TriggerCalculationRequest(nino, nonTysTaxYear, finalDeclaration)
        val outcome                            = Right(ResponseWrapper(correlationId, response))

        willPost(
          url = s"$baseUrl/income-tax/nino/$ninoString/taxYear/2019/tax-calculation?crystallise=$expectedCrystalliseParam",
          body = Json.parse("{}")
        ).returns(Future.successful(outcome))

        await(connector.triggerCalculation(request)) shouldBe outcome
      }

    "send a request and return the calculation id for a Tax Year Specific (TYS) tax year" in new TysIfsTest with Test {
      val request: TriggerCalculationRequest = TriggerCalculationRequest(nino, tysTaxYear, false)
      val outcome                            = Right(ResponseWrapper(correlationId, response))

      willPost(
        url = s"$baseUrl/income-tax/calculation/23-24/$ninoString?crystallise=false",
        body = Json.parse("{}")
      ).returns(Future.successful(outcome))

      await(connector.triggerCalculation(request)) shouldBe outcome
    }
  }

}
