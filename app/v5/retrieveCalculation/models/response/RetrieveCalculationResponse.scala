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

package v5.retrieveCalculation.models.response

import api.hateoas.{HateoasData, HateoasLinks, HateoasLinksFactory, Link}
import api.models.domain.TaxYear
import config.AppConfig
import play.api.libs.json.{Json, OWrites, Reads}
import v5.retrieveCalculation.def1.model.response.calculation.Def1_Calculation
import v5.retrieveCalculation.def1.model.response.inputs.Def1_Inputs
import v5.retrieveCalculation.def1.model.response.messages.Def1_Messages
import v5.retrieveCalculation.def1.model.response.metadata.Def1_Metadata

case class RetrieveCalculationResponse(
                                        metadata: Def1_Metadata,
                                        inputs: Def1_Inputs,
                                        calculation: Option[Def1_Calculation],
                                        messages: Option[Def1_Messages]
) {

  def withoutR8bSpecificUpdates: RetrieveCalculationResponse =
    this.withoutBasicExtension.withoutOffPayrollWorker.withoutTotalAllowanceAndDeductions

  def withoutBasicExtension: RetrieveCalculationResponse =
    RetrieveCalculationResponse(metadata, inputs, calculation.map(_.withoutBasicExtension), messages)

  def withoutOffPayrollWorker: RetrieveCalculationResponse =
    RetrieveCalculationResponse(metadata, inputs, calculation.map(_.withoutOffPayrollWorker), messages)

  def withoutTotalAllowanceAndDeductions: RetrieveCalculationResponse =
    RetrieveCalculationResponse(metadata, inputs, calculation.map(_.withoutTotalAllowanceAndDeductions), messages)

  def withoutUnderLowerProfitThreshold: RetrieveCalculationResponse =
    RetrieveCalculationResponse(metadata, inputs, calculation.map(_.withoutUnderLowerProfitThreshold), messages)

  def withoutTaxTakenOffTradingIncome: RetrieveCalculationResponse = {
    RetrieveCalculationResponse(metadata, inputs, calculation.map(_.withoutTaxTakenOffTradingIncome).filter(_.isDefined), messages)
  }

  def withoutBasicRateDivergenceUpdates: RetrieveCalculationResponse =
    this.withoutGiftAidTaxReductionWhereBasicRateDiffers.withoutGiftAidTaxChargeWhereBasicRateDiffers

  def withoutGiftAidTaxReductionWhereBasicRateDiffers: RetrieveCalculationResponse =
    RetrieveCalculationResponse(metadata, inputs, calculation.map(_.withoutGiftAidTaxReductionWhereBasicRateDiffers), messages)

  def withoutGiftAidTaxChargeWhereBasicRateDiffers: RetrieveCalculationResponse =
    RetrieveCalculationResponse(metadata, inputs, calculation.map(_.withoutGiftAidTaxChargeWhereBasicRateDiffers), messages)

  def withoutAdditionalFieldsUpdates: RetrieveCalculationResponse =
    this.withoutCessationDate.withoutOtherIncome.withoutCommencementDate.withoutItsaStatus

  def withoutCessationDate: RetrieveCalculationResponse = {
    RetrieveCalculationResponse(metadata, inputs.withoutCessationDate, calculation, messages)
  }

  def withoutCommencementDate: RetrieveCalculationResponse = {
    RetrieveCalculationResponse(metadata, inputs.withoutCommencementDate, calculation, messages)
  }

  def withoutOtherIncome: RetrieveCalculationResponse = {
    RetrieveCalculationResponse(metadata, inputs, calculation.map(_.withoutOtherIncome).filter(_.isDefined), messages)
  }

  def withoutItsaStatus: RetrieveCalculationResponse = {
    RetrieveCalculationResponse(metadata, inputs.withoutItsaStatus, calculation, messages)
  }

}

object RetrieveCalculationResponse extends HateoasLinks {

  def apply(metadata: Def1_Metadata,
            inputs: Def1_Inputs,
            calculation: Option[Def1_Calculation],
            messages: Option[Def1_Messages]): RetrieveCalculationResponse = {
    new RetrieveCalculationResponse(
      metadata,
      inputs,
      calculation = if (calculation.exists(_.isDefined)) calculation else None,
      messages
    )
  }

  implicit val reads: Reads[RetrieveCalculationResponse] = Json.reads[RetrieveCalculationResponse]

  implicit val writes: OWrites[RetrieveCalculationResponse] = Json.writes[RetrieveCalculationResponse]

  implicit object LinksFactory extends HateoasLinksFactory[RetrieveCalculationResponse, RetrieveCalculationHateoasData] {

    override def links(appConfig: AppConfig, data: RetrieveCalculationHateoasData): Seq[Link] = {
      import data._
      val intentToSubmitFinalDeclaration: Boolean = response.metadata.intentToSubmitFinalDeclaration

      val finalDeclaration: Boolean = response.metadata.finalDeclaration

      val responseHasErrors: Boolean = {
        for {
          messages <- response.messages
          errors   <- messages.errors
        } yield {
          errors.nonEmpty
        }
      }.getOrElse(false)

      if (intentToSubmitFinalDeclaration && !finalDeclaration && !responseHasErrors) {
        Seq(
          trigger(appConfig, nino, taxYear),
          retrieve(appConfig, nino, taxYear, calculationId),
          submitFinalDeclaration(appConfig, nino, taxYear, calculationId)
        )
      } else {
        Seq(
          trigger(appConfig, nino, taxYear),
          retrieve(appConfig, nino, taxYear, calculationId)
        )
      }
    }

  }

}

case class RetrieveCalculationHateoasData(
    nino: String,
    taxYear: TaxYear,
    calculationId: String,
    response: RetrieveCalculationResponse
) extends HateoasData
