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

package v3.models.response.retrieveCalculation

import api.models.domain.TaxYear
import api.models.hateoas.{HateoasData, Link}
import config.AppConfig
import play.api.libs.json.{Json, OWrites, Reads}
import v3.hateoas.{HateoasLinks, HateoasLinksFactory}
import v3.models.response.retrieveCalculation.calculation._
import v3.models.response.retrieveCalculation.inputs.Inputs
import v3.models.response.retrieveCalculation.messages.Messages
import v3.models.response.retrieveCalculation.metadata.Metadata

case class RetrieveCalculationResponse(
    metadata: Metadata,
    inputs: Inputs,
    calculation: Option[Calculation],
    messages: Option[Messages]
) {

  def withoutBasicExtension: RetrieveCalculationResponse =
    RetrieveCalculationResponse(metadata, inputs, calculation.map(_.withoutBasicExtension), messages)

  def withoutOffPayrollWorker: RetrieveCalculationResponse =
    RetrieveCalculationResponse(metadata, inputs, calculation.map(_.withoutOffPayrollWorker), messages)

  def withoutUnderLowerProfitThreshold: RetrieveCalculationResponse =
    RetrieveCalculationResponse(metadata, inputs, calculation.map(_.withoutUnderLowerProfitThreshold), messages)

  def withoutTotalAllowanceAndDeductions: RetrieveCalculationResponse =
    RetrieveCalculationResponse(metadata, inputs, calculation.map(_.withoutTotalAllowanceAndDeductions), messages)

}

object RetrieveCalculationResponse extends HateoasLinks {

  def apply(metadata: Metadata, inputs: Inputs, calculation: Option[Calculation], messages: Option[Messages]): RetrieveCalculationResponse = {
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
