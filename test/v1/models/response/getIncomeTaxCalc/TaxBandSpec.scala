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

class TaxBandSpec extends UnitSpec {

  val json: JsValue = Json.parse(
    """
      |{
      | "name": "name",
      | "rate": 100.25,
      | "bandLimit" : 400.25,
      | "apportionedBandLimit" : 500.25,
      | "income" : 600.25,
      | "taxAmount" : 700.25
      |}
    """.stripMargin)

  val model =
    TaxBand(
      name = "name",
      rate = 100.25,
      bandLimit = 400.25,
      apportionedBandLimit = 500.25,
      income = 600.25,
      taxAmount = 700.25
    )

  "TaxBand" should {

    "write correctly to json" in {
      Json.toJson(model) shouldBe json
    }

    "read correctly from json" in {
      json.validate[TaxBand] shouldBe JsSuccess(model)
    }
  }
}
