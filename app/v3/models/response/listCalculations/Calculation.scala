/*
 * Copyright 2022 HM Revenue & Customs
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

package v3.models.response.listCalculations

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, OWrites, Reads}
import v3.models.domain.TaxYear
import v3.models.response.common.CalculationType

case class Calculation(calculationId: String,
                       calculationTimestamp: String,
                       calculationType: CalculationType,
                       requestedBy: Option[String],
                       taxYear: Option[TaxYear],
                       totalIncomeTaxAndNicsDue: Option[BigDecimal],
                       intentToSubmitFinalDeclaration: Option[Boolean],
                       finalDeclaration: Option[Boolean],
                       finalDeclarationTimestamp: Option[String])

object Calculation {
  implicit val writes: OWrites[Calculation] = Json.writes[Calculation]

  implicit val reads: Reads[Calculation] =
    ((JsPath \ "calculationId").read[String] and
      (JsPath \ "calculationTimestamp").read[String] and
      (JsPath \ "calculationType").read[CalculationType] and
      (JsPath \ "requestedBy").readNullable[String] and
      (JsPath \ "year").readNullable[TaxYear] and
      (JsPath \ "totalIncomeTaxAndNicsDue").readNullable[BigDecimal] and
      (JsPath \ "intentToCrystallise").readNullable[Boolean] and
      (JsPath \ "crystallised").readNullable[Boolean] and
      (JsPath \ "crystallisationTimestamp").readNullable[String])(Calculation.apply _)

}
