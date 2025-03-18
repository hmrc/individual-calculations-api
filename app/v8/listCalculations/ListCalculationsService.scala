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

package v8.listCalculations

import cats.implicits._
import shared.controllers.RequestContext
import shared.models
import shared.models.errors._
import shared.services.{BaseService, ServiceOutcome}
import v8.listCalculations.model.request.ListCalculationsRequestData
import v8.listCalculations.model.response.{Calculation, ListCalculationsResponse}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ListCalculationsService @Inject() (connector: ListCalculationsConnector) extends BaseService {

  def list(request: ListCalculationsRequestData)(implicit
      ctx: RequestContext,
      ec: ExecutionContext): Future[ServiceOutcome[ListCalculationsResponse[Calculation]]] = {

    connector.list(request).map(_.leftMap(mapDownstreamErrors(downstreamErrorMap)))

  }

  private val downstreamErrorMap: Map[String, MtdError] = {
    val desErrors = Map(
      "INVALID_TAXABLE_ENTITY_ID" -> NinoFormatError,
      "INVALID_TAXYEAR"           -> TaxYearFormatError,
      "NOT_FOUND"                 -> NotFoundError,
      "SERVER_ERROR"              -> models.errors.InternalError,
      "SERVICE_UNAVAILABLE"       -> models.errors.InternalError,
      "UNMATCHED_STUB_ERROR"      -> RuleIncorrectGovTestScenarioError
    )

    val extraTysDesErrors = Map(
      "NO_DATA_FOUND"          -> NotFoundError,
      "INVALID_TAX_YEAR"       -> TaxYearFormatError,
      "INVALID_CORRELATION_ID" -> models.errors.InternalError,
      "TAX_YEAR_NOT_SUPPORTED" -> RuleTaxYearNotSupportedError
    )

    val hipErrors = Map(
      "1215" -> NinoFormatError,
      "1117" -> TaxYearFormatError,
      "5010" -> NotFoundError
    )

    desErrors ++ extraTysDesErrors ++ hipErrors
  }

}
