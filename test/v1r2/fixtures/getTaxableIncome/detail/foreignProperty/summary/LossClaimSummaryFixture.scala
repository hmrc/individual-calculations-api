/*
 * Copyright 2021 HM Revenue & Customs
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

package v1r2.fixtures.getTaxableIncome.detail.foreignProperty.summary

import play.api.libs.json.{JsValue, Json}
import v1r2.models.response.getTaxableIncome.detail.foreignProperty.summary.LossClaimsSummary

object LossClaimSummaryFixture {

  val carrySidewaysIncomeTaxLossesUsed: Option[BigInt] = Some(1000)
  val totalBroughtForwardIncomeTaxLosses: Option[BigInt] = Some(1000)
  val broughtForwardIncomeTaxLossesUsed: Option[BigInt] = Some(1000)
  val totalIncomeTaxLossesCarriedForward: Option[BigInt] = Some(1000)
  val broughtForwardCarrySidewaysIncomeTaxLossesUsed: Option[BigInt] = Some(1000)

  val lossClaimsSummaryModel: LossClaimsSummary =
    LossClaimsSummary(
      totalBroughtForwardIncomeTaxLosses = totalBroughtForwardIncomeTaxLosses,
      broughtForwardIncomeTaxLossesUsed = broughtForwardIncomeTaxLossesUsed,
      carrySidewaysIncomeTaxLossesUsed = carrySidewaysIncomeTaxLossesUsed,
      totalIncomeTaxLossesCarriedForward = totalIncomeTaxLossesCarriedForward,
      broughtForwardCarrySidewaysIncomeTaxLossesUsed = broughtForwardCarrySidewaysIncomeTaxLossesUsed
    )

  val lossClaimSummaryJson: JsValue = Json.parse(
    s"""
       |{
       |   "totalBroughtForwardIncomeTaxLosses": ${totalBroughtForwardIncomeTaxLosses.get},
       |   "broughtForwardIncomeTaxLossesUsed": ${broughtForwardIncomeTaxLossesUsed.get},
       |   "carrySidewaysIncomeTaxLossesUsed": ${carrySidewaysIncomeTaxLossesUsed.get},
       |   "totalIncomeTaxLossesCarriedForward": ${totalIncomeTaxLossesCarriedForward.get},
       |   "broughtForwardCarrySidewaysIncomeTaxLossesUsed": ${broughtForwardCarrySidewaysIncomeTaxLossesUsed.get}
       |}
     """.stripMargin
  )
}