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

package v1.fixtures.taxableIncome.detail

import play.api.libs.json.{JsObject, JsValue, Json}
import v1.fixtures.taxableIncome.detail.DividendsFixtures._
import v1.fixtures.taxableIncome.detail.PayPensionsProfitFixtures._
import v1.fixtures.taxableIncome.detail.SavingsAndGainsFixtures._
import v1.models.response.taxableIncome.detail.CalculationDetail

object CalculationDetailFixtures {
  val detailResponse           = CalculationDetail(Some(payPensionsProfitResponse), Some(savingsAndGainsResponse), Some(dividendsResponse))
  val detailResponseWithoutPPP = CalculationDetail(None, Some(savingsAndGainsResponse), Some(dividendsResponse))
  val detailResponseWithoutSAG = CalculationDetail(Some(payPensionsProfitResponse), None, Some(dividendsResponse))
  val detailResponseWithoutDiv = CalculationDetail(Some(payPensionsProfitResponse), Some(savingsAndGainsResponse), None)

  val dividendsJsonComponent: JsObject         = Json.obj("dividends"         -> dividendsJson)
  val savingsAndGainsJsonComponent: JsObject   = Json.obj("savingsAndGains"   -> savingsAndGainsJson)
  val payPensionsProfitJsonComponent: JsObject = Json.obj("payPensionsProfit" -> payPensionsProfitJson)

  val detailJson: JsValue           = dividendsJsonComponent.deepMerge(savingsAndGainsJsonComponent).deepMerge(payPensionsProfitJsonComponent)
  val detailJsonWithoutPPP: JsValue = dividendsJsonComponent.deepMerge(savingsAndGainsJsonComponent)
  val detailJsonWithoutSAG: JsValue = dividendsJsonComponent.deepMerge(payPensionsProfitJsonComponent)
  val detailJsonWithoutDiv: JsValue = savingsAndGainsJsonComponent.deepMerge(payPensionsProfitJsonComponent)

  val emptyDetailResponse = CalculationDetail(None, None, None)

}
