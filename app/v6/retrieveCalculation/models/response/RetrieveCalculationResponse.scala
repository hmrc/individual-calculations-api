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

package v6.retrieveCalculation.models.response

import config.CalculationsFeatureSwitches
import play.api.libs.json.{Json, OWrites, Reads}
import shared.models.domain.TaxYear
import v6.retrieveCalculation._

import scala.math.Ordered.orderingToOrdered

sealed trait RetrieveCalculationResponse {
  def intentToSubmitFinalDeclaration: Boolean

  def finalDeclaration: Boolean

  def hasErrors: Boolean
}

object RetrieveCalculationResponse {

  implicit val writes: OWrites[RetrieveCalculationResponse] = {
    case def1: Def1_RetrieveCalculationResponse => Json.toJsObject(def1)
    case def2: Def2_RetrieveCalculationResponse => Json.toJsObject(def2)
  }

}

case class Def1_RetrieveCalculationResponse(
    metadata: def1.model.response.metadata.Metadata,
    inputs: def1.model.response.inputs.Inputs,
    calculation: Option[def1.model.response.calculation.Calculation],
    messages: Option[def1.model.response.messages.Messages]
) extends RetrieveCalculationResponse {

  override def intentToSubmitFinalDeclaration: Boolean = metadata.intentToSubmitFinalDeclaration

  override def finalDeclaration: Boolean = metadata.finalDeclaration

  override def hasErrors: Boolean = {
    for {
      messages <- messages
      errors   <- messages.errors
    } yield errors.nonEmpty
  }.getOrElse(false)

  def adjustFields(featureSwitches: CalculationsFeatureSwitches, taxYear: String): Def1_RetrieveCalculationResponse = {
    import featureSwitches._

    def updateModelR8b(response: Def1_RetrieveCalculationResponse): Def1_RetrieveCalculationResponse =
      if (isR8bSpecificApiEnabled) response else response.withoutR8bSpecificUpdates

    def updateModelCl290(response: Def1_RetrieveCalculationResponse): Def1_RetrieveCalculationResponse =
      if (isCl290Enabled) response else response.withoutTaxTakenOffTradingIncome

    def updateModelBasicRateDivergence(taxYear: String, response: Def1_RetrieveCalculationResponse): Def1_RetrieveCalculationResponse = {
      if (isBasicRateDivergenceEnabled && TaxYear.fromMtd(taxYear) >= TaxYear.fromMtd("2024-25")) response
      else response.withoutBasicRateDivergenceUpdates
    }

    val responseMaybeWithoutR8b   = updateModelR8b(this)
    val responseMaybeWithoutCl290 = updateModelCl290(responseMaybeWithoutR8b)
    updateModelBasicRateDivergence(taxYear, responseMaybeWithoutCl290)

  }

  def withoutR8bSpecificUpdates: Def1_RetrieveCalculationResponse =
    this.withoutBasicExtension.withoutOffPayrollWorker.withoutTotalAllowanceAndDeductions

  def withoutBasicExtension: Def1_RetrieveCalculationResponse =
    Def1_RetrieveCalculationResponse(metadata, inputs, calculation.map(_.withoutBasicExtension), messages)

  def withoutOffPayrollWorker: Def1_RetrieveCalculationResponse =
    Def1_RetrieveCalculationResponse(metadata, inputs, calculation.map(_.withoutOffPayrollWorker), messages)

  def withoutTotalAllowanceAndDeductions: Def1_RetrieveCalculationResponse =
    Def1_RetrieveCalculationResponse(metadata, inputs, calculation.map(_.withoutTotalAllowanceAndDeductions), messages)

  // find where its used
  def withoutUnderLowerProfitThreshold: Def1_RetrieveCalculationResponse =
    Def1_RetrieveCalculationResponse(metadata, inputs, calculation.map(_.withoutUnderLowerProfitThreshold), messages)

  def withoutTaxTakenOffTradingIncome: Def1_RetrieveCalculationResponse = {
    Def1_RetrieveCalculationResponse(metadata, inputs, calculation.map(_.withoutTaxTakenOffTradingIncome).filter(_.isDefined), messages)
  }

  def withoutBasicRateDivergenceUpdates: Def1_RetrieveCalculationResponse =
    this.withoutGiftAidTaxReductionWhereBasicRateDiffers.withoutGiftAidTaxChargeWhereBasicRateDiffers

  def withoutGiftAidTaxReductionWhereBasicRateDiffers: Def1_RetrieveCalculationResponse =
    Def1_RetrieveCalculationResponse(metadata, inputs, calculation.map(_.withoutGiftAidTaxReductionWhereBasicRateDiffers), messages)

  def withoutGiftAidTaxChargeWhereBasicRateDiffers: Def1_RetrieveCalculationResponse =
    Def1_RetrieveCalculationResponse(metadata, inputs, calculation.map(_.withoutGiftAidTaxChargeWhereBasicRateDiffers), messages)

}

object Def1_RetrieveCalculationResponse {

  def apply(metadata: def1.model.response.metadata.Metadata,
            inputs: def1.model.response.inputs.Inputs,
            calculation: Option[def1.model.response.calculation.Calculation],
            messages: Option[def1.model.response.messages.Messages]): Def1_RetrieveCalculationResponse = {
    new Def1_RetrieveCalculationResponse(
      metadata,
      inputs,
      calculation = if (calculation.exists(_.isDefined)) calculation else None,
      messages
    )
  }

  implicit val reads: Reads[Def1_RetrieveCalculationResponse] = Json.reads[Def1_RetrieveCalculationResponse]

  implicit val writes: OWrites[Def1_RetrieveCalculationResponse] = Json.writes[Def1_RetrieveCalculationResponse]

}

case class Def2_RetrieveCalculationResponse(
    metadata: def2.model.response.metadata.Metadata,
    inputs: def2.model.response.inputs.Inputs,
    calculation: Option[def2.model.response.calculation.Calculation],
    messages: Option[def2.model.response.messages.Messages]
) extends RetrieveCalculationResponse {

  override def intentToSubmitFinalDeclaration: Boolean = metadata.intentToSubmitFinalDeclaration

  override def finalDeclaration: Boolean = metadata.finalDeclaration

  override def hasErrors: Boolean = {
    for {
      messages <- messages
      errors   <- messages.errors
    } yield errors.nonEmpty
  }.getOrElse(false)

}

object Def2_RetrieveCalculationResponse {

  implicit val reads: Reads[Def2_RetrieveCalculationResponse] = Json.reads[Def2_RetrieveCalculationResponse]

  implicit val writes: OWrites[Def2_RetrieveCalculationResponse] = Json.writes[Def2_RetrieveCalculationResponse]

}
