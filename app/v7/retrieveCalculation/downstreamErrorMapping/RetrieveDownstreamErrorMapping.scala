/*
 * Copyright 2024 HM Revenue & Customs
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

package v7.retrieveCalculation.downstreamErrorMapping

import shared.models.domain.TaxYear
import shared.models.errors._

import scala.math.Ordered.orderingToOrdered

sealed trait RetrieveDownstreamErrorMapping {
  def errorMap: Map[String, MtdError]
}

object RetrieveDownstreamErrorMapping {

  private val commonErrorsMap: Map[String, MtdError] = Map(
    "INVALID_TAXABLE_ENTITY_ID" -> NinoFormatError,
    "INVALID_CALCULATION_ID"    -> CalculationIdFormatError,
    "SERVER_ERROR"              -> InternalError,
    "SERVICE_UNAVAILABLE"       -> InternalError
  )

  case object PreTys extends RetrieveDownstreamErrorMapping {
    val errorMap: Map[String, MtdError] = commonErrorsMap ++ Map(
      "INVALID_CORRELATIONID" -> InternalError,
      "INVALID_CONSUMERID"    -> InternalError,
      "NO_DATA_FOUND"         -> NotFoundError
    )
  }

  // All other case objects were split in the case there is different mapping per tax year
  case object TaxYear2023 extends RetrieveDownstreamErrorMapping {
    val errorMap: Map[String, MtdError] = commonErrorsMap ++ Map(
      "INVALID_TAX_YEAR"       -> TaxYearFormatError,
      "INVALID_CORRELATION_ID" -> InternalError,
      "INVALID_CONSUMER_ID"    -> InternalError,
      "NOT_FOUND"              -> NotFoundError,
      "TAX_YEAR_NOT_SUPPORTED" -> RuleTaxYearNotSupportedError
    )
  }

  case object TaxYear2024 extends RetrieveDownstreamErrorMapping {
    val errorMap: Map[String, MtdError] = TaxYear2023.errorMap
  }

  case object TaxYear2025 extends RetrieveDownstreamErrorMapping {
    val errorMap: Map[String, MtdError] = TaxYear2023.errorMap
  }

  def errorMapFor(taxYear: TaxYear): RetrieveDownstreamErrorMapping = taxYear match {
    case ty if ty >= TaxYear.fromMtd("2025-26") => TaxYear2025
    case ty if ty == TaxYear.fromMtd("2024-25") => TaxYear2024
    case ty if ty == TaxYear.fromMtd("2023-24") => TaxYear2023
    case _                                      => PreTys
  }
}
