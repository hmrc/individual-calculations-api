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

package v1.models.response.getIncomeTaxAndNics

import play.api.libs.json.{JsValue, Json}
import support.UnitSpec

class IncomeTaxDetailSpec extends UnitSpec {

  val json: JsValue = Json.parse(
    """
      |{
      | "payPensionsProfit" : {
      |   "allowancesAllocated": 100.25,
      |   "incomeTaxAmount" :100.50
      | },
      |   "savingsAndGains" : {
      |   "allowancesAllocated": 200.25,
      |   "incomeTaxAmount" :200.50
      | },
      | "dividends" : {
      |   "allowancesAllocated": 300.25,
      |   "incomeTaxAmount" :300.50
      | },
      | "giftAid" : {
      |   "grossGiftAidPayments" : 400.25,
      |   "rate" : 400.50,
      |   "giftAidTax" : 400.75
      | }
      |}
    """.stripMargin)

  def incomeTypeBreakdown(input: BigDecimal): IncomeTypeBreakdown = IncomeTypeBreakdown(input + 0.25, input + 0.5, None)

  val model = IncomeTaxDetail(
    Some(incomeTypeBreakdown(100)),
    Some(incomeTypeBreakdown(200)),
    Some(incomeTypeBreakdown(300)),
    Some(GiftAid(400.25, 400.50, 400.75))
  )

  "IncomeTaxDetail" should {

    "read from json correctly" when {

      "provided with valid json" in {
        json.as[IncomeTaxDetail] shouldBe model
      }
    }

    "write to json correctly" when {

      "a valid model is provided" in {
        Json.toJson(model) shouldBe json
      }
    }
  }
}
