/*
 * Copyright 2020 HM Revenue & Customs
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

package v3.models.response.retrieveCalculation.calculation.businessProfitAndLoss

import play.api.libs.json.{Format, Json}
import v3.models.response.common.IncomeSourceType

case class BusinessProfitAndLoss(incomeSourceId: String,
                                 incomeSourceType: IncomeSourceType,
                                 incomeSourceName: Option[String],
                                 totalIncome: Option[BigDecimal],
                                 totalExpenses: Option[BigDecimal],
                                 netProfit: Option[BigDecimal],
                                 netLoss: Option[BigDecimal],
                                 totalAdditions: Option[BigDecimal],
                                 totalDeductions: Option[BigDecimal],
                                 accountingAdjustments: Option[BigDecimal],
                                 taxableProfit: Option[BigInt],
                                 adjustedIncomeTaxLoss: Option[BigInt],
                                 totalBroughtForwardIncomeTaxLosses: Option[BigInt],
                                 lossForCSFHL: Option[BigInt],
                                 broughtForwardIncomeTaxLossesUsed: Option[BigInt],
                                 taxableProfitAfterIncomeTaxLossesDeduction: Option[BigInt],
                                 carrySidewaysIncomeTaxLossesUsed: Option[BigInt],
                                 broughtForwardCarrySidewaysIncomeTaxLossesUsed: Option[BigInt],
                                 totalIncomeTaxLossesCarriedForward: Option[BigInt],
                                 class4Loss: Option[BigInt],
                                 totalBroughtForwardClass4Losses: Option[BigInt],
                                 carrySidewaysClass4LossesUsed: Option[BigInt],
                                 totalClass4LossesCarriedForward: Option[BigInt]
                                )

object BusinessProfitAndLoss {

  implicit val format: Format[BusinessProfitAndLoss]  = Json.format[BusinessProfitAndLoss]
}
