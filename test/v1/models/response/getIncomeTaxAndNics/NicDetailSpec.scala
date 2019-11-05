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
import v1.models.response.getIncomeTaxAndNics.detail.{Class2NicDetail, Class4Losses, Class4NicDetail, NicBand, NicDetail}

class NicDetailSpec extends UnitSpec {

  val json: JsValue = Json.parse(
    s"""{
       |	"class2Nics": {
       |		"weeklyRate": 100.1,
       |		"weeks": 100.2,
       |		"limit": 100.3,
       |		"apportionedLimit": 100.4,
       |		"underSmallProfitThreshold": true,
       |		"actualClass2Nic": false
       |	},
       |	"class4Nics": {
       |		"class4Losses": {
       |			"totalClass4LossesAvailable": 3001,
       |			"totalClass4LossesUsed": 3002
       |		},
       |		"totalIncomeLiableToClass4Charge": 3003,
       |		"totalIncomeChargeableToClass4": 3004,
       |		"class4NicBands": [{
       |			"name": "name",
       |			"rate": 100.25,
       |			"threshold": 200,
       |			"apportionedThreshold": 300,
       |			"income": 400,
       |			"amount": 500.25
       |		}]
       |	}
       |}""".stripMargin)


  val class4 = Class4NicDetail(
    Some(Class4Losses(Some(3001), Some(3002))),
    Some(3003),
    Some(3004),
    Some(
      Seq(NicBand(
        name = "name",
        rate = 100.25,
        threshold = Some(200),
        apportionedThreshold = Some(300),
        income = 400,
        amount = 500.25
      )))
  )

  val model: NicDetail = NicDetail(
    Some(Class2NicDetail(Some(100.10), Some(100.20), Some(100.30), Some(100.40), underSmallProfitThreshold = true, Some(false))),
    Some(class4))

  "NicDetail" should {

    "read from json correctly" when {

      "provided with valid json" in {
        json.as[NicDetail] shouldBe model
      }
    }

    "write to json correctly" when {

      "a valid model is provided" in {
        Json.toJson(model) shouldBe json
      }
    }
  }
}
