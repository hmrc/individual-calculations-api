/*
 * Copyright 2025 HM Revenue & Customs
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

import play.api.Configuration
import shared.connectors.ConnectorSpec
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
      val outcome: Right[Nothing, ResponseWrapper[ListCalculationsResponse[Calculation]]] =
        Right(ResponseWrapper(correlationId, listCalculationsResponseModel))

      "a valid request with a Non-TYS tax year is supplied" in new HipTest with Test {
        willGet(
          s"$baseUrl/itsd/calculations/liability/${nino.nino}?taxYear=${taxYear.asDownstream}"
        ).returns(Future.successful(outcome))

        await(connector.list(request)) shouldBe outcome
      }

      "a valid request with a TYS tax year is supplied and feature switch is disabled (IFS enabled)" in new IfsTest with Test {
        MockedAppConfig.featureSwitchConfig returns Configuration("ifs_hip_migration_1896.enabled" -> false)

        willGet(
          s"$baseUrl/income-tax/view/calculations/liability/${tysTaxYear.asTysDownstream}/${nino.nino}"
        ).returns(Future.successful(outcome))

        await(connector.list(tysRequest)) shouldBe outcome
      }

      "a valid request with a TYS tax year is supplied and feature switch is enabled (HIP enabled)" in new HipTest with Test {
        MockedAppConfig.featureSwitchConfig returns Configuration("ifs_hip_migration_1896.enabled" -> true)

        willGet(
          s"$baseUrl/itsa/income-tax/v1/${tysTaxYear.asTysDownstream}/view/calculations/liability/$nino"
        ).returns(Future.successful(outcome))

        await(connector.list(tysRequest)) shouldBe outcome
      }
    }

    "return an error" when {
      val outcome: Left[ResponseWrapper[DownstreamErrors], Nothing] =
        Left(ResponseWrapper(correlationId, DownstreamErrors.single(DownstreamErrorCode("ERROR_CODE"))))

      "downstream returns an error for a request with a Non-TYS tax year" in new HipTest with Test {
        willGet(
          s"$baseUrl/itsd/calculations/liability/${nino.nino}?taxYear=${taxYear.asDownstream}"
        ).returns(Future.successful(outcome))

        await(connector.list(request)) shouldBe outcome
      }

      "downstream returns an error for a request with a TYS tax year and feature switch is disabled (IFS enabled)" in new IfsTest with Test {
        MockedAppConfig.featureSwitchConfig returns Configuration("ifs_hip_migration_1896.enabled" -> false)

        willGet(
          s"$baseUrl/income-tax/view/calculations/liability/${tysTaxYear.asTysDownstream}/${nino.nino}"
        ).returns(Future.successful(outcome))

        await(connector.list(tysRequest)) shouldBe outcome
      }

      "downstream returns an error for a request with a TYS tax year and feature switch is enabled (HIP enabled)" in new HipTest with Test {
        MockedAppConfig.featureSwitchConfig returns Configuration("ifs_hip_migration_1896.enabled" -> true)

        willGet(
          s"$baseUrl/itsa/income-tax/v1/${tysTaxYear.asTysDownstream}/view/calculations/liability/$nino"
        ).returns(Future.successful(outcome))

        await(connector.list(tysRequest)) shouldBe outcome
      }
    }
  }

}
