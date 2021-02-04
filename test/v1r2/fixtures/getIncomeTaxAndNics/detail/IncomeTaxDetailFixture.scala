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

package v1r2.fixtures.getIncomeTaxAndNics.detail

import play.api.libs.json.{JsValue, Json}
import v1r2.fixtures.getIncomeTaxAndNics.detail.GiftAidFixture._
import v1r2.fixtures.getIncomeTaxAndNics.detail.IncomeTypeBreakdownFixture._
import v1r2.models.response.getIncomeTaxAndNics.detail.IncomeTaxDetail

object IncomeTaxDetailFixture {

  def incomeTaxDetailModel: IncomeTaxDetail =
    IncomeTaxDetail(
      payPensionsProfit = Some(incomeTypeBreakdownModel(100)),
      savingsAndGains = Some(incomeTypeBreakdownModel(200)),
      lumpSums = Some(incomeTypeBreakdownModel(300)),
      dividends = Some(incomeTypeBreakdownModel(400)),
      gainsOnLifePolicies = Some(incomeTypeBreakdownModel(500)),
      giftAid = Some(giftAidModel)
    )

  def incomeTaxDetailJson: JsValue = Json.parse(
    s"""
       |{
       |   "payPensionsProfit": ${incomeTypeBreakdownJson(100)},
       |   "savingsAndGains": ${incomeTypeBreakdownJson(200)},
       |   "lumpSums": ${incomeTypeBreakdownJson(300)},
       |   "dividends": ${incomeTypeBreakdownJson(400)},
       |   "gainsOnLifePolicies": ${incomeTypeBreakdownJson(500)},
       |   "giftAid": $giftAidJson
       |}
     """.stripMargin
  )
}