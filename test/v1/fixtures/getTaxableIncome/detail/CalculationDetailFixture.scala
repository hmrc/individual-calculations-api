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

package v1.fixtures.getTaxableIncome.detail

import play.api.libs.json.{JsValue, Json}
import v1.fixtures.getTaxableIncome.detail.DividendsFixture._
import v1.fixtures.getTaxableIncome.detail.GainsOnLifePoliciesFixture._
import v1.fixtures.getTaxableIncome.detail.LumpSumsFixture._
import v1.fixtures.getTaxableIncome.detail.PayPensionsProfitFixture._
import v1.fixtures.getTaxableIncome.detail.SavingsAndGainsFixture._
import v1.models.response.getTaxableIncome.detail.CalculationDetail

object CalculationDetailFixture {

  val calculationDetailModel: CalculationDetail =
    CalculationDetail(
      payPensionsProfit = Some(payPensionsProfitModel),
      savingsAndGains = Some(savingsAndGainsModel),
      dividends = Some(dividendsModel),
      lumpSums = Some(lumpSumsModel),
      gainsOnLifePolicies = Some(gainsOnLifePoliciesModel)
    )

  val calculationDetailJson: JsValue = Json.parse(
    s"""
       |{
       |   "payPensionsProfit" : $payPensionsProfitJson,
       |   "savingsAndGains" : $savingsAndGainsJson,
       |   "dividends" : $dividendsJson,
       |   "lumpSums" : $lumpSumsJson,
       |   "gainsOnLifePolicies" : $gainsOnLifePoliciesJson
       |}
    """.stripMargin
  )
}
