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

package v1.fixtures.taxableIncome.ukPropertyNonFhl

import play.api.libs.json.{JsValue, Json}
import v1.models.response.taxableIncome.detail.ukPropertyNonFhl.summary.LossClaimsSummary

object LossClaimsSummaryFixtures {

  val incomeSourceId: String                                 = "AAIS12345678904"
  val incomeSourceType: String                               = "01"
  val incomeSourceName: String                               = "abcdefghijklm"
  val totalIncome: BigDecimal                                    = 1000.00
  val totalExpenses: BigDecimal                                  = 1000.00
  val netProfit: BigDecimal                                      = 1000.00
  val netLoss: BigDecimal                                        = 1000.00
  val totalAdditions: BigDecimal                                 = 1000.00
  val totalDeductions: BigDecimal                                = 1000.00
  val accountingAdjustments: BigDecimal                      = -1000.00
  val taxableProfit: BigDecimal                                  = 1000.00
  val adjustedIncomeTaxLoss: BigDecimal                      = 1000.00
  val totalBroughtForwardIncomeTaxLosses: Option[BigInt] = Some(1000)
  val lossForCSFHL: Option[BigInt]                       = Some(1000)
  val broughtForwardIncomeTaxLossesUsed: Option[BigInt]  = Some(1000)
  val taxableProfitAfterIncomeTaxLossesDeduction: BigDecimal = 1000.00
  val totalIncomeTaxLossesCarriedForward: Option[BigInt] = Some(1000)
  val class4Loss: BigDecimal                                 = 1000.00
  val totalBroughtForwardClass4Losses: Option[BigDecimal]    = Some(1000.00)
  val broughtForwardClass4LossesUsed: Option[BigDecimal]     = Some(1000.00)
  val carrySidewaysClass4LossesUsed: Option[BigDecimal]      = Some(1000.00)
  val totalClass4LossesCarriedForward: Option[BigDecimal]    = Some(1000.00)

  val lossClaimSummaryDesJson: JsValue = Json.parse(s"""{
                                                       |    "incomeSourceId": "$incomeSourceId",
                                                       |    "incomeSourceType": "$incomeSourceType",
                                                       |    "incomeSourceName": "$incomeSourceName",
                                                       |    "totalIncome": $totalIncome,
                                                       |    "totalExpenses": $totalExpenses,
                                                       |    "netProfit": $netProfit,
                                                       |    "netLoss": $netLoss,
                                                       |    "totalAdditions": $totalAdditions,
                                                       |    "totalDeductions": $totalDeductions,
                                                       |    "accountingAdjustments": $accountingAdjustments,
                                                       |    "taxableProfit": $taxableProfit,
                                                       |    "adjustedIncomeTaxLoss": $adjustedIncomeTaxLoss,
                                                       |    "totalBroughtForwardIncomeTaxLosses": ${totalBroughtForwardIncomeTaxLosses.get},
                                                       |    "lossForCSFHL": ${lossForCSFHL.get},
                                                       |    "broughtForwardIncomeTaxLossesUsed": ${broughtForwardIncomeTaxLossesUsed.get},
                                                       |    "taxableProfitAfterIncomeTaxLossesDeduction": $taxableProfitAfterIncomeTaxLossesDeduction,
                                                       |    "totalIncomeTaxLossesCarriedForward": ${totalIncomeTaxLossesCarriedForward.get},
                                                       |    "class4Loss": $class4Loss,
                                                       |    "totalBroughtForwardClass4Losses": ${totalBroughtForwardClass4Losses.get},
                                                       |    "broughtForwardClass4LossesUsed": ${broughtForwardClass4LossesUsed.get},
                                                       |    "carrySidewaysClass4LossesUsed": ${carrySidewaysClass4LossesUsed.get},
                                                       |    "totalClass4LossesCarriedForward": ${totalClass4LossesCarriedForward.get}
                                                       |}""".stripMargin)

  val lossClaimsSummaryJson: JsValue = Json.parse(
    """
      |{
      | "totalBroughtForwardIncomeTaxLosses" : 1000,
      | "broughtForwardIncomeTaxLossesUsed" : 1000,
      | "totalIncomeTaxLossesCarriedForward" : 1000
      |}
    """.stripMargin)

  val lossClaimsSummaryModel = LossClaimsSummary(Some(1000), Some(1000), Some(1000))
}
