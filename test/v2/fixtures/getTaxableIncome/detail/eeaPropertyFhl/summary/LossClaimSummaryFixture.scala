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

package v2.fixtures.getTaxableIncome.detail.eeaPropertyFhl.summary

import play.api.libs.json.{JsValue, Json}
import v2.models.response.getTaxableIncome.detail.eeaPropertyFhl.summary.LossClaimsSummary

object LossClaimSummaryFixture {

  val lossForCSFHL: Option[BigInt] = Some(1000)
  val totalBroughtForwardIncomeTaxLosses: Option[BigInt] = Some(1000)
  val broughtForwardIncomeTaxLossesUsed: Option[BigInt] = Some(1000)
  val totalIncomeTaxLossesCarriedForward: Option[BigInt] = Some(1000)

  val lossClaimsSummaryModel: LossClaimsSummary =
    LossClaimsSummary(
      lossForCSFHL = lossForCSFHL,
      totalBroughtForwardIncomeTaxLosses = totalBroughtForwardIncomeTaxLosses,
      broughtForwardIncomeTaxLossesUsed = broughtForwardIncomeTaxLossesUsed,
      totalIncomeTaxLossesCarriedForward = totalIncomeTaxLossesCarriedForward
    )

  val lossClaimSummaryJson: JsValue = Json.parse(
    s"""
       |{
       |   "lossForCSFHL": ${lossForCSFHL.get},
       |   "totalBroughtForwardIncomeTaxLosses": ${totalBroughtForwardIncomeTaxLosses.get},
       |   "broughtForwardIncomeTaxLossesUsed": ${broughtForwardIncomeTaxLossesUsed.get},
       |   "totalIncomeTaxLossesCarriedForward": ${totalIncomeTaxLossesCarriedForward.get}
       |}
     """.stripMargin
  )
}