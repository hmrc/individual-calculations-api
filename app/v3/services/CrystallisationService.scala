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
import javax.inject.{Inject, Singleton}
import uk.gov.hmrc.http.HeaderCarrier
import utils.Logging
import v3.connectors.CrystallisationConnector
import v3.controllers.EndpointLogContext
import v3.models.errors._
import v3.models.outcomes.ResponseWrapper
import v3.models.request.crystallisation.CrystallisationRequest
import v3.models.response.common.DesUnit
import v3.support.BackendResponseMappingSupport

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CrystallisationService @Inject()(connector: CrystallisationConnector) extends BackendResponseMappingSupport with Logging {

  def declareCrystallisation(request: CrystallisationRequest)(
    implicit hc: HeaderCarrier,
    ec: ExecutionContext,
    logContext: EndpointLogContext,
    correlationId: String): Future[Either[ErrorWrapper, ResponseWrapper[DesUnit]]] = {

    val result = for {
      desResponseWrapper <- EitherT(connector.declareCrystallisation(request)).leftMap(mapDesErrors(desErrorMap))
    } yield desResponseWrapper

    result.value
  }

  private def desErrorMap: Map[String, MtdError] =
    Map(
      "INVALID_IDTYPE" -> DownstreamError,
      "INVALID_IDVALUE" -> NinoFormatError,
      "INVALID_TAXYEAR" -> TaxYearFormatError,
      "INVALID_CALCID" -> CalculationIdFormatError,
      "NOT_FOUND" -> NotFoundError,
      "INCOME_SOURCES_CHANGED" -> RuleIncomeSourcesChangedError,
      "RECENT_SUBMISSIONS_EXIST" -> RuleRecentSubmissionsExistError,
      "RESIDENCY_CHANGED" -> RuleResidencyChangedError,
      "INVALID_INCOME_SOURCES" -> RuleIncomeSourcesInvalid,
      "INCOME_SUBMISSIONS_NOT_EXIST" -> RuleNoIncomeSubmissionsExistError,
      "BUSINESS_VALIDATION" -> RuleSubmissionFailed,
      "FINAL_DECLARATION_RECEIVED" -> RuleFinalDeclarationReceivedError,
      "SERVER_ERROR" -> DownstreamError,
      "SERVICE_UNAVAILABLE" -> DownstreamError
    )
}