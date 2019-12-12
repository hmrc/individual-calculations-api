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

package v1.fixtures.getTaxableIncome.detail.ukPropertyNonFhl

import play.api.libs.json.{JsObject, JsValue, Json}
import v1.fixtures.getTaxableIncome.detail.ukPropertyNonFhl.detail.LossClaimsDetailFixtures
import v1.fixtures.getTaxableIncome.detail.ukPropertyNonFhl.summary.LossClaimsSummaryFixtures
import v1.models.response.getTaxableIncome.detail.ukPropertyNonFhl.UkPropertyNonFhl

object UkPropertyNonFhlFixtures {

  def businessProfitAndLossJson(incomeSourceType: String): JsValue = Json.parse(
    s"""
      |{
      | "incomeSourceType" : "$incomeSourceType",
      | "totalIncome" : 2000.98,
      | "totalExpenses" : 2000.98,
      | "netProfit" : 2000.98,
      | "netLoss" : 2000.98,
      | "totalAdditions" : 2000.98,
      | "totalDeductions" : 2000.98,
      | "accountingAdjustments" : -2000.98,
      | "taxableProfit" : 2000,
      | "adjustedIncomeTaxLoss" : 2000,
      | "taxableProfitAfterIncomeTaxLossesDeduction" : 2000
      |}
    """.stripMargin).asInstanceOf[JsObject].deepMerge(LossClaimsSummaryFixtures.lossClaimsSummaryJson.asInstanceOf[JsObject])

  val ukPropertyNonFhlMtdJson: JsValue = Json.parse(
    s"""
      |{
      | "totalIncome" : 2000.98,
      | "totalExpenses" : 2000.98,
      | "netProfit" : 2000.98,
      | "netLoss" : 2000.98,
      | "totalAdditions" : 2000.98,
      | "totalDeductions" : 2000.98,
      | "accountingAdjustments" : -2000.98,
      | "taxableProfit" : 2000,
      | "adjustedIncomeTaxLoss" : 2000,
      | "taxableProfitAfterIncomeTaxLossesDeduction" : 2000,
      | "lossClaimsSummary" : ${LossClaimsSummaryFixtures.lossClaimsSummaryJson.toString()},
      | "lossClaimsDetail" : ${LossClaimsDetailFixtures.lossClaimsDetailMtdJson.toString()}
      |}
    """.stripMargin)

  val emptyJson: JsValue = Json.obj()

  val ukPropertyNonFhlModel = UkPropertyNonFhl(
    Some(2000.98),
    Some(2000.98),
    Some(2000.98),
    Some(2000.98),
    Some(2000.98),
    Some(2000.98),
    Some(-2000.98),
    Some(2000),
    Some(2000),
    Some(2000),
    Some(LossClaimsSummaryFixtures.lossClaimsSummaryModel),
    Some(LossClaimsDetailFixtures.lossClaimsDetailModel)
  )

  val emptyUkPropertyNonFhlModel = UkPropertyNonFhl(None, None, None, None, None, None, None, None, None, None, None, None)
}
