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

package v5.retrieveCalculation.schema

import api.controllers.validators.resolvers.ResolveTaxYear
import api.models.domain.TaxYear
import api.schema.DownstreamReadable
import play.api.libs.json.Reads
import v5.retrieveCalculation.models.response.{Def1_RetrieveCalculationResponse, RetrieveCalculationResponse}

import scala.math.Ordered.orderingToOrdered

sealed trait RetrieveCalculationSchema extends DownstreamReadable[RetrieveCalculationResponse]

object RetrieveCalculationSchema {

  case object Def1 extends RetrieveCalculationSchema {
    type DownstreamResp = Def1_RetrieveCalculationResponse
    val connectorReads: Reads[DownstreamResp] = Def1_RetrieveCalculationResponse.reads
  }

  case object Def2 extends RetrieveCalculationSchema {
    type DownstreamResp = Def1_RetrieveCalculationResponse
    val connectorReads: Reads[DownstreamResp] = Def1_RetrieveCalculationResponse.reads
  }

  private val latestSchema = Def2

  def schemaFor(taxYear: String): RetrieveCalculationSchema =
    ResolveTaxYear(taxYear).toOption
      .map(schemaFor)
      .getOrElse(latestSchema)

  def schemaFor(taxYear: TaxYear): RetrieveCalculationSchema =
    if (taxYear <= TaxYear.starting(2023)) Def1
    else if (taxYear == TaxYear.starting(2024)) Def2
    else latestSchema

}
