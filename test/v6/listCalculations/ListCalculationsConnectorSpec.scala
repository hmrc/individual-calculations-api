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

package v6.listCalculations

import shared.connectors.{ConnectorSpec, DownstreamOutcome}
import shared.models.domain.{Nino, TaxYear}
import shared.models.errors.{DownstreamErrorCode, DownstreamErrors}
import shared.models.outcomes.ResponseWrapper
import v6.listCalculations.def1.model.Def1_ListCalculationsFixture
import v6.listCalculations.def1.model.response.Calculation
import v6.listCalculations.model.request.Def1_ListCalculationsRequestData
import v6.listCalculations.model.response.ListCalculationsResponse

import scala.concurrent.Future

class ListCalculationsConnectorSpec extends ConnectorSpec with Def1_ListCalculationsFixture {

  val nino: Nino          = Nino("AA111111A")
  val taxYear: TaxYear    = TaxYear.fromMtd("2018-19")
  val tysTaxYear: TaxYear = TaxYear.fromMtd("2023-24")

  val request: Def1_ListCalculationsRequestData    = Def1_ListCalculationsRequestData(nino, taxYear)
  val tysRequest: Def1_ListCalculationsRequestData = Def1_ListCalculationsRequestData(nino, tysTaxYear)

  trait Test { _: ConnectorTest =>

    val connector: ListCalculationsConnector = new ListCalculationsConnector(
      http = mockHttpClient,
      appConfig = mockAppConfig
    )

  }

  "ListCalculationsConnector" should {
    "return successful response" when {
      "Non-TYS tax year query param is passed" in new DesTest with Test {
        val outcome = Right(ResponseWrapper(correlationId, listCalculationsResponseModel))

        willGet(
          s"$baseUrl/income-tax/list-of-calculation-results/${nino.nino}?taxYear=2019"
        )
          .returns(Future.successful(outcome))

        await(connector.list(request)) shouldBe outcome
      }

      "TYS tax year query param is passed" in new IfsTest with Test {
        val outcome = Right(ResponseWrapper(correlationId, listCalculationsResponseModel))

        willGet(
          s"$baseUrl/income-tax/view/calculations/liability/${tysTaxYear.asTysDownstream}/${nino.nino}"
        )
          .returns(Future.successful(outcome))

        await(connector.list(tysRequest)) shouldBe outcome
      }
    }

    "an error is received" must {
      "return the expected result" in new DesTest with Test {
        val outcome = Left(ResponseWrapper(correlationId, DownstreamErrors.single(DownstreamErrorCode("ERROR_CODE"))))

        willGet(
          s"$baseUrl/income-tax/list-of-calculation-results/${nino.nino}?taxYear=2019"
        )
          .returns(Future.successful(outcome))

        private val result: DownstreamOutcome[ListCalculationsResponse[Calculation]] = await(connector.list(request))
        result shouldBe outcome
      }
    }
  }

}
