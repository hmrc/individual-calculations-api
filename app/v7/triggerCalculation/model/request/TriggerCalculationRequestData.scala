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

package v7.triggerCalculation.model.request

import shared.models.domain.{Nino, TaxYear}
import v7.triggerCalculation.schema.TriggerCalculationSchema
import v7.common.model.domain.{CalculationType, Post26Downstream, TysDownstream}

sealed trait TriggerCalculationRequestData {
  val nino: Nino
  val taxYear: TaxYear
  val calculationType: CalculationType
  val tysDownstream: TysDownstream

  val schema: TriggerCalculationSchema
}

case class Def1_TriggerCalculationRequestData(nino: Nino, taxYear: TaxYear, calculationType: CalculationType, tysDownstream: TysDownstream)
    extends TriggerCalculationRequestData {
  override val schema: TriggerCalculationSchema = TriggerCalculationSchema.Def1
}

object Def1_TriggerCalculationRequestData {

  def apply(nino: Nino, taxYear: TaxYear, calculationType: CalculationType): TriggerCalculationRequestData =
    Def1_TriggerCalculationRequestData(nino, taxYear, calculationType, Post26Downstream)

}
