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

package v1.models.response.getIncomeTaxCalc

import play.api.libs.json.{JsValue, Json}
import support.UnitSpec

class IncomeTypeBreakdownSpec extends UnitSpec {
  val json: JsValue = Json.parse(
    """
      |{
      | "allowancesAllocated" : 100.25,
      | "incomeTaxAmount" : 200.25,
      | "taxBands" : [
      |   {
      |     "name" : "name",
      |     "rate" : 300.25,
      |     "threshold" : 400.25,
      |     "apportionedThreshold" : 500.25,
      |     "bandLimit" : 600.25,
      |     "apportionedBandLimit" : 700.25,
      |     "income" : 800.25,
      |     "amount" : 900.25
      |   }
      | ]
      |}
    """.stripMargin)

  val model = IncomeTypeBreakdown(100.25, 200.25,
    Seq(TaxBand("name", 300.25, Some(400.25), Some(500.25), Some(600.25), Some(700.25), 800.25, 900.25)))

  "IncomeTaxSummary" should {

    "read from json correctly" when {

      "provided with valid json" in {
        json.as[IncomeTypeBreakdown] shouldBe model
      }
    }

    "write to json correctly" when {

      "a valid model is provided" in {
        Json.toJson(model) shouldBe json
      }
    }
  }
}
