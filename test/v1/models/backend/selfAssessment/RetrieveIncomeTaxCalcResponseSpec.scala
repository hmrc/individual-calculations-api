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

package v1.models.backend.selfAssessment

import play.api.libs.json.{JsSuccess, JsValue, Json}
import support.UnitSpec

class RetrieveIncomeTaxCalcResponseSpec extends  UnitSpec {

  val summaryModel = CalculationSummary("")
  val detailModel = CalculationDetail("")
  val model = RetrieveIncomeTaxCalcResponse(summaryModel, detailModel)

  val json: JsValue = Json.parse(
    s"""
      |{
      | "summary" : ${Json.toJson(summaryModel).toString()},
      | "detail" : ${Json.toJson(detailModel).toString()}
      |}
    """.stripMargin)

  "CalculationDetail" should {

    "write to json correctly" in {
      Json.toJson(model) shouldBe json
    }

    "read from json correctly" in {
      json.validate[RetrieveIncomeTaxCalcResponse] shouldBe JsSuccess(model)
    }
  }

}
