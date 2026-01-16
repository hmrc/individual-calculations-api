/*
 * Copyright 2026 HM Revenue & Customs
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

package v6.triggerCalculation

import play.api.libs.json.Json
import shared.connectors.ConnectorSpec
import shared.models.domain.{Nino, TaxYear}
import shared.models.outcomes.ResponseWrapper
import uk.gov.hmrc.http.StringContextOps
import v6.triggerCalculation.model.request.{Def1_TriggerCalculationRequestData, TriggerCalculationRequestData}
import v6.triggerCalculation.model.response.{Def1_TriggerCalculationResponse, TriggerCalculationResponse}

import scala.concurrent.Future

class TriggerCalculationConnectorSpec extends ConnectorSpec {

  val ninoString: String                   = "ZG903729C"
  val nino: Nino                           = Nino(ninoString)
  val response: TriggerCalculationResponse = Def1_TriggerCalculationResponse("someCalcId")

  trait Test {
    self: ConnectorTest =>

    val connector: TriggerCalculationConnector = new TriggerCalculationConnector(http = mockHttpClient, appConfig = mockAppConfig)
  }

  "connector" when {
    "triggering an in year calculation" must {

      "send a request with crystallise is false and return the calculation id" in new IfsTest with Test {

        val request: TriggerCalculationRequestData = Def1_TriggerCalculationRequestData(nino, TaxYear.fromMtd("2018-19"), false)
        val outcome: Right[Nothing, ResponseWrapper[TriggerCalculationResponse]] = Right(ResponseWrapper(correlationId, response))

        willPost(
          url = url"$baseUrl/income-tax/nino/$ninoString/taxYear/2019/tax-calculation?crystallise=false",
          body = Json.parse("{}")
        ).returns(Future.successful(outcome))

        await(connector.triggerCalculation(request)) shouldBe outcome
      }

      "send a request and return the calculation id for a Tax Year Specific (TYS) tax year" in new IfsTest with Test {
        val request: TriggerCalculationRequestData = Def1_TriggerCalculationRequestData(nino, TaxYear.fromMtd("2023-24"), finalDeclaration = false)
        val outcome: Right[Nothing, ResponseWrapper[TriggerCalculationResponse]] = Right(ResponseWrapper(correlationId, response))

        willPost(
          url = url"$baseUrl/income-tax/calculation/23-24/$ninoString?crystallise=false",
          body = Json.parse("{}")
        ).returns(Future.successful(outcome))

        await(connector.triggerCalculation(request)) shouldBe outcome
      }
    }

  }

}
