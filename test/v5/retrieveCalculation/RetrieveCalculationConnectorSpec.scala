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

package v5.retrieveCalculation

import shared.connectors.{ConnectorSpec, DownstreamOutcome}
import shared.models.domain.{CalculationId, Nino, TaxYear}
import shared.models.errors.{DownstreamErrorCode, DownstreamErrors}
import shared.models.outcomes.ResponseWrapper
import org.scalamock.handlers.CallHandler
import v5.retrieveCalculation.def1.model.Def1_CalculationFixture
import v5.retrieveCalculation.models.request.{Def1_RetrieveCalculationRequestData, RetrieveCalculationRequestData}
import v5.retrieveCalculation.models.response.RetrieveCalculationResponse

import scala.concurrent.Future

class RetrieveCalculationConnectorSpec extends ConnectorSpec with Def1_CalculationFixture {

  val nino: Nino                   = Nino("ZG903729C")
  val calculationId: CalculationId = CalculationId("f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c")

  private val preTysTaxYear = TaxYear.fromMtd("2018-19")
  private val tysTaxYear    = TaxYear.fromMtd("2023-24")

  "retrieveCalculation" should {
    "return a valid response" when {
      "a valid request is supplied" in new IfsTest with Test {
        def taxYear: TaxYear = preTysTaxYear
        val outcome: Right[Nothing, ResponseWrapper[RetrieveCalculationResponse]] =
          Right(ResponseWrapper(correlationId, minimalCalculationR8bResponse))

        stubHttpResponse(outcome)

        await(connector.retrieveCalculation(request)) shouldBe outcome
      }

      "a valid request with Tax Year Specific tax year is supplied" in new IfsTest with Test {
        def taxYear: TaxYear = tysTaxYear
        val outcome: Right[Nothing, ResponseWrapper[RetrieveCalculationResponse]] =
          Right(ResponseWrapper(correlationId, minimalCalculationR8bResponse))

        stubTysHttpResponse(outcome)

        await(connector.retrieveCalculation(request)) shouldBe outcome
      }

      "response is an error" must {
        val downstreamErrorResponse: DownstreamErrors =
          DownstreamErrors.single(DownstreamErrorCode("SOME_ERROR"))
        val outcome = Left(ResponseWrapper(correlationId, downstreamErrorResponse))

        "return the error" in new IfsTest with Test {
          def taxYear: TaxYear = preTysTaxYear
          stubHttpResponse(outcome)

          val result: DownstreamOutcome[RetrieveCalculationResponse] =
            await(connector.retrieveCalculation(request))
          result shouldBe outcome
        }

        "return the error given a TYS tax year request" in new IfsTest with Test {
          def taxYear: TaxYear = tysTaxYear
          stubTysHttpResponse(outcome)

          val result: DownstreamOutcome[RetrieveCalculationResponse] =
            await(connector.retrieveCalculation(request))
          result shouldBe outcome
        }
      }
    }
  }

  trait Test { _: ConnectorTest =>
    def taxYear: TaxYear

    val request: RetrieveCalculationRequestData = Def1_RetrieveCalculationRequestData(nino, taxYear, calculationId)

    val connector: RetrieveCalculationConnector = new RetrieveCalculationConnector(
      http = mockHttpClient,
      appConfig = mockAppConfig
    )

    protected def stubHttpResponse(
        outcome: DownstreamOutcome[RetrieveCalculationResponse]): CallHandler[Future[DownstreamOutcome[RetrieveCalculationResponse]]]#Derived = {
      willGet(
        url = s"$baseUrl/income-tax/view/calculations/liability/${nino.nino}/$calculationId"
      )
        .returns(Future.successful(outcome))
    }

    protected def stubTysHttpResponse(
        outcome: DownstreamOutcome[RetrieveCalculationResponse]): CallHandler[Future[DownstreamOutcome[RetrieveCalculationResponse]]]#Derived = {
      willGet(
        url = s"$baseUrl/income-tax/view/calculations/liability/${taxYear.asTysDownstream}/${nino.nino}/$calculationId"
      )
        .returns(Future.successful(outcome))
    }

  }

}
