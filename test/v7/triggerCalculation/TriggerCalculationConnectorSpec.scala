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

package v7.triggerCalculation

import play.api.libs.json.Json
import shared.connectors.ConnectorSpec
import shared.models.domain.{Nino, TaxYear}
import shared.models.outcomes.ResponseWrapper
import uk.gov.hmrc.http.StringContextOps
import v7.common.model.domain.{Either24or25Downstream, Post26Downstream, `in-year`, `intent-to-amend`, `intent-to-finalise`}
import v7.triggerCalculation.model.request.{Def1_TriggerCalculationRequestData, TriggerCalculationRequestData}
import v7.triggerCalculation.model.response.{Def1_TriggerCalculationResponse, TriggerCalculationResponse}

import java.net.URL
import scala.concurrent.Future

class TriggerCalculationConnectorSpec extends ConnectorSpec {

  val ninoString: String                   = "ZG903729C"
  val nino: Nino                           = Nino(ninoString)
  val response: TriggerCalculationResponse = Def1_TriggerCalculationResponse("someCalcId")

  def api1426Url(intentToFinalise: String): URL =
    url"$baseUrl/income-tax/nino/$ninoString/taxYear/2019/tax-calculation?crystallise=$intentToFinalise"

  def api1897Url(intentToFinalise: String): URL =
    url"$baseUrl/income-tax/calculation/23-24/$ninoString?crystallise=$intentToFinalise"

  def api2081Url(intentToFinalise: String): URL =
    url"$baseUrl/income-tax/25-26/calculation/$ninoString/$intentToFinalise"

  trait Test { self: ConnectorTest =>

    val connector: TriggerCalculationConnector = new TriggerCalculationConnector(http = mockHttpClient, appConfig = mockAppConfig)
  }

  "connector" when {
    "triggering an in-year calculation in a 2024 or 2025 tax year" must {
      val request: TriggerCalculationRequestData =
        Def1_TriggerCalculationRequestData(nino, TaxYear.fromMtd("2023-24"), `in-year`, Either24or25Downstream)
      makeRequestWithIFSEnabled(request, api1897Url("false"))
    }
    "triggering an intent-to-finalise calculation in a 2024 or 2025 tax year" must {
      val request: TriggerCalculationRequestData =
        Def1_TriggerCalculationRequestData(nino, TaxYear.fromMtd("2023-24"), `intent-to-finalise`, Either24or25Downstream)
      makeRequestWithIFSEnabled(request, api1897Url("true"))
    }
    "triggering an in-year calculation in a post 2026 tax year" must {
      val request: TriggerCalculationRequestData =
        Def1_TriggerCalculationRequestData(nino, TaxYear.fromMtd("2025-26"), `in-year`, Post26Downstream)
      makeRequestWithIFSEnabled(request, api2081Url("IY"))
    }
    "triggering an intent-to-finalise calculation in a post 2026 tax year" must {
      val request: TriggerCalculationRequestData =
        Def1_TriggerCalculationRequestData(nino, TaxYear.fromMtd("2025-26"), `intent-to-finalise`, Post26Downstream)
      makeRequestWithIFSEnabled(request, api2081Url("IF"))
    }
    "triggering an intent-to-amend calculation in a post 2026 tax year" must {
      val request: TriggerCalculationRequestData =
        Def1_TriggerCalculationRequestData(nino, TaxYear.fromMtd("2025-26"), `intent-to-amend`, Post26Downstream)
      makeRequestWithIFSEnabled(request, api2081Url("IA"))
    }

    def makeRequestWithIFSEnabled(request: TriggerCalculationRequestData, downstreamUrl: URL): Unit =
      s"send a request for a calculation type of ${request.calculationType} and return a calc ID" in new IfsTest with Test {
        val expectedOutcome: Right[Nothing, ResponseWrapper[TriggerCalculationResponse]] = Right(ResponseWrapper(correlationId, response))

        willPost(
          url = downstreamUrl,
          body = Json.parse("{}")
        ).returns(Future.successful(expectedOutcome))

        await(connector.triggerCalculation(request)).shouldBe(expectedOutcome)
      }
  }

}
