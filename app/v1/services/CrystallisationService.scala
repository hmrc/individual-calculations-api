/*
 * Copyright 2020 HM Revenue & Customs
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

package v1.services

import cats.data.EitherT
import javax.inject.{Inject, Singleton}
import uk.gov.hmrc.http.HeaderCarrier
import utils.Logging
import v1.connectors.CrystallisationConnector
import v1.controllers.EndpointLogContext
import v1.models.errors._
import v1.models.outcomes.ResponseWrapper
import v1.models.request.crystallisation.CrystallisationRequest
import v1.models.response.intentToCrystallise.IntentToCrystalliseResponse
import v1.support.BackendResponseMappingSupport

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CrystallisationService @Inject()(connector: CrystallisationConnector) extends BackendResponseMappingSupport with Logging {

  def submitIntentToCrystallise(request: CrystallisationRequest)(
    implicit hc: HeaderCarrier,
    ec: ExecutionContext,
    logContext: EndpointLogContext): Future[Either[ErrorWrapper, ResponseWrapper[IntentToCrystalliseResponse]]] = {

    val result = for {
      desResponseWrapper <- EitherT(connector.submitIntentToCrystallise(request)).leftMap(mapDesErrors(desErrorMap))
    } yield desResponseWrapper

    result.value
  }

  private def desErrorMap: Map[String, MtdError] =
    Map(
      "INVALID_NINO" -> NinoFormatError,
      "INVALID_TAX_YEAR" -> TaxYearFormatError,
      "INVALID_TAX_CRYSTALLISE" -> DownstreamError,
      "INVALID_REQUEST" -> DownstreamError,
      "NO_SUBMISSION_EXIST" -> RuleNoSubmissionsExistError,
      "CONFLICT" -> RuleFinalDeclarationReceivedError,
      "SERVER_ERROR" -> DownstreamError,
      "SERVICE_UNAVAILABLE" -> DownstreamError,
      "NOT_FOUND" -> NotFoundError
    )
}


