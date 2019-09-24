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

package v1.models.response.taxableIncome.detail.selfEmployment.summary

import play.api.libs.json.{Json, OFormat}

case class LossClaimsSummary(
    totalBroughtForwardIncomeTaxLosses: Option[BigDecimal],
    broughtForwardIncomeTaxLossesUsed: Option[BigDecimal],
    totalIncomeTaxLossesCarriedForward: Option[BigDecimal],
    totalBroughtForwardClass4Losses: Option[BigDecimal],
    broughtForwardClass4LossesUsed: Option[BigDecimal],
    carrySidewaysClass4LossesUsed: Option[BigDecimal],
    totalClass4LossesCarriedForward: Option[BigDecimal]
)

object LossClaimsSummary {
  implicit val format: OFormat[LossClaimsSummary] = Json.format[LossClaimsSummary]
}

