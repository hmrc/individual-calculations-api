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

import cats.implicits._
import v3.connectors.SubmitFinalDeclarationConnector
import v3.controllers.RequestContext
import v3.models.errors._
import v3.models.outcomes.ResponseWrapper
import v3.models.request.SubmitFinalDeclarationRequest

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SubmitFinalDeclarationService @Inject() (connector: SubmitFinalDeclarationConnector) extends BaseService {

  def submitFinalDeclaration(request: SubmitFinalDeclarationRequest)(implicit
      ctx: RequestContext,
      ec: ExecutionContext): Future[Either[ErrorWrapper, ResponseWrapper[Unit]]] = {

    connector.submitFinalDeclaration(request).map(_.leftMap(mapDownstreamErrors(downstreamErrorMap)))
  }

  private val downstreamErrorMap: Map[String, MtdError] =
    Map(
      "INVALID_TAXABLE_ENTITY_ID"      -> NinoFormatError,
      "INVALID_TAX_YEAR"               -> TaxYearFormatError,
      "INVALID_CALCID"                 -> CalculationIdFormatError,
      "INVALID_CORRELATION_ID"         -> InternalError,
      "NOT_FOUND"                      -> NotFoundError,
      "INCOME_SOURCES_CHANGED"         -> RuleIncomeSourcesChangedError,
      "RECENT_SUBMISSIONS_EXIST"       -> RuleRecentSubmissionsExistError,
      "RESIDENCY_CHANGED"              -> RuleResidencyChangedError,
      "FINAL_DECLARATION_RECEIVED"     -> RuleFinalDeclarationReceivedError,
      "INVALID_INCOME_SOURCES"         -> RuleIncomeSourcesInvalidError,
      "INCOME_SUBMISSIONS_NOT_EXIST"   -> RuleNoIncomeSubmissionsExistError,
      "BUSINESS_VALIDATION"            -> RuleSubmissionFailedError,
      "CRYSTALLISATION_TAX_YEAR_ERROR" -> RuleFinalDeclarationTaxYearError,
      "CRYSTALLISATION_IN_PROGRESS"    -> RuleFinalDeclarationInProgressError,
      "TAX_YEAR_NOT_SUPPORTED"         -> RuleTaxYearNotSupportedError,
      "SERVER_ERROR"                   -> InternalError,
      "SERVICE_UNAVAILABLE"            -> InternalError,
      "UNMATCHED_STUB_ERROR"           -> RuleIncorrectGovTestScenarioError
    )

}
