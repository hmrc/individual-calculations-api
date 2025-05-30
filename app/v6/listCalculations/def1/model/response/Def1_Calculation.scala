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

package v6.listCalculations.def1.model.response

import common.TaxYearFormats._
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, OWrites, Reads}
import shared.models.domain.TaxYear
import v6.common.model.response.CalculationType

sealed trait Calculation {
  def calculationId: String
  def taxYear: Option[TaxYear]
}

object Calculation {

  implicit val writes: OWrites[Calculation] = OWrites.apply[Calculation] { case a: Def1_Calculation =>
    Json.toJsObject(a)
  }

}

case class Def1_Calculation(calculationId: String,
                            calculationTimestamp: String,
                            calculationType: CalculationType,
                            requestedBy: Option[String],
                            taxYear: Option[TaxYear],
                            totalIncomeTaxAndNicsDue: Option[BigDecimal],
                            intentToSubmitFinalDeclaration: Option[Boolean],
                            finalDeclaration: Option[Boolean],
                            finalDeclarationTimestamp: Option[String])
    extends Calculation

object Def1_Calculation {
  implicit val writes: OWrites[Def1_Calculation] = Json.writes[Def1_Calculation]

  implicit val reads: Reads[Def1_Calculation] =
    ((JsPath \ "calculationId").read[String] and
      (JsPath \ "calculationTimestamp").read[String] and
      (JsPath \ "calculationType").read[CalculationType] and
      (JsPath \ "requestedBy").readNullable[String] and
      (JsPath \ "year").readNullable[TaxYear] and
      (JsPath \ "totalIncomeTaxAndNicsDue").readNullable[BigDecimal] and
      (JsPath \ "intentToCrystallise").readNullable[Boolean] and
      (JsPath \ "crystallised").readNullable[Boolean] and
      (JsPath \ "crystallisationTimestamp").readNullable[String])(Def1_Calculation.apply _)

}
