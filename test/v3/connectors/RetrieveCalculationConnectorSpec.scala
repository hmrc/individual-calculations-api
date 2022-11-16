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

import v3.models.domain.{Nino, TaxYear}
import v3.models.outcomes.ResponseWrapper
import v3.models.request.RetrieveCalculationRequest
import v3.models.response.retrieveCalculation.CalculationFixture

import scala.concurrent.Future

class RetrieveCalculationConnectorSpec extends ConnectorSpec with CalculationFixture {

  val nino: Nino            = Nino("AA123456A")
  val taxYear               = "2018-19"
  val calculationId: String = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"

  val request: RetrieveCalculationRequest = RetrieveCalculationRequest(nino, TaxYear.fromMtd(taxYear), calculationId)

  trait Test { _: ConnectorTest =>
    val connector: RetrieveCalculationConnector = new RetrieveCalculationConnector(
      http = mockHttpClient,
      appConfig = mockAppConfig
    )
  }

  "retrieveCalculation" should {
    "return a valid response" when {
      val outcome                    = Right(ResponseWrapper(correlationId, minimalCalculationResponse))

      "a valid request with queryParams is supplied" in new IfsTest with Test {

        willGet(
            url = s"$baseUrl/income-tax/view/calculations/liability/${nino.nino}/$calculationId",
          )
          .returns(Future.successful(outcome))

        await(connector.retrieveCalculation(request)) shouldBe outcome
      }
    }
  }

}
