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

import play.api.libs.json.{JsSuccess, JsValue, Json}
import support.UnitSpec

class GetIncomeTaxCalcResponseSpec extends  UnitSpec {

  val incomeTaxSummary = IncomeTaxSummary(100.25, None, None)
  val incomeTaxDetail = IncomeTaxDetail(Some(IncomeTypeBreakdown(200.25, 300.25, None)), None, None, None)
  val summaryModel = CalculationSummary(incomeTaxSummary, None, None, None, 400.25, "UK")
  val detailModel = CalculationDetail(incomeTaxDetail, None, None)
  val model = GetIncomeTaxCalcResponse(summaryModel, detailModel)

  val outputJson: JsValue = Json.parse(
    s"""
      |{
      | "summary" : ${Json.toJson(summaryModel).toString()},
      | "detail" : ${Json.toJson(detailModel).toString()}
      |}
    """.stripMargin)

  val inputJson: JsValue = Json.parse(
    s"""
       |{
       | "incomeTax": {
       |   "summary" : ${Json.toJson(summaryModel).toString()},
       |   "detail" : ${Json.toJson(detailModel).toString()}
       | }
       |}
    """.stripMargin)

  "CalculationDetail" should {

    "write to json correctly" in {
      Json.toJson(model) shouldBe outputJson
    }

    "read from json correctly" in {
      inputJson.as[GetIncomeTaxCalcResponse] shouldBe model
    }
  }

}
