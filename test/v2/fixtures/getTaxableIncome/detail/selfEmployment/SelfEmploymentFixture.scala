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

package v2.fixtures.getTaxableIncome.detail.selfEmployment

import play.api.libs.json.{JsValue, Json}
import v2.fixtures.getTaxableIncome.detail.selfEmployment.detail.LossClaimsDetailFixture._
import v2.fixtures.getTaxableIncome.detail.selfEmployment.summary.LossClaimSummaryFixture._
import v2.models.response.getTaxableIncome.detail.selfEmployment.SelfEmployment
import v2.models.response.getTaxableIncome.detail.selfEmployment.detail.BusinessSourceAdjustableSummary

object SelfEmploymentFixture {

  val selfEmploymentId: String = "AAIS12345678904"
  val totalIncome: Option[BigDecimal] = Some(79291394)
  val totalExpenses: Option[BigDecimal] = Some(89005890)
  val netProfit: Option[BigDecimal] = Some(93480427)
  val netLoss: Option[BigDecimal] = Some(10017816)
  val class4Loss: Option[BigInt] = Some(2)
  val totalAdditions: Option[BigDecimal] = Some(39901282)
  val totalDeductions: Option[BigDecimal] = Some(80648172)
  val accountingAdjustments: Option[BigDecimal] = Some(-8769926.99)
  val taxableProfit: Option[BigInt] = Some(92149284)
  val adjustedIncomeTaxLoss: Option[BigDecimal] = Some(2)
  val taxableProfitAfterIncomeTaxLossesDeduction: Option[BigInt] = Some(2)
  val bsas: BusinessSourceAdjustableSummary = BusinessSourceAdjustableSummary(
    bsasId = "a54ba782-5ef4-47f4-ab72-495406665ca9",
    applied = true,
    links = None)

  val selfEmploymentModel: SelfEmployment =
    SelfEmployment(
      selfEmploymentId = selfEmploymentId,
      totalIncome = totalIncome,
      totalExpenses = totalExpenses,
      netProfit = netProfit,
      netLoss = netLoss,
      class4Loss = class4Loss,
      totalAdditions = totalAdditions,
      totalDeductions = totalDeductions,
      accountingAdjustments = accountingAdjustments,
      adjustedIncomeTaxLoss = adjustedIncomeTaxLoss,
      taxableProfit = taxableProfit,
      taxableProfitAfterIncomeTaxLossesDeduction = taxableProfitAfterIncomeTaxLossesDeduction,
      lossClaimsSummary = Some(lossClaimsSummaryModel),
      lossClaimsDetail = Some(lossClaimsDetailDefaultResponse),
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

  val selfEmploymentJson: JsValue = Json.parse(
    s"""
       |{
       |    "selfEmploymentId": "$selfEmploymentId",
       |    "totalIncome": ${totalIncome.get},
       |    "totalExpenses": ${totalExpenses.get},
       |    "netProfit": ${netProfit.get},
       |    "netLoss": ${netLoss.get},
       |    "class4Loss": ${class4Loss.get},
       |    "totalAdditions": ${totalAdditions.get},
       |    "totalDeductions": ${totalDeductions.get},
       |    "accountingAdjustments": ${accountingAdjustments.get},
       |    "adjustedIncomeTaxLoss": ${adjustedIncomeTaxLoss.get},
       |    "taxableProfit": ${taxableProfit.get},
       |    "taxableProfitAfterIncomeTaxLossesDeduction": ${taxableProfitAfterIncomeTaxLossesDeduction.get},
       |    "lossClaimsSummary" : $lossClaimSummaryJson,
       |    "lossClaimsDetail" : $lossClaimsDetailJson,
       |    "bsas": $bsasJson
       |}
     """.stripMargin
  )
}