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

package v5.triggerCalculation.model.response

import play.api.libs.json.OFormat.oFormatFromReadsAndOWrites
import play.api.libs.json._

sealed trait TriggerCalculationResponse {
  val calculationId: String
}

object TriggerCalculationResponse {

  implicit val vendorWrites: OWrites[TriggerCalculationResponse] = { case def1: Def1_TriggerCalculationResponse =>
    Json.toJsObject(def1)
  }

}

case class Def1_TriggerCalculationResponse(calculationId: String) extends TriggerCalculationResponse

object Def1_TriggerCalculationResponse {

  implicit val reads: Reads[Def1_TriggerCalculationResponse]    = (JsPath \ "id").read[String].map(Def1_TriggerCalculationResponse.apply)
  implicit val writes: OWrites[Def1_TriggerCalculationResponse] = Json.writes[Def1_TriggerCalculationResponse]

}
