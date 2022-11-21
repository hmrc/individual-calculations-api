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

import v3.fixtures.ListCalculationsFixture
import v3.models.domain.{Nino, TaxYear}
import v3.models.errors.{DownstreamErrorCode, DownstreamErrors}
import v3.models.outcomes.ResponseWrapper
import v3.models.request.ListCalculationsRequest

import scala.concurrent.Future

class ListCalculationsConnectorSpec extends ConnectorSpec with ListCalculationsFixture {

  val nino: Nino          = Nino("AA111111A")
  val taxYear: TaxYear    = TaxYear.fromMtd("2018-19")
  val tysTaxYear: TaxYear = TaxYear.fromMtd("2023-24")

  val request: ListCalculationsRequest    = ListCalculationsRequest(nino, Some(taxYear))
  val tysRequest: ListCalculationsRequest = ListCalculationsRequest(nino, Some(tysTaxYear))

  trait Test { _: ConnectorTest =>

    val connector: ListCalculationsConnector = new ListCalculationsConnector(
      http = mockHttpClient,
      appConfig = mockAppConfig
    )

  }

  "ListCalculationsConnector" when {
    "a successful response is received" must {
      "return the expected non TYS result" in new DesTest with Test {
        val outcome = Right(ResponseWrapper(correlationId, listCalculationsResponseModel))

        willGet(
          s"$baseUrl/income-tax/list-of-calculation-results/${nino.nino}?taxYear=2019"
        )
          .returns(Future.successful(outcome))

        await(connector.list(request)) shouldBe outcome
      }
      "return the expected TYS result" in new TysIfsTest with Test {
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
          s"$baseUrl/income-tax/list-of-calculation-results/${nino.nino}"
        )
          .returns(Future.successful(outcome))

        await(connector.list(request.copy(taxYear = None))) shouldBe outcome
      }
    }
  }

}
