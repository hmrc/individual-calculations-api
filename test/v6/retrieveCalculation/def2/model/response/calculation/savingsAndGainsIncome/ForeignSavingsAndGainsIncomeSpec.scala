/*
 * Copyright 2023 HM Revenue & Customs
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

package v6.retrieveCalculation.def2.model.response.calculation.savingsAndGainsIncome

import play.api.libs.json.{JsValue, Json}
import shared.models.utils.JsonErrorValidators
import shared.utils.UnitSpec
import v6.retrieveCalculation.def2.model.response.common.IncomeSourceType.`foreign-savings-and-gains`

class ForeignSavingsAndGainsIncomeSpec extends UnitSpec with JsonErrorValidators {

  val model: ForeignSavingsAndGainsIncome = ForeignSavingsAndGainsIncome(
    `foreign-savings-and-gains`,
    Some("GER"),
    Some(5000.99),
    Some(5000.99),
    Some(5000.99),
    Some(true)
  )

  val downstreamJson: JsValue = Json.parse(
    """
      |{
      |  "incomeSourceType": "16",
      |  "countryCode": "GER",
      |  "grossIncome": 5000.99,
      |  "netIncome": 5000.99,
      |  "taxDeducted": 5000.99,
      |  "foreignTaxCreditRelief": true
      |}
      |""".stripMargin
  )

  val mtdJson: JsValue = Json.parse(
    """
      |{
      |  "incomeSourceType": "foreign-savings-and-gains",
      |  "countryCode": "GER",
      |  "grossIncome": 5000.99,
      |  "netIncome": 5000.99,
      |  "taxDeducted": 5000.99,
      |  "foreignTaxCreditRelief": true
      |}
      |""".stripMargin
  )

  "reads" when {
    "passed valid JSON" should {
      "return a valid model" in {
        downstreamJson.as[ForeignSavingsAndGainsIncome] shouldBe model
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
