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

package v1r2.fixtures.getTaxableIncome.detail.ukPropertyFhl

import play.api.libs.json.{JsValue, Json}
import v1r2.fixtures.getTaxableIncome.detail.ukPropertyFhl.detail.LossClaimsDetailFixture._
import v1r2.fixtures.getTaxableIncome.detail.ukPropertyFhl.summary.LossClaimSummaryFixture._
import v1r2.models.response.getTaxableIncome.detail.ukPropertyFhl.UkPropertyFhl
import v1r2.models.response.getTaxableIncome.detail.ukPropertyFhl.detail.BusinessSourceAdjustableSummary

object UkPropertyFhlFixture {

  val totalIncome: Option[BigDecimal] = Some(1000.01)
  val totalExpenses: Option[BigDecimal] = Some(1000.02)
  val netProfit: Option[BigDecimal] = Some(1000.03)
  val netLoss: Option[BigDecimal] = Some(1000.04)
  val totalAdditions: Option[BigDecimal] = Some(1000.05)
  val totalDeductions: Option[BigDecimal] = Some(1000.06)
  val adjustedIncomeTaxLoss: Option[BigInt] = None
  val taxableProfit: Option[BigInt] = Some(1008)
  val taxableProfitAfterIncomeTaxLossesDeduction: Option[BigInt] = None
  val bsas: BusinessSourceAdjustableSummary = BusinessSourceAdjustableSummary(
    bsasId = "a54ba782-5ef4-47f4-ab72-495406665ca9",
    applied = true,
    links = None
  )

  val ukPropertyFhlModel: UkPropertyFhl =
    UkPropertyFhl(
      totalIncome = totalIncome,
      totalExpenses = totalExpenses,
      netProfit = netProfit,
      netLoss = netLoss,
      totalAdditions = totalAdditions,
      totalDeductions = totalDeductions,
      adjustedIncomeTaxLoss = adjustedIncomeTaxLoss,
      taxableProfit = taxableProfit,
      taxableProfitAfterIncomeTaxLossesDeduction = taxableProfitAfterIncomeTaxLossesDeduction,
      lossClaimsSummary = Some(lossClaimsSummaryModel),
      lossClaimsDetail = Some(lossClaimsDetailModel),
      bsas = Some(bsas)
    )

  val bsasJson: JsValue = Json.parse(
    """
      |{
      |   "bsasId": "a54ba782-5ef4-47f4-ab72-495406665ca9",
      |   "applied": true
      |}
    """.stripMargin
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
       |   "taxableProfit": ${taxableProfit.get},
       |	 "lossClaimsSummary": $lossClaimSummaryJson,
       |   "lossClaimsDetail": $lossClaimsDetailJson,
       |   "bsas": $bsasJson
       |}
     """.stripMargin
  )
}