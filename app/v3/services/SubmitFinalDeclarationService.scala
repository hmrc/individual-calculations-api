/*
 * Copyright 2022 HM Revenue & Customs
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

import cats.data.EitherT
import uk.gov.hmrc.http.HeaderCarrier
import utils.Logging
import v3.connectors.SubmitFinalDeclarationConnector
import v3.controllers.EndpointLogContext
import v3.models.errors._
import v3.models.outcomes.ResponseWrapper
import v3.models.request.SubmitFinalDeclarationRequest
import v3.support.DownstreamResponseMappingSupport

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SubmitFinalDeclarationService @Inject() (connector: SubmitFinalDeclarationConnector) extends DownstreamResponseMappingSupport with Logging {

  def submitFinalDeclaration(request: SubmitFinalDeclarationRequest)(implicit
      hc: HeaderCarrier,
      ec: ExecutionContext,
      logContext: EndpointLogContext,
      correlationId: String): Future[Either[ErrorWrapper, ResponseWrapper[Unit]]] = {

    val result = for {
      downstreamResponseWrapper <- EitherT(connector.submitFinalDeclaration(request)).leftMap(mapDesErrors(downstreamErrorMap))
    } yield downstreamResponseWrapper

    result.value
  }

  private def downstreamErrorMap: Map[String, MtdError] =
    Map(
      "INVALID_IDTYPE"               -> InternalError,
      "INVALID_IDVALUE"              -> NinoFormatError,
      "INVALID_TAXYEAR"              -> TaxYearFormatError,
      "INVALID_CALCID"               -> CalculationIdFormatError,
      "NOT_FOUND"                    -> NotFoundError,
      "INCOME_SOURCES_CHANGED"       -> RuleIncomeSourcesChangedError,
      "RECENT_SUBMISSIONS_EXIST"     -> RuleRecentSubmissionsExistError,
      "RESIDENCY_CHANGED"            -> RuleResidencyChangedError,
      "INVALID_INCOME_SOURCES"       -> RuleIncomeSourcesInvalidError,
      "INCOME_SUBMISSIONS_NOT_EXIST" -> RuleNoIncomeSubmissionsExistError,
      "BUSINESS_VALIDATION"          -> RuleSubmissionFailedError,
      "FINAL_DECLARATION_RECEIVED"   -> RuleFinalDeclarationReceivedError,
      "SERVER_ERROR"                 -> InternalError,
      "SERVICE_UNAVAILABLE"          -> InternalError,
      "UNMATCHED_STUB_ERROR"         -> RuleIncorrectGovTestScenarioError
    )

}
