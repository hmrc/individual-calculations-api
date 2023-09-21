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

package v5.services

import api.controllers.RequestContext
import api.models.errors._
import api.services.{BaseService, ServiceOutcome}
import cats.implicits._
import v5.connectors.RetrieveCalculationConnector
import v5.models.request.RetrieveCalculationRequest
import v5.models.response.retrieveCalculation.RetrieveCalculationResponse

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RetrieveCalculationService @Inject() (connector: RetrieveCalculationConnector) extends BaseService {

  def retrieveCalculation(request: RetrieveCalculationRequest)(implicit
      ctx: RequestContext,
      ec: ExecutionContext): Future[ServiceOutcome[RetrieveCalculationResponse]] = {

    connector.retrieveCalculation(request).map(_.leftMap(mapDownstreamErrors(downstreamErrorMap)))

  }

  private val downstreamErrorMap: Map[String, MtdError] = {
    val errors: Map[String, MtdError] = Map(
      "INVALID_TAXABLE_ENTITY_ID" -> NinoFormatError,
      "INVALID_CALCULATION_ID"    -> CalculationIdFormatError,
      "INVALID_CORRELATIONID"     -> InternalError,
      "INVALID_CONSUMERID"        -> InternalError,
      "NO_DATA_FOUND"             -> NotFoundError,
      "SERVER_ERROR"              -> InternalError,
      "SERVICE_UNAVAILABLE"       -> InternalError,
      "UNMATCHED_STUB_ERROR"      -> RuleIncorrectGovTestScenarioError
    )

    val extraTysErrors: Map[String, MtdError] = Map(
      "INVALID_TAX_YEAR"       -> TaxYearFormatError,
      "INVALID_CORRELATION_ID" -> InternalError,
      "INVALID_CONSUMER_ID"    -> InternalError,
      "NOT_FOUND"              -> NotFoundError,
      "TAX_YEAR_NOT_SUPPORTED" -> RuleTaxYearNotSupportedError
    )

    errors ++ extraTysErrors
  }

}
