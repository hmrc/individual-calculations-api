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

package v8.retrieveCalculation.def4.model.response.calculation.savingsAndGainsIncome

import play.api.libs.json.{JsValue, Json}
import shared.models.utils.JsonErrorValidators
import shared.utils.UnitSpec
import v8.common.model.response.IncomeSourceType

class UkSavingsAndGainsIncomeSpec extends UnitSpec with JsonErrorValidators {

  val model: UkSavingsAndGainsIncome = UkSavingsAndGainsIncome(
    Some("000000000000210"),
    IncomeSourceType.`uk-savings-and-gains`,
    Some("My Savings Account 1"),
    99.99,
    Some(99.99),
    Some(99.99)
  )

  val downstreamJson: JsValue = Json.parse(
    """
      |{
      |    "incomeSourceId":"000000000000210",
      |    "incomeSourceType":"09",
      |    "incomeSourceName":"My Savings Account 1",
      |    "grossIncome":99.99,
      |    "netIncome":99.99,
      |    "taxDeducted":99.99
      |}
      |""".stripMargin
  )

  val mtdJson: JsValue = Json.parse(
    """
      |{
      |   "incomeSourceId":"000000000000210",
      |   "incomeSourceType":"uk-savings-and-gains",
      |   "incomeSourceName":"My Savings Account 1",
      |   "grossIncome":99.99,
      |   "netIncome":99.99,
      |   "taxDeducted":99.99
      |}
      |""".stripMargin
  )

  "reads" when {
    "passed valid JSON" should {
      "return a valid model" in {
        downstreamJson.as[UkSavingsAndGainsIncome] shouldBe model
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
