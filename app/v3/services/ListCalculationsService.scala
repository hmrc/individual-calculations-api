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

import cats.data.EitherT
import v3.connectors.ListCalculationsConnector
import v3.controllers.RequestContext
import v3.models.errors.{MtdError, _}
import v3.models.request.ListCalculationsRequest
import v3.models.response.listCalculations.ListCalculationsResponse.ListCalculations

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class ListCalculationsService @Inject() (connector: ListCalculationsConnector) extends BaseService {

  def list(request: ListCalculationsRequest)(implicit
                                             ctx: RequestContext,
                                             ec: ExecutionContext): ServiceOutcome[ListCalculations] = {

    EitherT(connector.list(request)).leftMap(mapDownstreamErrors(downstreamErrorMap)).value
  }

  private val downstreamErrorMap: Map[String, MtdError] = {
    val errors = Map(
      "INVALID_TAXABLE_ENTITY_ID" -> NinoFormatError,
      "INVALID_TAXYEAR"           -> TaxYearFormatError,
      "NOT_FOUND"                 -> NotFoundError,
      "SERVER_ERROR"              -> InternalError,
      "SERVICE_UNAVAILABLE"       -> InternalError,
      "UNMATCHED_STUB_ERROR"      -> RuleIncorrectGovTestScenarioError
    )
    val extraTysErrors = Map(
      "INVALID_TAX_YEAR"       -> TaxYearFormatError,
      "INVALID_CORRELATION_ID" -> InternalError,
      "TAX_YEAR_NOT_SUPPORTED" -> RuleTaxYearNotSupportedError
    )
    errors ++ extraTysErrors
  }

}
