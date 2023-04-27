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

package v2.services

import cats.data.EitherT
import uk.gov.hmrc.http.HeaderCarrier
import utils.Logging
import v2.connectors.CrystallisationConnector
import v2.controllers.EndpointLogContext
import v2.models.errors._
import v2.models.outcomes.ResponseWrapper
import v2.models.request.crystallisation.CrystallisationRequest
import v2.models.response.common.DownstreamUnit
import v2.support.BackendResponseMappingSupport

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CrystallisationService @Inject() (connector: CrystallisationConnector) extends BackendResponseMappingSupport with Logging {

  def declareCrystallisation(request: CrystallisationRequest)(implicit
      hc: HeaderCarrier,
      ec: ExecutionContext,
      logContext: EndpointLogContext,
      correlationId: String): Future[Either[ErrorWrapper, ResponseWrapper[DownstreamUnit]]] = {

    val result = for {
      downstreamResponseWrapper <- EitherT(connector.declareCrystallisation(request)).leftMap(mapDownstreamErrors(downstreamErrorMap))
    } yield downstreamResponseWrapper

    result.value
  }

  private def downstreamErrorMap: Map[String, MtdError] =
    Map(
      "INVALID_IDTYPE"               -> DownstreamError,
      "INVALID_IDVALUE"              -> NinoFormatError,
      "INVALID_TAXYEAR"              -> TaxYearFormatError,
      "INVALID_CALCID"               -> CalculationIdFormatError,
      "NOT_FOUND"                    -> NotFoundError,
      "INCOME_SOURCES_CHANGED"       -> RuleIncomeSourcesChangedError,
      "RECENT_SUBMISSIONS_EXIST"     -> RuleRecentSubmissionsExistError,
      "RESIDENCY_CHANGED"            -> RuleResidencyChangedError,
      "INVALID_INCOME_SOURCES"       -> RuleIncomeSourcesInvalid,
      "INCOME_SUBMISSIONS_NOT_EXIST" -> RuleNoIncomeSubmissionsExistError,
      "BUSINESS_VALIDATION"          -> RuleSubmissionFailed,
      "FINAL_DECLARATION_RECEIVED"   -> RuleFinalDeclarationReceivedError,
      "SERVER_ERROR"                 -> DownstreamError,
      "SERVICE_UNAVAILABLE"          -> DownstreamError,
      "UNMATCHED_STUB_ERROR"         -> RuleIncorrectGovTestScenarioError
    )

}
