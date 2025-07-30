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

package v6.retrieveCalculation

import org.scalamock.handlers.CallHandler
import play.api.Configuration
import shared.connectors.{ConnectorSpec, DownstreamOutcome}
import shared.models.domain.{CalculationId, Nino, TaxYear}
import shared.models.errors.{DownstreamErrorCode, DownstreamErrors}
import shared.models.outcomes.ResponseWrapper
import uk.gov.hmrc.http.StringContextOps
import v6.retrieveCalculation.def1.model.Def1_CalculationFixture
import v6.retrieveCalculation.models.request.{Def1_RetrieveCalculationRequestData, RetrieveCalculationRequestData}
import v6.retrieveCalculation.models.response.RetrieveCalculationResponse

import java.net.URL
import scala.concurrent.Future

class RetrieveCalculationConnectorSpec extends ConnectorSpec with Def1_CalculationFixture {

  val nino: Nino                   = Nino("ZG903729C")
  val calculationId: CalculationId = CalculationId("f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c")

  private val preTysTaxYear = TaxYear.fromMtd("2018-19")
  private val tysTaxYear    = TaxYear.fromMtd("2023-24")

  "retrieveCalculation" should {
    "return a valid response" when {
      "a valid request with a Non-TYS tax year is supplied" in new IfsTest with Test {
        def taxYear: TaxYear = preTysTaxYear
        val outcome: Right[Nothing, ResponseWrapper[RetrieveCalculationResponse]] =
          Right(ResponseWrapper(correlationId, minimalCalculationR8bResponse))

        stubHttpResponse(outcome)

        await(connector.retrieveCalculation(request)).shouldBe(outcome)
      }

      "a valid request with a TYS tax year is supplied and feature switch is disabled (IFS enabled)" in new IfsTest with Test {
        def taxYear: TaxYear = tysTaxYear
        val outcome: Right[Nothing, ResponseWrapper[RetrieveCalculationResponse]] =
          Right(ResponseWrapper(correlationId, minimalCalculationR8bResponse))

        stubTysHttpResponse(isHipEnabled = false, outcome = outcome)

        await(connector.retrieveCalculation(request)).shouldBe(outcome)
      }

      "a valid request with a TYS tax year is supplied and feature switch is enabled (HIP enabled)" in new HipTest with Test {
        def taxYear: TaxYear = tysTaxYear
        val outcome: Right[Nothing, ResponseWrapper[RetrieveCalculationResponse]] =
          Right(ResponseWrapper(correlationId, minimalCalculationR8bResponse))

        stubTysHttpResponse(isHipEnabled = true, outcome = outcome)

        await(connector.retrieveCalculation(request)).shouldBe(outcome)
      }
    }

    "return an error" when {
      val downstreamErrorResponse: DownstreamErrors =
        DownstreamErrors.single(DownstreamErrorCode("SOME_ERROR"))
      val outcome = Left(ResponseWrapper(correlationId, downstreamErrorResponse))

      "downstream returns an error for a request with a Non-TYS tax year" in new IfsTest with Test {
        def taxYear: TaxYear = preTysTaxYear
        stubHttpResponse(outcome)

        val result: DownstreamOutcome[RetrieveCalculationResponse] =
          await(connector.retrieveCalculation(request))
        result.shouldBe(outcome)
      }

      "downstream returns an error for a request with a TYS tax year and feature switch is disabled (IFS enabled)" in new IfsTest with Test {
        def taxYear: TaxYear = tysTaxYear
        stubTysHttpResponse(isHipEnabled = false, outcome = outcome)

        val result: DownstreamOutcome[RetrieveCalculationResponse] =
          await(connector.retrieveCalculation(request))
        result.shouldBe(outcome)
      }

      "downstream returns an error for a request with a TYS tax year and feature switch is enabled (HIP enabled)" in new HipTest with Test {
        def taxYear: TaxYear = tysTaxYear
        stubTysHttpResponse(isHipEnabled = true, outcome = outcome)

        val result: DownstreamOutcome[RetrieveCalculationResponse] =
          await(connector.retrieveCalculation(request))
        result.shouldBe(outcome)
      }
    }
  }

  trait Test { self: ConnectorTest =>
    def taxYear: TaxYear

    val request: RetrieveCalculationRequestData = Def1_RetrieveCalculationRequestData(nino, taxYear, calculationId)

    val connector: RetrieveCalculationConnector = new RetrieveCalculationConnector(
      http = mockHttpClient,
      appConfig = mockAppConfig
    )

    protected def stubHttpResponse(
        outcome: DownstreamOutcome[RetrieveCalculationResponse]): CallHandler[Future[DownstreamOutcome[RetrieveCalculationResponse]]]#Derived = {
      willGet(
        url = url"$baseUrl/income-tax/view/calculations/liability/${nino.nino}/$calculationId"
      )
        .returns(Future.successful(outcome))
    }

    protected def stubTysHttpResponse(
        isHipEnabled: Boolean,
        outcome: DownstreamOutcome[RetrieveCalculationResponse]): CallHandler[Future[DownstreamOutcome[RetrieveCalculationResponse]]]#Derived = {

      val url: URL = if (isHipEnabled) {
        url"$baseUrl/itsa/income-tax/v1/${taxYear.asTysDownstream}/view/calculations/liability/$nino/$calculationId"
      } else {
        url"$baseUrl/income-tax/view/calculations/liability/${taxYear.asTysDownstream}/${nino.nino}/$calculationId"
      }

      MockedAppConfig.featureSwitchConfig returns Configuration("ifs_hip_migration_1885.enabled" -> isHipEnabled)

      willGet(url = url).returns(Future.successful(outcome))
    }

  }

}
