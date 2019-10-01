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

class NicBandSpec extends UnitSpec {
  val json: JsValue = Json.parse(
    s"""
       |				{
       |					"name": "name",
       |					"rate": 100.25,
       |					"threshold": 200.25,
       |					"apportionedThreshold": 300.25,
       |					"income": 400.25,
       |					"amount": 500.25
       |				}
           """.stripMargin)

  val model = NicBand(
    name = "name",
    rate = 100.25,
    threshold = Some(200.25),
    apportionedThreshold = Some(300.25),
    income = 400.25,
    amount = 500.25
  )

  "NicBand" should {

    "read from json correctly" when {

      "provided with valid json" in {
        json.as[NicBand] shouldBe model
      }
    }

    "write to json correctly" when {

      "a valid model is provided" in {
        Json.toJson(model) shouldBe json
      }
    }
  }
}
