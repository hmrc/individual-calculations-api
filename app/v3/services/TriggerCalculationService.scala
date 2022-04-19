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
import cats.implicits._
import uk.gov.hmrc.http.HeaderCarrier
import utils.Logging
import v3.connectors.TriggerCalculationConnector
import v3.controllers.EndpointLogContext
import v3.models.errors._
import v3.models.outcomes.ResponseWrapper
import v3.models.request.TriggerCalculationRequest
import v3.models.response.triggerCalculation.TriggerCalculationResponse
import v3.support.DownstreamResponseMappingSupport

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TriggerCalculationService @Inject() (connector: TriggerCalculationConnector) extends DownstreamResponseMappingSupport with Logging {

  def triggerCalculation(request: TriggerCalculationRequest)(implicit
      hc: HeaderCarrier,
      ec: ExecutionContext,
      logContext: EndpointLogContext,
      correlationId: String): Future[Either[ErrorWrapper, ResponseWrapper[TriggerCalculationResponse]]] = {
    val result = for {
      responseWrapper <- EitherT(connector.triggerCalculation(request)).leftMap(mapDesErrors(mappingToMtdError))
    } yield responseWrapper

    result.value
  }

  private val mappingToMtdError: Map[String, MtdError] = Map(
    "INVALID_NINO"            -> NinoFormatError,
    "INVALID_TAX_YEAR"        -> TaxYearFormatError,
    "INVALID_TAX_CRYSTALLISE" -> FormatFinalDeclaration,
    "INVALID_REQUEST"         -> DownstreamError,
    "NO_SUBMISSION_EXIST"     -> RuleNoSubmissionsExistError,
    "CONFLICT"                -> RuleFinalDeclarationReceivedError,
    "SERVER_ERROR"            -> DownstreamError,
    "SERVICE_UNAVAILABLE"     -> DownstreamError
  )

}
