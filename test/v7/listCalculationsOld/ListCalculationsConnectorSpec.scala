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

package v7.listCalculationsOld

import play.api.Configuration
import shared.connectors.{ConnectorSpec, DownstreamOutcome}
import shared.models.domain.{Nino, TaxYear}
import shared.models.errors.{DownstreamErrorCode, DownstreamErrors}
import shared.models.outcomes.ResponseWrapper
import uk.gov.hmrc.http.StringContextOps
import v7.listCalculationsOld.def1.model.Def1_ListCalculationsFixture
import v7.listCalculationsOld.def1.model.response.Calculation
import v7.listCalculationsOld.model.request.Def1_ListCalculationsRequestData
import v7.listCalculationsOld.model.response.ListCalculationsResponse

import scala.concurrent.Future

class ListCalculationsConnectorSpec extends ConnectorSpec with Def1_ListCalculationsFixture {

  val nino: Nino           = Nino("AA111111A")
  val taxYear2019: TaxYear = TaxYear.fromMtd("2018-19")
  val taxYear2024: TaxYear = TaxYear.fromMtd("2023-24")
  val taxYear2025: TaxYear = TaxYear.fromMtd("2024-25")
  val taxYear2026: TaxYear = TaxYear.fromMtd("2025-26")

  def request(taxYear: TaxYear): Def1_ListCalculationsRequestData = Def1_ListCalculationsRequestData(nino, taxYear)

  trait Test { _: ConnectorTest =>

    val connector: ListCalculationsConnector = new ListCalculationsConnector(
      http = mockHttpClient,
      appConfig = mockAppConfig
    )

  }

  "ListCalculationsConnector" should {
    "return successful response" when {
      "Non-TYS tax year query param is passed and feature switch is disabled (DES enabled)" in new DesTest with Test {
        val outcome = Right(ResponseWrapper(correlationId, listCalculationsResponseModel))

        MockedAppConfig.featureSwitchConfig returns Configuration("des_hip_migration_1404.enabled" -> false)

        willGet(
          url"$baseUrl/income-tax/list-of-calculation-results/$nino?taxYear=2019"
        )
          .returns(Future.successful(outcome))

        await(connector.list(request(taxYear2019))) shouldBe outcome
      }

      "Non-TYS tax year query param is passed and feature switch is enabled (HIP  enabled)" in new HipTest with Test {
        val outcome = Right(ResponseWrapper(correlationId, listCalculationsResponseModel))

        MockedAppConfig.featureSwitchConfig returns Configuration("des_hip_migration_1404.enabled" -> true)

        willGet(
          url"$baseUrl/itsd/calculations/liability/$nino?taxYear=2019"
        )
          .returns(Future.successful(outcome))

        await(connector.list(request(taxYear2019))) shouldBe outcome
      }

      "2024 tax year query param is passed" in new IfsTest with Test {
        val outcome = Right(ResponseWrapper(correlationId, listCalculationsResponseModel))

        willGet(
          url"$baseUrl/income-tax/${taxYear2024.asTysDownstream}/view/calculations-summary/$nino"
        )
          .returns(Future.successful(outcome))

        await(connector.list(request(taxYear2024))) shouldBe outcome
      }

      "2025 tax year query param is passed" in new IfsTest with Test {
        val outcome = Right(ResponseWrapper(correlationId, listCalculationsResponseModel))

        willGet(
          url"$baseUrl/income-tax/${taxYear2025.asTysDownstream}/view/calculations-summary/$nino"
        )
          .returns(Future.successful(outcome))

        await(connector.list(request(taxYear2025))) shouldBe outcome
      }

      "2026 or later tax year query param is passed" in new IfsTest with Test {
        val outcome = Right(ResponseWrapper(correlationId, listCalculationsResponseModel))

        willGet(
          url"$baseUrl/income-tax/${taxYear2026.asTysDownstream}/view/$nino/calculations-summary"
        )
          .returns(Future.successful(outcome))

        await(connector.list(request(taxYear2026))) shouldBe outcome
      }
    }

    "return the expected result" when {
      "an error is received and feature switch disabled (DES enabled)" in new DesTest with Test {
        val outcome = Left(ResponseWrapper(correlationId, DownstreamErrors.single(DownstreamErrorCode("ERROR_CODE"))))

        MockedAppConfig.featureSwitchConfig returns Configuration("des_hip_migration_1404.enabled" -> false)

        willGet(
          url"$baseUrl/income-tax/list-of-calculation-results/${nino.nino}?taxYear=2019"
        )
          .returns(Future.successful(outcome))

        private val result: DownstreamOutcome[ListCalculationsResponse[Calculation]] = await(connector.list(request(taxYear2019)))
        result shouldBe outcome
      }

      "an error is received and feature switch enabled (HIP enabled)" in new HipTest with Test {
        val outcome = Left(ResponseWrapper(correlationId, DownstreamErrors.single(DownstreamErrorCode("ERROR_CODE"))))

        MockedAppConfig.featureSwitchConfig returns Configuration("des_hip_migration_1404.enabled" -> true)

        willGet(
          url"$baseUrl/itsd/calculations/liability/${nino.nino}?taxYear=2019"
        )
          .returns(Future.successful(outcome))

        private val result: DownstreamOutcome[ListCalculationsResponse[Calculation]] = await(connector.list(request(taxYear2019)))
        result shouldBe outcome
      }
    }
  }

}
