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

package v3.connectors

import api.connectors.{ConnectorSpec, DownstreamOutcome}
import api.models.domain.{Nino, TaxYear}
import api.models.errors.{DownstreamErrorCode, DownstreamErrors}
import api.models.outcomes.ResponseWrapper
import org.scalamock.handlers.CallHandler
import v3.models.request.RetrieveCalculationRequest
import v3.models.response.retrieveCalculation.{CalculationFixture, RetrieveCalculationResponse}

import scala.concurrent.Future

class RetrieveCalculationConnectorSpec extends ConnectorSpec with CalculationFixture {

  val nino: Nino            = Nino("AA123456A")
  val calculationId: String = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"

  private val preTysTaxYear = TaxYear.fromMtd("2018-19")
  private val tysTaxYear    = TaxYear.fromMtd("2023-24")

  "retrieveCalculation" should {
    "return a valid response" when {
      "a valid request is supplied" in new IfsTest with Test {
        def taxYear: TaxYear = preTysTaxYear
        val outcome          = Right(ResponseWrapper(correlationId, minimalCalculationResponse))

        stubHttpResponse(outcome)

        await(connector.retrieveCalculation(request)) shouldBe outcome
      }

      "a valid request with Tax Year Specific tax year is supplied" in new TysIfsTest with Test {
        def taxYear: TaxYear = tysTaxYear
        val outcome          = Right(ResponseWrapper(correlationId, minimalCalculationResponse))

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

        "return the error given a TYS tax year request" in new TysIfsTest with Test {
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

    val request: RetrieveCalculationRequest = RetrieveCalculationRequest(nino, taxYear, calculationId)

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
