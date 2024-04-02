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

package v5.listCalculations

import api.controllers.RequestContext
import api.models
import api.models.errors._
import api.services.{BaseService, ServiceOutcome}
import cats.implicits._
import v5.listCalculations.model.request.ListCalculationsRequestData
import v5.listCalculations.model.response.Def1_ListCalculationsResponse.ListCalculations

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ListCalculationsService @Inject()(connector: ListCalculationsConnector) extends BaseService {

  def list(request: ListCalculationsRequestData)(implicit ctx: RequestContext, ec: ExecutionContext): Future[ServiceOutcome[ListCalculations]] = {

    connector.list(request).map(_.leftMap(mapDownstreamErrors(downstreamErrorMap)))

  }

  private val downstreamErrorMap: Map[String, MtdError] = {
    val errors = Map(
      "INVALID_TAXABLE_ENTITY_ID" -> NinoFormatError,
      "INVALID_TAXYEAR"           -> TaxYearFormatError,
      "NOT_FOUND"                 -> NotFoundError,
      "SERVER_ERROR"              -> models.errors.InternalError,
      "SERVICE_UNAVAILABLE"       -> models.errors.InternalError,
      "UNMATCHED_STUB_ERROR"      -> RuleIncorrectGovTestScenarioError
    )
    val extraTysErrors = Map(
      "INVALID_TAX_YEAR"       -> TaxYearFormatError,
      "INVALID_CORRELATION_ID" -> models.errors.InternalError,
      "TAX_YEAR_NOT_SUPPORTED" -> RuleTaxYearNotSupportedError
    )
    errors ++ extraTysErrors
  }

}
