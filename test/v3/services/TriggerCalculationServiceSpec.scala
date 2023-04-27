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

package v3.services

import api.models.domain.{Nino, TaxYear}
import api.models.errors.{DownstreamErrorCode, DownstreamErrors, ErrorWrapper, FinalDeclarationFormatError, InternalError, MtdError, NinoFormatError, RuleBusinessValidationFailureError, RuleCalculationInProgressError, RuleFinalDeclarationReceivedError, RuleIncomeSourcesChangedError, RuleIncorrectGovTestScenarioError, RuleNoIncomeSubmissionsExistError, RuleRecentSubmissionsExistError, RuleResidencyChangedError, RuleTaxYearNotEndedError, RuleTaxYearNotSupportedError, TaxYearFormatError}
import api.models.outcomes.ResponseWrapper
import api.services.ServiceSpec
import v3.mocks.connectors.MockTriggerCalculationConnector
import v3.models.request.TriggerCalculationRequest
import v3.models.response.triggerCalculation.TriggerCalculationResponse

import scala.concurrent.Future

class TriggerCalculationServiceSpec extends ServiceSpec {

  val nino: Nino = Nino("AA123456A")

  val taxYear: TaxYear = TaxYear.fromDownstream("2020")

  val request: TriggerCalculationRequest = TriggerCalculationRequest(nino, taxYear, finalDeclaration = true)

  val response: TriggerCalculationResponse = TriggerCalculationResponse("someCalcId")

  trait Test extends MockTriggerCalculationConnector {
    val service = new TriggerCalculationService(mockConnector)
  }

  "triggerCalculation" when {
    "finalDeclaration parameter specified" when {
      "successful" must {
        "use the parameters and return the response" in new Test {

          MockTriggerCalculationConnector
            .triggerCalculation(request)
            .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

          await(service.triggerCalculation(request)) shouldBe Right(ResponseWrapper(correlationId, response))
        }
      }
    }

    "unsuccessful" should {
      "map errors according to spec" when {
        def serviceError(downstreamErrorCode: String, error: MtdError): Unit =
          s"a $downstreamErrorCode error is returned from the service" in new Test {

            MockTriggerCalculationConnector
              .triggerCalculation(request)
              .returns(Future.successful(Left(ResponseWrapper(correlationId, DownstreamErrors.single(DownstreamErrorCode(downstreamErrorCode))))))

            await(service.triggerCalculation(request)) shouldBe Left(ErrorWrapper(correlationId, error))
          }

        val errors = List(
          ("INVALID_NINO", NinoFormatError),
          ("INVALID_TAX_YEAR", TaxYearFormatError),
          ("INVALID_TAX_CRYSTALLISE", FinalDeclarationFormatError),
          ("INVALID_REQUEST", InternalError),
          ("NO_SUBMISSION_EXIST", RuleNoIncomeSubmissionsExistError),
          ("CONFLICT", RuleFinalDeclarationReceivedError),
          ("SERVER_ERROR", InternalError),
          ("SERVICE_UNAVAILABLE", InternalError),
          ("UNMATCHED_STUB_ERROR", RuleIncorrectGovTestScenarioError)
        )

        val extraTysErrors = List(
          ("INVALID_TAXABLE_ENTITY_ID", NinoFormatError),
          ("INVALID_CRYSTALLISE", FinalDeclarationFormatError),
          ("INVALID_CORRELATION_ID", InternalError),
          ("NO_VALID_INCOME_SOURCES", InternalError),
          ("NO_SUBMISSIONS_EXIST", RuleNoIncomeSubmissionsExistError),
          ("INVALID_CALCULATION_ID", InternalError),
          ("CHANGED_INCOME_SOURCES", RuleIncomeSourcesChangedError),
          ("OUTDATED_SUBMISSION", RuleRecentSubmissionsExistError),
          ("RESIDENCY_CHANGED", RuleResidencyChangedError),
          ("ALREADY_DECLARED", RuleFinalDeclarationReceivedError),
          ("PREMATURE_CRYSTALLISATION", RuleTaxYearNotEndedError),
          ("CALCULATION_EXISTS", RuleCalculationInProgressError),
          ("BVR_FAILURE", RuleBusinessValidationFailureError),
          ("TAX_YEAR_NOT_SUPPORTED", RuleTaxYearNotSupportedError)
        )

        (errors ++ extraTysErrors).foreach(args => (serviceError _).tupled(args))
      }
    }

  }

}
