/*
 * Copyright 2019 HM Revenue & Customs
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

package v1.models.response.getCalculation.taxableIncome.detail.selfEmployment

import play.api.libs.functional.syntax._
import play.api.libs.json.{Json, OWrites, Reads, _}

case class LossClaimsSummary(totalBroughtForwardIncomeTaxLosses: Option[BigDecimal],
                             broughtForwardIncomeTaxLossesUsed: Option[BigDecimal],
                             totalIncomeTaxLossesCarriedForward: Option[BigDecimal],
                             totalBroughtForwardClass4Losses: Option[BigDecimal],
                             broughtForwardClass4LossesUsed: Option[BigDecimal],
                             carrySidewaysClass4LossesUsed: Option[BigDecimal],
                             totalClass4LossesCarriedForward: Option[BigDecimal]) {

  val isEmpty: Boolean = this == LossClaimsSummary.emptyLossClaimsSummary
}

object LossClaimsSummary {
  val emptyLossClaimsSummary                      = LossClaimsSummary(None, None, None, None, None, None, None)
  implicit val writes: OWrites[LossClaimsSummary] = Json.writes[LossClaimsSummary]
  implicit val reads: Reads[LossClaimsSummary] = (
    (JsPath \ "totalBroughtForwardIncomeTaxLosses").readNullable[BigDecimal] and
      (JsPath \ "broughtForwardIncomeTaxLossesUsed").readNullable[BigDecimal] and
      (JsPath \ "totalIncomeTaxLossesCarriedForward").readNullable[BigDecimal] and
      (JsPath \ "totalBroughtForwardClass4Losses").readNullable[BigDecimal] and
      (JsPath \ "broughtForwardClass4LossesUsed").readNullable[BigDecimal] and
      (JsPath \ "carrySidewaysClass4LossesUsed").readNullable[BigDecimal] and
      (JsPath \ "totalClass4LossesCarriedForward").readNullable[BigDecimal]
  )(LossClaimsSummary.apply _)
}
