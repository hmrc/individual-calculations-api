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

package v7.triggerCalculation

import api.errors._
import shared.models.domain.{Nino, TaxYear}
import shared.models.errors._
import shared.models.outcomes.ResponseWrapper
import shared.services.ServiceSpec
import v7.common.model.domain.{Pre24Downstream, `intent-to-amend`}
import v7.triggerCalculation.model.request.{Def1_TriggerCalculationRequestData, TriggerCalculationRequestData}
import v7.triggerCalculation.model.response.{Def1_TriggerCalculationResponse, TriggerCalculationResponse}

import scala.concurrent.Future

class TriggerCalculationServiceSpec extends ServiceSpec {

  val nino: Nino = Nino("ZG903729C")

  val taxYear: TaxYear = TaxYear.fromDownstream("2020")

  val request: TriggerCalculationRequestData = Def1_TriggerCalculationRequestData(nino, taxYear, `intent-to-amend`, Pre24Downstream)

  val response: TriggerCalculationResponse = Def1_TriggerCalculationResponse("someCalcId")

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

          await(service.triggerCalculation(request)).shouldBe(Right(ResponseWrapper(correlationId, response)))
        }
      }
    }

    "unsuccessful" should {
      "map errors according to pre 2024 spec" when {
        def serviceError(downstreamErrorCode: String, error: MtdError): Unit =
          s"a $downstreamErrorCode error is returned from the service" in new Test {

            MockTriggerCalculationConnector
              .triggerCalculation(request)
              .returns(Future.successful(Left(ResponseWrapper(correlationId, DownstreamErrors.single(DownstreamErrorCode(downstreamErrorCode))))))

            await(service.triggerCalculation(request)).shouldBe(Left(ErrorWrapper(correlationId, error)))
          }

        val api1426downstreamErrorMap =
          List(
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

        val api1897downstreamErrorMap =
          List(
            ("INVALID_TAXABLE_ENTITY_ID", NinoFormatError),
            ("INVALID_TAX_YEAR", TaxYearFormatError),
            ("INVALID_CRYSTALLISE", FinalDeclarationFormatError),
            ("NO_VALID_INCOME_SOURCES", RuleIncomeSourcesInvalidError(UNPROCESSABLE_ENTITY)),
            ("NO_SUBMISSIONS_EXIST", RuleNoIncomeSubmissionsExistError),
            ("CHANGED_INCOME_SOURCES", RuleIncomeSourcesChangedError),
            ("OUTDATED_SUBMISSION", RuleRecentSubmissionsExistError),
            ("RESIDENCY_CHANGED", RuleResidencyChangedError),
            ("ALREADY_DECLARED", RuleFinalDeclarationReceivedError),
            ("PREMATURE_CRYSTALLISATION", RuleTaxYearNotEndedError),
            ("CALCULATION_EXISTS", RuleCalculationInProgressError),
            ("BVR_FAILURE", RuleBusinessValidationFailureError),
            ("TAX_YEAR_NOT_SUPPORTED", RuleTaxYearNotSupportedError),
            ("SERVER_ERROR", InternalError),
            ("SERVICE_UNAVAILABLE", InternalError),
            ("UNMATCHED_STUB_ERROR", RuleIncorrectGovTestScenarioError)
          )

        val api2081downstreamErrorMap =
          List(
            ("INVALID_TAXABLE_ENTITY_ID", NinoFormatError),
            ("INVALID_TAX_YEAR", TaxYearFormatError),
            ("INVALID_CALCULATION_TYPE", InternalError),
            ("NO_VALID_INCOME_SOURCES", RuleIncomeSourcesInvalidError(UNPROCESSABLE_ENTITY)),
            ("NO_SUBMISSIONS_EXIST", RuleNoIncomeSubmissionsExistError),
            ("ALREADY_DECLARED", RuleFinalDeclarationReceivedError),
            ("PREMATURE_FINALISATION", RulePrematureFinalisationError),
            ("CALCULATION_EXISTS", RuleCalculationInProgressError),
            ("BVR_FAILURE", RuleBusinessValidationFailureError),
            ("DECLARATION_NOT_RECEIVED", RuleDeclarationNotReceivedError),
            ("TAX_YEAR_NOT_SUPPORTED", RuleTaxYearNotSupportedError),
            ("OUTSIDE_AMENDMENT_WINDOW", RuleOutsideAmendmentWindowError),
            ("SERVER_ERROR", InternalError),
            ("SERVICE_UNAVAILABLE", InternalError),
            ("UNMATCHED_STUB_ERROR", RuleIncorrectGovTestScenarioError)
          )

        (api1426downstreamErrorMap ++ api1897downstreamErrorMap ++ api2081downstreamErrorMap).distinct
          .foreach(args => serviceError.tupled(args))
      }
    }

  }

}
