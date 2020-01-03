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

package v1.fixtures.getTaxableIncome.detail.ukPropertyFhl

import play.api.libs.json.{JsValue, Json}
import v1.fixtures.getTaxableIncome.detail.ukPropertyFhl.detail.LossClaimsDetailFixture._
import v1.fixtures.getTaxableIncome.detail.ukPropertyFhl.summary.LossClaimSummaryFixture._
import v1.models.response.getTaxableIncome.detail.ukPropertyFhl.UkPropertyFhl


object UkPropertyFhlFixture {

  val totalIncome: Option[BigDecimal] = Some(1000.01)
  val totalExpenses: Option[BigDecimal] = Some(1000.02)
  val netProfit: Option[BigDecimal] = Some(1000.03)
  val netLoss: Option[BigDecimal] = Some(1000.04)
  val totalAdditions: Option[BigDecimal] = Some(1000.05)
  val totalDeductions: Option[BigDecimal] = Some(1000.06)
  val accountingAdjustments: Option[BigDecimal] = Some(1000.07)
  val adjustedIncomeTaxLoss: Option[BigInt] = None
  val taxableProfit: Option[BigInt] = Some(1008)
  val taxableProfitAfterIncomeTaxLossesDeduction: Option[BigInt] = None

  val ukPropertyFhlModel: UkPropertyFhl =
    UkPropertyFhl(
      totalIncome = totalIncome,
      totalExpenses = totalExpenses,
      netProfit = netProfit,
      netLoss = netLoss,
      totalAdditions = totalAdditions,
      totalDeductions = totalDeductions,
      accountingAdjustments = accountingAdjustments,
      adjustedIncomeTaxLoss = adjustedIncomeTaxLoss,
      taxableProfit = taxableProfit,
      taxableProfitAfterIncomeTaxLossesDeduction = taxableProfitAfterIncomeTaxLossesDeduction,
      lossClaimsSummary = Some(lossClaimsSummaryModel),
      lossClaimsDetail = Some(lossClaimsDetailModel)
    )

  val ukPropertyFhlJson: JsValue = Json.parse(
    s"""
       |{
       |   "totalIncome": ${totalIncome.get},
       |   "totalExpenses": ${totalExpenses.get},
       |   "netProfit": ${netProfit.get},
       |	 "netLoss": ${netLoss.get},
       |   "totalAdditions": ${totalAdditions.get},
       |   "totalDeductions": ${totalDeductions.get},
       |   "accountingAdjustments": ${accountingAdjustments.get},
       |   "taxableProfit": ${taxableProfit.get},
       |	 "lossClaimsSummary": $lossClaimSummaryJson,
       |   "lossClaimsDetail": $lossClaimsDetailJson
       |}
    """.stripMargin)
}
