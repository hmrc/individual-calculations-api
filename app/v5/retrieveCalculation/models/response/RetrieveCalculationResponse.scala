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
import config.{AppConfig, FeatureSwitches}
import play.api.libs.json.{Json, OWrites, Reads}
import v5.retrieveCalculation.def1.model.response.calculation.Def1_Calculation
import v5.retrieveCalculation.def1.model.response.inputs.Def1_Inputs
import v5.retrieveCalculation.def1.model.response.messages.Def1_Messages
import v5.retrieveCalculation.def1.model.response.metadata.Def1_Metadata

sealed trait RetrieveCalculationResponse {
  def adjustFields(featureSwitches: FeatureSwitches, taxYear: String): RetrieveCalculationResponse
}

object RetrieveCalculationResponse extends HateoasLinks {

  implicit val writes: OWrites[RetrieveCalculationResponse] = { case def1: Def1_RetrieveCalculationResponse =>
    Json.toJsObject(def1)
  }

  implicit object RetrieveAnnualSubmissionLinksFactory extends HateoasLinksFactory[RetrieveCalculationResponse, RetrieveCalculationHateoasData] {

    override def links(appConfig: AppConfig, data: RetrieveCalculationHateoasData): Seq[Link] = {
      data.response match {
        case def1Response: Def1_RetrieveCalculationResponse =>
          Def1_RetrieveCalculationResponse.def1Links(appConfig, def1Response, data)
      }
    }

  }

}

case class Def1_RetrieveCalculationResponse(
    metadata: Def1_Metadata,
    inputs: Def1_Inputs,
    calculation: Option[Def1_Calculation],
    messages: Option[Def1_Messages]
) extends RetrieveCalculationResponse {

  def adjustFields(featureSwitches: FeatureSwitches, taxYear: String): Def1_RetrieveCalculationResponse = {
    import featureSwitches._

    def updateModelR8b(response: Def1_RetrieveCalculationResponse): Def1_RetrieveCalculationResponse =
      if (isR8bSpecificApiEnabled) response else response.withoutR8bSpecificUpdates

    def updateModelAdditionalFields(response: Def1_RetrieveCalculationResponse): Def1_RetrieveCalculationResponse =
      if (isRetrieveSAAdditionalFieldsEnabled) response else response.withoutAdditionalFieldsUpdates

    def updateModelCl290(response: Def1_RetrieveCalculationResponse): Def1_RetrieveCalculationResponse =
      if (isCl290Enabled) response else response.withoutTaxTakenOffTradingIncome

    def updateModelBasicRateDivergence(taxYear: String, response: Def1_RetrieveCalculationResponse): Def1_RetrieveCalculationResponse = {
      if (isBasicRateDivergenceEnabled && TaxYear.fromMtd(taxYear).is2025) response else response.withoutBasicRateDivergenceUpdates
    }

    val responseMaybeWithoutR8b              = updateModelR8b(this)
    val responseMaybeWithoutAdditionalFields = updateModelAdditionalFields(responseMaybeWithoutR8b)
    val responseMaybeWithoutCl290            = updateModelCl290(responseMaybeWithoutAdditionalFields)
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

  def withoutAdditionalFieldsUpdates: Def1_RetrieveCalculationResponse =
    this.withoutCessationDate.withoutOtherIncome.withoutCommencementDate.withoutItsaStatus

  def withoutCessationDate: Def1_RetrieveCalculationResponse = {
    Def1_RetrieveCalculationResponse(metadata, inputs.withoutCessationDate, calculation, messages)
  }

  def withoutCommencementDate: Def1_RetrieveCalculationResponse = {
    Def1_RetrieveCalculationResponse(metadata, inputs.withoutCommencementDate, calculation, messages)
  }

  def withoutOtherIncome: Def1_RetrieveCalculationResponse = {
    Def1_RetrieveCalculationResponse(metadata, inputs, calculation.map(_.withoutOtherIncome).filter(_.isDefined), messages)
  }

  def withoutItsaStatus: Def1_RetrieveCalculationResponse = {
    Def1_RetrieveCalculationResponse(metadata, inputs.withoutItsaStatus, calculation, messages)
  }

}

object Def1_RetrieveCalculationResponse extends HateoasLinks {

  def apply(metadata: Def1_Metadata,
            inputs: Def1_Inputs,
            calculation: Option[Def1_Calculation],
            messages: Option[Def1_Messages]): Def1_RetrieveCalculationResponse = {
    new Def1_RetrieveCalculationResponse(
      metadata,
      inputs,
      calculation = if (calculation.exists(_.isDefined)) calculation else None,
      messages
    )
  }

  implicit val reads: Reads[Def1_RetrieveCalculationResponse] = Json.reads[Def1_RetrieveCalculationResponse]

  implicit val writes: OWrites[Def1_RetrieveCalculationResponse] = Json.writes[Def1_RetrieveCalculationResponse]

  private[response] def def1Links(appConfig: AppConfig,
                                  response: Def1_RetrieveCalculationResponse,
                                  data: RetrieveCalculationHateoasData): Seq[Link] = {

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

    import data._

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

case class RetrieveCalculationHateoasData(
    nino: String,
    taxYear: TaxYear,
    calculationId: String,
    response: RetrieveCalculationResponse
) extends HateoasData
