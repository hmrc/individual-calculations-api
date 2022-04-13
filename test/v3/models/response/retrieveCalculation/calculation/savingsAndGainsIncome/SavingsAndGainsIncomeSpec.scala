/*
 * Copyright 2022 HM Revenue & Customs
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

package v3.models.response.retrieveCalculation.calculation.savingsAndGainsIncome

import play.api.libs.json.{JsValue, Json}
import support.UnitSpec
import v3.models.response.common.IncomeSourceType
import v3.models.utils.JsonErrorValidators

class SavingsAndGainsIncomeSpec extends UnitSpec with JsonErrorValidators {

  val ukModel: UkSavingsAndGainsIncome = UkSavingsAndGainsIncome(
    Some("000000000000210"),
    IncomeSourceType.`uk-savings-and-gains`,
    Some("My Savings Account 1"),
    5000.99,
    Some(5000.99),
    Some(5000.99)
  )

  val foreignModel: ForeignSavingsAndGainsIncome = ForeignSavingsAndGainsIncome(
    IncomeSourceType.`foreign-savings-and-gains`,
    Some("GER"),
    Some(5000.99),
    Some(5000.99),
    Some(5000.99),
    Some(true)
  )

  val model: SavingsAndGainsIncome = SavingsAndGainsIncome(
    Some(100),
    Some(100),
    Some(Seq(ukModel)),
    Some(100),
    Some(Seq(foreignModel))
  )

  val downstreamJson: JsValue = Json.parse(
    """
      |{
      |  "totalChargeableSavingsAndGains": 100,
      |  "totalUkSavingsAndGains": 100,
      |  "ukSavingsAndGainsIncome": [
      |    {
      |      "incomeSourceId": "000000000000210",
      |      "incomeSourceType": "09",
      |      "incomeSourceName": "My Savings Account 1",
      |      "grossIncome": 5000.99,
      |      "netIncome": 5000.99,
      |      "taxDeducted": 5000.99
      |    }
      |  ],
      |  "chargeableForeignSavingsAndGains": 100,
      |  "foreignSavingsAndGainsIncome": [
      |    {
      |      "incomeSourceType": "16",
      |      "countryCode": "GER",
      |      "grossIncome": 5000.99,
      |      "netIncome": 5000.99,
      |      "taxDeducted": 5000.99,
      |      "foreignTaxCreditRelief": true
      |    }
      |  ]
      |}
      |""".stripMargin
  )

  val mtdJson: JsValue = Json.parse(
    """
      |{
      |  "totalChargeableSavingsAndGains": 100,
      |  "totalUkSavingsAndGains": 100,
      |  "ukSavingsAndGainsIncome": [
      |    {
      |      "incomeSourceId": "000000000000210",
      |      "incomeSourceType": "uk-savings-and-gains",
      |      "incomeSourceName": "My Savings Account 1",
      |      "grossIncome": 5000.99,
      |      "netIncome": 5000.99,
      |      "taxDeducted": 5000.99
      |    }
      |  ],
      |  "chargeableForeignSavingsAndGains": 100,
      |  "foreignSavingsAndGainsIncome": [
      |    {
      |      "incomeSourceType": "foreign-savings-and-gains",
      |      "countryCode": "GER",
      |      "grossIncome": 5000.99,
      |      "netIncome": 5000.99,
      |      "taxDeducted": 5000.99,
      |      "foreignTaxCreditRelief": true
      |    }
      |  ]
      |}
      |""".stripMargin
  )

  "reads" when {
    "passed valid JSON" should {
      "return a valid model" in {
        downstreamJson.as[SavingsAndGainsIncome] shouldBe model
      }
    }
  }
  "writes" when {
    "passed valid model" should {
      "return valid JSON" in {
        Json.toJson(model) shouldBe mtdJson
      }
    }
  }
}
