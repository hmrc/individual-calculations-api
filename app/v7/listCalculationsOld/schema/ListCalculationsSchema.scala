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

package v7.listCalculationsOld.schema

import shared.controllers.validators.resolvers.ResolveTaxYear
import shared.models.domain.TaxYear
import shared.schema.DownstreamReadable
import play.api.libs.json.Reads
import v7.listCalculationsOld.def1.model.response.{Calculation, Def1_Calculation}
import v7.listCalculationsOld.model.response.{Def1_ListCalculationsResponse, ListCalculationsResponse}

import java.time.Clock
import scala.math.Ordered.orderingToOrdered

sealed trait ListCalculationsSchema extends DownstreamReadable[ListCalculationsResponse[Calculation]]

object ListCalculationsSchema {

  case object Def1 extends ListCalculationsSchema {
    type DownstreamResp = Def1_ListCalculationsResponse[Def1_Calculation]
    val connectorReads: Reads[DownstreamResp] = Def1_ListCalculationsResponse.reads
  }

  private val defaultSchema = Def1

  def schemaFor(maybeTaxYear: Option[String])(implicit clock: Clock = Clock.systemUTC): ListCalculationsSchema = {
    maybeTaxYear
      .map(ResolveTaxYear.resolver)
      .flatMap(_.toOption.map(schemaFor))
      .getOrElse(schemaFor(TaxYear.currentTaxYear))
  }

  def schemaFor(taxYear: TaxYear): ListCalculationsSchema = {
    if (TaxYear.starting(2023) <= taxYear) {
      Def1
    } else {
      defaultSchema
    }
  }

}
