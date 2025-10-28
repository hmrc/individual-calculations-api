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

package v8.triggerCalculation

import api.errors.*
import cats.implicits.*
import play.api.http.Status.UNPROCESSABLE_ENTITY
import shared.controllers.RequestContext
import shared.models.errors.*
import shared.services.{BaseService, ServiceOutcome}
import v8.triggerCalculation.model.request.TriggerCalculationRequestData
import v8.triggerCalculation.model.response.TriggerCalculationResponse

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TriggerCalculationService @Inject() (connector: TriggerCalculationConnector) extends BaseService {

  def triggerCalculation(request: TriggerCalculationRequestData)(implicit
      ctx: RequestContext,
      ec: ExecutionContext): Future[ServiceOutcome[TriggerCalculationResponse]] = {

    val tysErrorMap = api1426downstreamErrorMap ++ api1897downstreamErrorMap ++ api2081downstreamErrorMap

    connector.triggerCalculation(request).map(_.leftMap(mapDownstreamErrors(tysErrorMap)))
  }

  private val api1426downstreamErrorMap: Map[String, MtdError] =
    Map(
      "INVALID_NINO"            -> NinoFormatError,
      "INVALID_TAX_YEAR"        -> TaxYearFormatError,
      "INVALID_TAX_CRYSTALLISE" -> FinalDeclarationFormatError,
      "INVALID_REQUEST"         -> InternalError,
      "NO_SUBMISSION_EXIST"     -> RuleNoIncomeSubmissionsExistError,
      "CONFLICT"                -> RuleFinalDeclarationReceivedError,
      "SERVER_ERROR"            -> InternalError,
      "SERVICE_UNAVAILABLE"     -> InternalError,
      "UNMATCHED_STUB_ERROR"    -> RuleIncorrectGovTestScenarioError
    )

  private val api1897downstreamErrorMap: Map[String, MtdError] =
    Map(
      "INVALID_TAXABLE_ENTITY_ID" -> NinoFormatError,
      "INVALID_TAX_YEAR"          -> TaxYearFormatError,
      "INVALID_CRYSTALLISE"       -> FinalDeclarationFormatError,
      "NO_VALID_INCOME_SOURCES"   -> RuleIncomeSourcesInvalidError(UNPROCESSABLE_ENTITY),
      "NO_SUBMISSIONS_EXIST"      -> RuleNoIncomeSubmissionsExistError,
      "CHANGED_INCOME_SOURCES"    -> RuleIncomeSourcesChangedError,
      "OUTDATED_SUBMISSION"       -> RuleRecentSubmissionsExistError,
      "RESIDENCY_CHANGED"         -> RuleResidencyChangedError,
      "ALREADY_DECLARED"          -> RuleFinalDeclarationReceivedError,
      "PREMATURE_CRYSTALLISATION" -> RuleTaxYearNotEndedError,
      "CALCULATION_EXISTS"        -> RuleCalculationInProgressError,
      "BVR_FAILURE"               -> RuleBusinessValidationFailureError,
      "TAX_YEAR_NOT_SUPPORTED"    -> RuleTaxYearNotSupportedError,
      "SERVER_ERROR"              -> InternalError,
      "SERVICE_UNAVAILABLE"       -> InternalError,
      "UNMATCHED_STUB_ERROR"      -> RuleIncorrectGovTestScenarioError
    )

  private val api2081downstreamErrorMap: Map[String, MtdError] =
    Map(
      "INVALID_TAXABLE_ENTITY_ID" -> NinoFormatError,
      "INVALID_TAX_YEAR"          -> TaxYearFormatError,
      "INVALID_CALCULATION_TYPE"  -> InternalError,
      "NO_VALID_INCOME_SOURCES"   -> RuleIncomeSourcesInvalidError(UNPROCESSABLE_ENTITY),
      "NO_SUBMISSIONS_EXIST"      -> RuleNoIncomeSubmissionsExistError,
      "ALREADY_DECLARED"          -> RuleFinalDeclarationReceivedError,
      "PREMATURE_FINALISATION"    -> RulePrematureFinalisationError,
      "CALCULATION_EXISTS"        -> RuleCalculationInProgressError,
      "BVR_FAILURE"               -> RuleBusinessValidationFailureError,
      "DECLARATION_NOT_RECEIVED"  -> RuleDeclarationNotReceivedError,
      "TAX_YEAR_NOT_SUPPORTED"    -> RuleTaxYearNotSupportedError,
      "OUTSIDE_AMENDMENT_WINDOW"  -> RuleOutsideAmendmentWindowError,
      "SERVER_ERROR"              -> InternalError,
      "SERVICE_UNAVAILABLE"       -> InternalError,
      "UNMATCHED_STUB_ERROR"      -> RuleIncorrectGovTestScenarioError
    )

}
