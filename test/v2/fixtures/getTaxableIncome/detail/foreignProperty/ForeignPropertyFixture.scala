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

package v2.fixtures.getTaxableIncome.detail.foreignProperty

import play.api.libs.json.{JsValue, Json}
import v2.fixtures.getTaxableIncome.detail.foreignProperty.detail.LossClaimsDetailFixture._
import v2.fixtures.getTaxableIncome.detail.foreignProperty.summary.LossClaimSummaryFixture._
import v2.models.response.getTaxableIncome.detail.foreignProperty.ForeignProperty
import v2.models.response.getTaxableIncome.detail.foreignProperty.detail.BusinessSourceAdjustableSummary

object ForeignPropertyFixture {

  val totalIncome: Option[BigDecimal] = Some(2000.98)
  val totalExpenses: Option[BigDecimal] = Some(2000.98)
  val netProfit: Option[BigDecimal] = Some(2000.98)
  val netLoss: Option[BigDecimal] = Some(2000.98)
  val totalAdditions: Option[BigDecimal] = Some(2000.98)
  val totalDeductions: Option[BigDecimal] = Some(2000.98)
  val accountingAdjustments: Option[BigDecimal] = Some(-2000.98)
  val adjustedIncomeTaxLoss: Option[BigInt] = Some(2000)
  val taxableProfit: Option[BigInt] = Some(2000)
  val taxableProfitAfterIncomeTaxLossesDeduction: Option[BigInt] = Some(2000)
  val bsas: BusinessSourceAdjustableSummary = BusinessSourceAdjustableSummary(
    bsasId = "a54ba782-5ef4-47f4-ab72-495406665ca9",
    applied = true,
    links = None)

  val foreignPropertyModel: ForeignProperty =
    ForeignProperty(
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

  val foreignPropertyJson: JsValue = Json.parse(
    s"""
       |{
       |   "totalIncome" : 2000.98,
       |   "totalExpenses" : 2000.98,
       |   "netProfit" : 2000.98,
       |   "netLoss" : 2000.98,
       |  "totalAdditions" : 2000.98,
       |   "totalDeductions" : 2000.98,
       |   "accountingAdjustments" : -2000.98,
       |   "taxableProfit" : 2000,
       |   "adjustedIncomeTaxLoss" : 2000,
       |   "taxableProfitAfterIncomeTaxLossesDeduction" : 2000,
       |   "lossClaimsSummary" : $lossClaimSummaryJson,
       |   "lossClaimsDetail" : $lossClaimsDetailJson,
       |   "bsas": $bsasJson
       |}
     """.stripMargin
  )
}