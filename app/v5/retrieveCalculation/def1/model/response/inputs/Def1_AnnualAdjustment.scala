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

package v5.retrieveCalculation.def1.model.response.inputs

import play.api.libs.functional.syntax._
import play.api.libs.json._
import v5.retrieveCalculation.def1.model.response.common.Def1_IncomeSourceType
import v5.retrieveCalculation.def1.model.response.common.Def1_IncomeSourceType._

case class Def1_AnnualAdjustment(incomeSourceId: String,
                                 incomeSourceType: Def1_IncomeSourceType,
                                 bsasId: String,
                                 receivedDateTime: String,
                                 applied: Boolean)

object Def1_AnnualAdjustment {

  implicit val incomeSourceTypeFormat: Format[Def1_IncomeSourceType] = Def1_IncomeSourceType.formatRestricted(
    `self-employment`,
    `uk-property-non-fhl`,
    `uk-property-fhl`,
    `foreign-property-fhl-eea`,
    `foreign-property`
  )

  implicit val reads: Reads[Def1_AnnualAdjustment] = (
    (JsPath \ "incomeSourceId").read[String] and
      (JsPath \ "incomeSourceType").read[Def1_IncomeSourceType](incomeSourceTypeFormat) and
      (JsPath \ "ascId").read[String] and
      (JsPath \ "receivedDateTime").read[String] and
      (JsPath \ "applied").read[Boolean]
  )(Def1_AnnualAdjustment.apply _)

  implicit val writes: OWrites[Def1_AnnualAdjustment] = Json.writes
}
