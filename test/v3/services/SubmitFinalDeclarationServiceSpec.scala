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
import api.models.errors.{CalculationIdFormatError, DownstreamErrorCode, DownstreamErrors, ErrorWrapper, InternalError, MtdError, NinoFormatError, NotFoundError, RuleFinalDeclarationInProgressError, RuleFinalDeclarationReceivedError, RuleFinalDeclarationTaxYearError, RuleIncomeSourcesChangedError, RuleIncomeSourcesInvalidError, RuleIncorrectGovTestScenarioError, RuleNoIncomeSubmissionsExistError, RuleRecentSubmissionsExistError, RuleResidencyChangedError, RuleSubmissionFailedError, RuleTaxYearNotSupportedError, TaxYearFormatError}
import api.models.outcomes.ResponseWrapper
import v3.mocks.connectors.MockSubmitFinalDeclarationConnector
import v3.models.request.SubmitFinalDeclarationRequest

import scala.concurrent.Future

class SubmitFinalDeclarationServiceSpec extends ServiceSpec {

  trait Test extends MockSubmitFinalDeclarationConnector {

    val service: SubmitFinalDeclarationService = new SubmitFinalDeclarationService(
      mockSubmitFinalDeclarationConnector
    )

  }

  "SubmitFinalDeclarationService" when {
    val nino: Nino            = Nino("AA123456A")
    val taxYear: TaxYear      = TaxYear.fromMtd("2019-20")
    val calculationId: String = "a1e8057e-fbbc-47a8-a8b4-78d9f015c253"

    val request: SubmitFinalDeclarationRequest = SubmitFinalDeclarationRequest(
      nino,
      taxYear,
      calculationId
    )

    "SubmitFinalDeclaration" must {
      "return correct result for a success" in new Test {
        val outcome: Right[Nothing, ResponseWrapper[Unit]] = Right(ResponseWrapper(correlationId, ()))

        MockSubmitFinalDeclarationConnector
          .submitFinalDeclaration(request)
          .returns(Future.successful(outcome))

        val result: Either[ErrorWrapper, ResponseWrapper[Unit]] = await(service.submitFinalDeclaration(request))
        result shouldBe outcome
      }
    }

    "map errors according to spec" when {
      def serviceError(downstreamErrorCode: String, error: MtdError): Unit =
        s"a $downstreamErrorCode error is returned from the service" in new Test {

          MockSubmitFinalDeclarationConnector
            .submitFinalDeclaration(request)
            .returns(Future.successful(Left(ResponseWrapper(correlationId, DownstreamErrors.single(DownstreamErrorCode(downstreamErrorCode))))))

          val result: Either[ErrorWrapper, ResponseWrapper[Unit]] = await(service.submitFinalDeclaration(request))
          result shouldBe Left(ErrorWrapper(correlationId, error))
        }

      val errors = Seq(
        ("INVALID_TAXABLE_ENTITY_ID", NinoFormatError),
        ("INVALID_TAX_YEAR", TaxYearFormatError),
        ("INVALID_CALCID", CalculationIdFormatError),
        ("INVALID_CORRELATION_ID", InternalError),
        ("NOT_FOUND", NotFoundError),
        ("INCOME_SOURCES_CHANGED", RuleIncomeSourcesChangedError),
        ("RECENT_SUBMISSIONS_EXIST", RuleRecentSubmissionsExistError),
        ("RESIDENCY_CHANGED", RuleResidencyChangedError),
        ("FINAL_DECLARATION_RECEIVED", RuleFinalDeclarationReceivedError),
        ("INVALID_INCOME_SOURCES", RuleIncomeSourcesInvalidError),
        ("INCOME_SUBMISSIONS_NOT_EXIST", RuleNoIncomeSubmissionsExistError),
        ("BUSINESS_VALIDATION", RuleSubmissionFailedError),
        ("CRYSTALLISATION_TAX_YEAR_ERROR", RuleFinalDeclarationTaxYearError),
        ("CRYSTALLISATION_IN_PROGRESS", RuleFinalDeclarationInProgressError),
        ("TAX_YEAR_NOT_SUPPORTED", RuleTaxYearNotSupportedError),
        ("SERVER_ERROR", InternalError),
        ("SERVICE_UNAVAILABLE", InternalError),
        ("UNMATCHED_STUB_ERROR", RuleIncorrectGovTestScenarioError)
      )

      val extraDesErrors = Seq(
        ("INVALID_IDTYPE", InternalError),
        ("INVALID_IDVALUE", NinoFormatError),
        ("INVALID_TAXYEAR", TaxYearFormatError)
      )

      (errors ++ extraDesErrors).foreach(args => (serviceError _).tupled(args))
    }
  }

}
