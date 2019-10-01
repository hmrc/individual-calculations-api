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

import play.api.libs.json.{JsSuccess, JsValue, Json}
import support.UnitSpec

class CalculationSummarySpec extends UnitSpec {

  val incomeTaxSummary = IncomeTaxSummary(100.25, None, None)
  val nicSummary = NicSummary(Some(200.25), None, None)

  val json: JsValue = Json.parse(
    s"""
      |{
      | "incomeTax" : ${Json.toJson(incomeTaxSummary).toString()},
      | "nics" : ${Json.toJson(nicSummary).toString()},
      | "totalIncomeTaxNicsCharged" : 300.25,
      | "totalTaxDeducted" : 400.25,
      | "totalIncomeTaxAndNicsDue" : 500.25,
      | "taxRegime" : "UK"
      |}
    """.stripMargin)

  val model = CalculationSummary(incomeTaxSummary, Some(nicSummary), Some(300.25), Some(400.25), 500.25, "UK")

  "CalculationSummary" should {

    "write to json correctly" in {
      Json.toJson(model) shouldBe json
    }

    "read from json correctly" in {
      json.validate[CalculationSummary] shouldBe JsSuccess(model)
    }
  }
}
