/*
 * Copyright 2026 HM Revenue & Customs
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

package v9.submitFinalDeclaration.model.request

import api.models.domain.{CalculationId, Nino, TaxYear}
import play.api.libs.json.{JsValue, Json}
import v9.common.model.domain.CalculationType
import v9.retrieveCalculation.models.request.*
import v9.retrieveCalculation.schema.RetrieveCalculationSchema

sealed trait SubmitFinalDeclarationRequestData {
  val nino: Nino
  val taxYear: TaxYear
  val calculationId: CalculationId
  val calculationType: CalculationType
  def toNrsJson: JsValue

  def toRetrieveRequestData: RetrieveCalculationRequestData =
    RetrieveCalculationSchema.schemaFor(taxYear) match {
      case RetrieveCalculationSchema.Def1 => Def1_RetrieveCalculationRequestData(nino, taxYear, calculationId)
      case RetrieveCalculationSchema.Def2 => Def2_RetrieveCalculationRequestData(nino, taxYear, calculationId)
      case RetrieveCalculationSchema.Def3 => Def3_RetrieveCalculationRequestData(nino, taxYear, calculationId)
      case RetrieveCalculationSchema.Def4 => Def4_RetrieveCalculationRequestData(nino, taxYear, calculationId)
    }

}

case class Def1_SubmitFinalDeclarationRequestData(nino: Nino, taxYear: TaxYear, calculationId: CalculationId, calculationType: CalculationType)
    extends SubmitFinalDeclarationRequestData {
  def toNrsJson: JsValue = Json.obj("calculationId" -> calculationId.calculationId)
}
