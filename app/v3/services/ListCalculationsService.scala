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
import v3.connectors.ListCalculationsConnector
import v3.controllers.EndpointLogContext
import v3.models.errors.{MtdError, _}
import v3.models.request.ListCalculationsRequest
import v3.models.response.listCalculations.ListCalculationsResponse.ListCalculations
import v3.support.DownstreamResponseMappingSupport

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class ListCalculationsService @Inject() (connector: ListCalculationsConnector) extends DownstreamResponseMappingSupport with Logging {

  def list(request: ListCalculationsRequest)(implicit
      hc: HeaderCarrier,
      ec: ExecutionContext,
      logContext: EndpointLogContext,
      correlationId: String): ServiceOutcome[ListCalculations] = {
    val result = for {
      responseWrapper <- EitherT(connector.list(request)).leftMap(mapDesErrors(mappingToMtdError))
    } yield responseWrapper

    result.value
  }

  private val mappingToMtdError: Map[String, MtdError] = Map(
    "INVALID_TAXABLE_ENTITY_ID" -> NinoFormatError,
    "INVALID_TAXYEAR"           -> TaxYearFormatError,
    "NOT_FOUND"                 -> NotFoundError,
    "SERVER_ERROR"              -> DownstreamError,
    "SERVICE_UNAVAILABLE"       -> DownstreamError,
    "UNMATCHED_STUB_ERROR"      -> RuleIncorrectGovTestScenarioError
  )

}
