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

package v8.triggerCalculation.schema

import shared.controllers.validators.resolvers.ResolveTaxYear
import shared.models.domain.TaxYear
import shared.schema.DownstreamReadable
import play.api.libs.json.Reads
import v8.triggerCalculation.model.response.{Def1_TriggerCalculationResponse, TriggerCalculationResponse}

import scala.math.Ordered.orderingToOrdered

sealed trait TriggerCalculationSchema extends DownstreamReadable[TriggerCalculationResponse]

object TriggerCalculationSchema {

  case object Def1 extends TriggerCalculationSchema {
    type DownstreamResp = Def1_TriggerCalculationResponse
    val connectorReads: Reads[DownstreamResp] = Def1_TriggerCalculationResponse.reads
  }

  private val defaultSchema = Def1

  def schemaFor(taxYear: String): TriggerCalculationSchema =
    ResolveTaxYear(taxYear).toOption
      .map(schemaFor)
      .getOrElse(defaultSchema)

  def schemaFor(taxYear: TaxYear): TriggerCalculationSchema =
    if (TaxYear.starting(2025) <= taxYear) {
      Def1
    } else {
      defaultSchema
    }

}
