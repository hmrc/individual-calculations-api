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
import v3.connectors.RetrieveCalculationConnector
import v3.controllers.EndpointLogContext
import v3.models.errors._
import v3.models.outcomes.ResponseWrapper
import v3.models.request.RetrieveCalculationRequest
import v3.models.response.retrieveCalculation.RetrieveCalculationResponse
import v3.support.DownstreamResponseMappingSupport

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RetrieveCalculationService @Inject() (connector: RetrieveCalculationConnector) extends DownstreamResponseMappingSupport with Logging {

  def retrieveCalculation(request: RetrieveCalculationRequest)(implicit
      hc: HeaderCarrier,
      ec: ExecutionContext,
      logContext: EndpointLogContext,
      correlationId: String): Future[Either[ErrorWrapper, ResponseWrapper[RetrieveCalculationResponse]]] = {

    val result = for {
      desResponseWrapper <- EitherT(connector.retrieveCalculation(request)).leftMap(mapDesErrors(mapDownstreamErrors))
    } yield desResponseWrapper

    result.value
  }

  private val mapDownstreamErrors: Map[String, MtdError] = Map(
    "INVALID_TAXABLE_ENTITY_ID" -> NinoFormatError,
    "INVALID_CALCULATION_ID"    -> CalculationIdFormatError,
    "INVALID_CORRELATIONID"     -> InternalError,
    "INVALID_CONSUMERID"        -> InternalError,
    "NO_DATA_FOUND"             -> NotFoundError,
    "SERVER_ERROR"              -> InternalError,
    "SERVICE_UNAVAILABLE"       -> InternalError,
    "UNMATCHED_STUB_ERROR"      -> RuleIncorrectGovTestScenarioError
  )

}
