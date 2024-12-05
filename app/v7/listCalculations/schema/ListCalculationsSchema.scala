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

package v7.listCalculations.schema

import play.api.libs.json.Reads
import shared.controllers.validators.resolvers.ResolveTaxYear
import shared.models.domain.TaxYear
import shared.schema.DownstreamReadable
import v7.listCalculations.def1.model.response.Def1_Calculation
import v7.listCalculations.def2.model.response.Def2_Calculation
import v7.listCalculations.def3.model.response.Def3_Calculation
import v7.listCalculations.model.response.{Calculation, Def1_ListCalculationsResponse, Def2_ListCalculationsResponse, Def3_ListCalculationsResponse, ListCalculationsResponse}

import scala.math.Ordered.orderingToOrdered

sealed trait ListCalculationsSchema extends DownstreamReadable[ListCalculationsResponse[Calculation]]

object ListCalculationsSchema {

  case object Def1 extends ListCalculationsSchema {
    type DownstreamResp = Def1_ListCalculationsResponse[Def1_Calculation]
    val connectorReads: Reads[DownstreamResp] = Def1_ListCalculationsResponse.reads
  }

  case object Def2 extends ListCalculationsSchema {
    type DownstreamResp = Def2_ListCalculationsResponse[Def2_Calculation]
    val connectorReads: Reads[DownstreamResp] = Def2_ListCalculationsResponse.reads
  }

  case object Def3 extends ListCalculationsSchema {
    type DownstreamResp = Def3_ListCalculationsResponse[Def3_Calculation]
    val connectorReads: Reads[DownstreamResp] = Def3_ListCalculationsResponse.reads
  }

  private val latestSchema = Def3

  def schemaFor(taxYear: String): ListCalculationsSchema = {
    val resolveTaxYear = ResolveTaxYear(taxYear).toOption

    val schema = resolveTaxYear.map(schemaFor)

    schema.getOrElse(latestSchema)
  }

  private def schemaFor(taxYear: TaxYear): ListCalculationsSchema = {
    if (taxYear < TaxYear.ending(2023)) Def1
    else if (taxYear == TaxYear.ending(2024) || taxYear == TaxYear.ending(2025)) Def2
    else if (taxYear >= TaxYear.ending(2026)) Def3
    else latestSchema
  }

}
