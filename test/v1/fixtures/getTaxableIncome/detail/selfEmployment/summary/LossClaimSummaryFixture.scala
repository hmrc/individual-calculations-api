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

package v1.fixtures.getTaxableIncome.detail.selfEmployment.summary

import play.api.libs.json.{JsValue, Json}
import v1.models.response.getTaxableIncome.detail.selfEmployment.summary.LossClaimsSummary

object LossClaimSummaryFixture {

  val totalBroughtForwardIncomeTaxLosses: Option[BigDecimal] = Some(1)
  val broughtForwardIncomeTaxLossesUsed: Option[BigDecimal] = Some(2)
  val totalIncomeTaxLossesCarriedForward: Option[BigDecimal] = Some(3)
  val totalBroughtForwardClass4Losses: Option[BigDecimal] = Some(1)
  val broughtForwardClass4LossesUsed: Option[BigDecimal] = Some(2)
  val carrySidewaysClass4LossesUsed: Option[BigDecimal] = Some(2)
  val totalClass4LossesCarriedForward: Option[BigDecimal] = Some(3)
  val carrySidewaysIncomeTaxLossesUsed: Option[BigDecimal] = Some(2)

  val lossClaimsSummaryModel: LossClaimsSummary =
    LossClaimsSummary(
      totalBroughtForwardIncomeTaxLosses = totalBroughtForwardIncomeTaxLosses,
      broughtForwardIncomeTaxLossesUsed = broughtForwardIncomeTaxLossesUsed,
      carrySidewaysIncomeTaxLossesUsed = carrySidewaysIncomeTaxLossesUsed,
      totalIncomeTaxLossesCarriedForward = totalIncomeTaxLossesCarriedForward,
      totalBroughtForwardClass4Losses = totalBroughtForwardClass4Losses,
      broughtForwardClass4LossesUsed = broughtForwardClass4LossesUsed,
      carrySidewaysClass4LossesUsed = carrySidewaysClass4LossesUsed,
      totalClass4LossesCarriedForward = totalClass4LossesCarriedForward
    )

  val lossClaimSummaryJson: JsValue = Json.parse(
    s"""
       |{
       |    "totalBroughtForwardIncomeTaxLosses": ${totalBroughtForwardIncomeTaxLosses.get},
       |    "broughtForwardIncomeTaxLossesUsed": ${broughtForwardIncomeTaxLossesUsed.get},
       |    "carrySidewaysIncomeTaxLossesUsed": ${carrySidewaysIncomeTaxLossesUsed.get},
       |    "totalIncomeTaxLossesCarriedForward": ${totalIncomeTaxLossesCarriedForward.get},
       |    "totalBroughtForwardClass4Losses": ${totalBroughtForwardClass4Losses.get},
       |    "broughtForwardClass4LossesUsed": ${broughtForwardClass4LossesUsed.get},
       |    "carrySidewaysClass4LossesUsed": ${carrySidewaysClass4LossesUsed.get},
       |    "totalClass4LossesCarriedForward": ${totalClass4LossesCarriedForward.get}
       |}
     """.stripMargin
  )
}