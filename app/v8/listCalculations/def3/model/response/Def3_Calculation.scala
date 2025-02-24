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

package v8.listCalculations.def3.model.response

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, OWrites, Reads}
import v8.listCalculations.model.response.{Calculation, ListCalculationType}

case class Def3_Calculation(calculationId: String,
                            calculationTimestamp: String,
                            calculationType: ListCalculationType,
                            calculationTrigger: String,
                            calculationOutcome: String,
                            liabilityAmount: Option[BigDecimal],
                            fromDate: Option[String],
                            toDate: Option[String]
                           )
    extends Calculation

object Def3_Calculation {
  implicit val writes: OWrites[Def3_Calculation] = Json.writes[Def3_Calculation]

  implicit val reads: Reads[Def3_Calculation] =
    ((JsPath \ "calculationId").read[String] and
      (JsPath \ "calculationTimestamp").read[String] and
      (JsPath \ "calculationType").read[ListCalculationType] and
      (JsPath \ "calculationTrigger").read[String] and
      (JsPath \ "calculationOutcome").read[String] and
      (JsPath \ "liabilityAmount").readNullable[BigDecimal] and
      (JsPath \ "fromDate").readNullable[String] and
      (JsPath \ "toDate").readNullable[String])(Def3_Calculation.apply _)

}
