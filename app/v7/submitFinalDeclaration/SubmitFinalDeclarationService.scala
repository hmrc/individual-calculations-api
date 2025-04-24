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

package v7.submitFinalDeclaration

import api.errors._
import shared.controllers.RequestContext
import shared.models.errors._
import shared.services.{BaseService, ServiceOutcome}
import v7.submitFinalDeclaration.model.request.SubmitFinalDeclarationRequestData

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SubmitFinalDeclarationService @Inject() (connector: SubmitFinalDeclarationConnector, nrsService: NrsProxyService) extends BaseService {

  def submitFinalDeclaration(nino: String, request: SubmitFinalDeclarationRequestData)(implicit
      ctx: RequestContext,
      ec: ExecutionContext): Future[ServiceOutcome[Unit]] = {
    connector.submitFinalDeclaration(request).map {
      case Right(success) =>
        nrsService.updateNrs(nino, request)
        Right(success)
      case Left(error) => Left(mapDownstreamErrors(downstreamErrorMap)(error))
    }
  }

  private val downstreamErrorMap: Map[String, MtdError] = {
    val errors: Map[String, MtdError] = Map(
      "INVALID_TAXABLE_ENTITY_ID"      -> NinoFormatError,
      "INVALID_TAX_YEAR"               -> TaxYearFormatError,
      "INVALID_CALCULATION_TYPE"       -> InternalError,
      "INVALID_CALCULATION_ID"         -> CalculationIdFormatError,
      "INVALID_CORRELATION_ID"         -> InternalError,
      "NOT_FOUND"                      -> NotFoundError,
      "OUTSIDE_AMENDMENT_WINDOW"       -> RuleOutsideAmendmentWindowError,
      "INCOME_SOURCES_CHANGED"         -> RuleIncomeSourcesChangedError,
      "RECENT_SUBMISSIONS_EXIST"       -> RuleRecentSubmissionsExistError,
      "RESIDENCY_CHANGED"              -> RuleResidencyChangedError,
      "FINAL_DECLARATION_RECEIVED"     -> RuleFinalDeclarationReceivedError,
      "INVALID_INCOME_SOURCES"         -> RuleIncomeSourcesInvalidError,
      "INCOME_SUBMISSIONS_NOT_EXIST"   -> RuleNoIncomeSubmissionsExistError,
      "BUSINESS_VALIDATION"            -> RuleSubmissionFailedError,
      "CRYSTALLISATION_TAX_YEAR_ERROR" -> RuleFinalDeclarationTaxYearError,
      "FINALISATION_BEFORE_TAX_YEAR_ENDS_ERROR" -> RuleFinalDeclarationTaxYearError,
      "CRYSTALLISATION_IN_PROGRESS"    -> RuleFinalDeclarationInProgressError,
      "FINALISATION_IN_PROGRESS"       -> RuleFinalDeclarationInProgressError,
      "TAX_YEAR_NOT_SUPPORTED"         -> RuleTaxYearNotSupportedError,
      "SERVER_ERROR"                   -> InternalError,
      "SERVICE_UNAVAILABLE"            -> InternalError,
      "UNMATCHED_STUB_ERROR"           -> RuleIncorrectGovTestScenarioError
    )

    val extraDesErrors: Map[String, MtdError] = Map(
      "INVALID_IDTYPE"  -> InternalError,
      "INVALID_IDVALUE" -> NinoFormatError,
      "INVALID_TAXYEAR" -> TaxYearFormatError
    )

    errors ++ extraDesErrors

  }

}
