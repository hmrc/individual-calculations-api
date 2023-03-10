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

import config.AppConfig
import play.api.libs.json.{Json, OWrites, Reads}
import v3.hateoas.{HateoasLinks, HateoasLinksFactory}
import v3.models.domain.TaxYear
import v3.models.hateoas.{HateoasData, Link}
import v3.models.response.retrieveCalculation.calculation._
import v3.models.response.retrieveCalculation.calculation.employmentAndPensionsIncome.EmploymentAndPensionsIncome
import v3.models.response.retrieveCalculation.calculation.taxCalculation.{Nics, TaxCalculation}
import v3.models.response.retrieveCalculation.inputs.Inputs
import v3.models.response.retrieveCalculation.messages.Messages
import v3.models.response.retrieveCalculation.metadata.Metadata

case class RetrieveCalculationResponse(
    metadata: Metadata,
    inputs: Inputs,
    calculation: Option[Calculation],
    messages: Option[Messages]
) {

  def withoutBasicExtension: RetrieveCalculationResponse = {
    val updatedCalculation = this.calculation
      .map { calc =>
        val updatedReliefs = calc.reliefs
          .map(_.copy(basicRateExtension = None))
          .filter(_.isDefined)
        calc.copy(reliefs = updatedReliefs)
      }
      .filter(_.isDefined)
    copy(calculation = updatedCalculation)
  }

  def withoutOffPayrollWorker: RetrieveCalculationResponse = {
    calculation match {
      case None => this
      case Some(calc) =>
        val updatedEmploymentAndPensionsIncome: Option[EmploymentAndPensionsIncome] = calc.employmentAndPensionsIncome
          .map { detail =>
            detail.employmentAndPensionsIncomeDetail match {
              case Some(det) =>
                val details = (for (c <- det) yield c.copy(offPayrollWorker = None)).filter(_.isDefined)
                detail.copy(employmentAndPensionsIncomeDetail = if (details.isEmpty) None else Some(details))
              case _ => detail
            }
          }
          .filter(_.isDefined)
        copy(calculation = Some(calc.copy(employmentAndPensionsIncome = updatedEmploymentAndPensionsIncome)))
    }
  }

  def withoutUnderLowerProfitThreshold: RetrieveCalculationResponse = {

    def updateClass2Nics(nics: Nics): Nics =
      nics.class2Nics match {
        case None => nics
        case Some(contribution) =>
          val updatedClass2Nics = contribution.copy(underLowerProfitThreshold = None)
          nics.copy(class2Nics = Some(updatedClass2Nics))
      }

    calculation match {
      case None => this
      case Some(calc) =>
        val taxCalc: Option[TaxCalculation] = calc.taxCalculation.map { taxCalculation =>
          val contribution = taxCalculation.nics match {
            case None       => taxCalculation.nics
            case Some(nics) => Some(updateClass2Nics(nics))
          }
          taxCalculation.copy(nics = contribution)
        }
        this.copy(calculation = Some(calc.copy(taxCalculation = taxCalc)))
    }

  }

  def withoutTotalAllowanceAndDeductions: RetrieveCalculationResponse = {
    val updatedCalculation = this.calculation
      .map { calc =>
        val updatedEoy = calc.endOfYearEstimate
          .map(_.copy(totalAllowancesAndDeductions = None))
          .filter(_.isDefined)
        calc.copy(endOfYearEstimate = updatedEoy)
      }
      .filter(_.isDefined)
    copy(calculation = updatedCalculation)
  }

}

object RetrieveCalculationResponse extends HateoasLinks {
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
