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

import shared.models.domain.{CalculationId, Nino, TaxYear}
import shared.models.errors._
import shared.models.outcomes.ResponseWrapper
import shared.services.ServiceSpec
import uk.gov.hmrc.http.HeaderCarrier
import v5.retrieveCalculation.def1.model.Def1_CalculationFixture
import v5.retrieveCalculation.models.request.{Def1_RetrieveCalculationRequestData, RetrieveCalculationRequestData}
import v5.retrieveCalculation.models.response.RetrieveCalculationResponse

import scala.concurrent.Future

class RetrieveCalculationServiceSpec extends ServiceSpec with Def1_CalculationFixture {

  private val nino: Nino                   = Nino("ZG903729C")
  private val taxYear: TaxYear             = TaxYear.fromMtd("2019-20")
  private val calculationId: CalculationId = CalculationId("someCalcId")

  val request: RetrieveCalculationRequestData = Def1_RetrieveCalculationRequestData(nino, taxYear, calculationId)
  val response: RetrieveCalculationResponse   = minimalCalculationResponse

  trait Test extends MockRetrieveCalculationConnector {
    implicit val hc: HeaderCarrier = HeaderCarrier()

    val service = new RetrieveCalculationService(mockConnector)
  }

  "retrieveCalculation" should {
    "return a valid response" when {
      "a valid request is supplied" in new Test {
        MockRetrieveCalculationConnector
          .retrieveCalculation(request)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        await(service.retrieveCalculation(request)).shouldBe(Right(ResponseWrapper(correlationId, response)))
      }
    }

    "return error response" when {

      def serviceError(downstreamErrorCode: String, error: MtdError): Unit =
        s"a $downstreamErrorCode error is returned from the service" in new Test {

          MockRetrieveCalculationConnector
            .retrieveCalculation(request)
            .returns(Future.successful(Left(ResponseWrapper(correlationId, DownstreamErrors.single(DownstreamErrorCode(downstreamErrorCode))))))

          await(service.retrieveCalculation(request)).shouldBe(Left(ErrorWrapper(correlationId, error)))
        }

      // TODO ensure errors match
      val errors = Seq(
        ("INVALID_TAXABLE_ENTITY_ID", NinoFormatError),
        ("INVALID_CALCULATION_ID", CalculationIdFormatError),
        ("INVALID_CORRELATIONID", InternalError),
        ("INVALID_CONSUMERID", InternalError),
        ("NO_DATA_FOUND", NotFoundError),
        ("SERVER_ERROR", InternalError),
        ("SERVICE_UNAVAILABLE", InternalError),
        ("UNMATCHED_STUB_ERROR", RuleIncorrectGovTestScenarioError)
      )

      val extraTysErrors = Seq(
        ("INVALID_TAX_YEAR", TaxYearFormatError),
        ("INVALID_CORRELATION_ID", InternalError),
        ("INVALID_CONSUMER_ID", InternalError),
        ("NOT_FOUND", NotFoundError),
        ("TAX_YEAR_NOT_SUPPORTED", RuleTaxYearNotSupportedError)
      )

      (errors ++ extraTysErrors).foreach(args => serviceError.tupled(args))
    }
  }

}
