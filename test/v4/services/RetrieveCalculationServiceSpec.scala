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

package v4.services

import api.models.domain.{CalculationId, Nino, TaxYear}
import api.models.errors.{CalculationIdFormatError, DownstreamErrorCode, DownstreamErrors, ErrorWrapper, InternalError, MtdError, NinoFormatError, NotFoundError, RuleIncorrectGovTestScenarioError, RuleTaxYearNotSupportedError, TaxYearFormatError}
import api.models.outcomes.ResponseWrapper
import api.services.ServiceSpec
import uk.gov.hmrc.http.HeaderCarrier
import v4.mocks.connectors.MockRetrieveCalculationConnector
import v4.models.request.RetrieveCalculationRequest
import v4.models.response.retrieveCalculation.{CalculationFixture, RetrieveCalculationResponse}

import scala.concurrent.Future

class RetrieveCalculationServiceSpec extends ServiceSpec with CalculationFixture {

  private val nino          = Nino("AA123456A")
  private val taxYear       = "2019-20"
  private val calculationId = "someCalcId"

  val request: RetrieveCalculationRequest   = RetrieveCalculationRequest(nino, TaxYear.fromMtd(taxYear), CalculationId(calculationId))
  val response: RetrieveCalculationResponse = minimalCalculationResponse

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

        await(service.retrieveCalculation(request)) shouldBe Right(ResponseWrapper(correlationId, response))
      }
    }

    "return error response" when {

      def serviceError(downstreamErrorCode: String, error: MtdError): Unit =
        s"a $downstreamErrorCode error is returned from the service" in new Test {

          MockRetrieveCalculationConnector
            .retrieveCalculation(request)
            .returns(Future.successful(Left(ResponseWrapper(correlationId, DownstreamErrors.single(DownstreamErrorCode(downstreamErrorCode))))))

          await(service.retrieveCalculation(request)) shouldBe Left(ErrorWrapper(correlationId, error))
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

      (errors ++ extraTysErrors).foreach(args => (serviceError _).tupled(args))
    }
  }

}
