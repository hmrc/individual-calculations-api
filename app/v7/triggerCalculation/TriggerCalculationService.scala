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

import shared.controllers.RequestContext
import shared.models.errors._
import api.errors._
import shared.services.{BaseService, ServiceOutcome}
import cats.implicits._
import v7.triggerCalculation.model.request.TriggerCalculationRequestData
import v7.triggerCalculation.model.response.TriggerCalculationResponse

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TriggerCalculationService @Inject() (connector: TriggerCalculationConnector) extends BaseService {

  def triggerCalculation(request: TriggerCalculationRequestData)(implicit
      ctx: RequestContext,
      ec: ExecutionContext): Future[ServiceOutcome[TriggerCalculationResponse]] = {

    connector.triggerCalculation(request).map(_.leftMap(mapDownstreamErrors(downstreamErrorMap)))
  }

  private val downstreamErrorMap: Map[String, MtdError] = {
    val errors = Map(
      "INVALID_NINO"                  -> NinoFormatError,
      "INVALID_TAX_YEAR"              -> TaxYearFormatError,
      "INVALID_CALCULATION_TYPE"      -> InternalError,
      "INVALID_TAX_CRYSTALLISE"       -> FinalDeclarationFormatError,
      "INVALID_REQUEST"               -> InternalError,
      "FORMAT_CALCULATION_TYPE"       -> FormatCalculationTypeError,
      "CALCULATION_TYPE_NOT_ALLOWED"  -> FormatCalculationTypeError,
      "NO_SUBMISSION_EXIST"           -> RuleNoIncomeSubmissionsExistError,
      "CONFLICT"                      -> RuleFinalDeclarationReceivedError,
      "OUTSIDE_AMENDMENT_WINDOW"      -> RuleOutsideAmendmentWindowError,
      "DECLARATION_NOT_RECEIVED"      -> RuleFinalDeclarationReceivedError,
      "SERVER_ERROR"                  -> InternalError,
      "SERVICE_UNAVAILABLE"           -> InternalError,
      "UNMATCHED_STUB_ERROR"          -> RuleIncorrectGovTestScenarioError
    )

    val extraTysErrors = Map(
      "INVALID_TAXABLE_ENTITY_ID" -> NinoFormatError,
      "INVALID_CRYSTALLISE"       -> FinalDeclarationFormatError,
      "NO_VALID_INCOME_SOURCES"   -> InternalError,
      "NO_SUBMISSIONS_EXIST"      -> RuleNoIncomeSubmissionsExistError,
      "CHANGED_INCOME_SOURCES"    -> RuleIncomeSourcesChangedError,
      "OUTDATED_SUBMISSION"       -> RuleRecentSubmissionsExistError,
      "RESIDENCY_CHANGED"         -> RuleResidencyChangedError,
      "ALREADY_DECLARED"          -> RuleFinalDeclarationReceivedError,
      "PREMATURE_CRYSTALLISATION" -> RuleTaxYearNotEndedError,
      "PREMATURE_FINALISATION"    -> RulePrematureFinalisationError,
      "CALCULATION_EXISTS"        -> RuleCalculationInProgressError,
      "BVR_FAILURE"               -> RuleBusinessValidationFailureError,
      "TAX_YEAR_NOT_SUPPORTED"    -> RuleTaxYearNotSupportedError
    )

    errors ++ extraTysErrors
  }

}
