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

class NicDetailSpec extends UnitSpec {

  val json: JsValue = Json.parse(
    s"""
       |{
       |  "class2Nics" : {
       |    "weeklyRate" : 100.10,
       |    "weeks" : 100.20,
       |    "limit" : 100.30,
       |    "apportionedLimit" : 100.40,
       |    "underSmallProfitThreshold" : true,
       |    "actualClass2Nic" : false
       |  },
       |  "class4NicBands" : [
       |    {
       |      "name" : "name",
       |      "rate" : 200.10,
       |      "threshold" : 200.20,
       |      "apportionedThreshold" : 200.30,
       |      "income" : 200.40,
       |      "amount" : 200.50
       |    }
       |  ]
       |}
           """.stripMargin)

  val model = NicDetail(
    Some(Class2NicDetail(Some(100.10), Some(100.20), Some(100.30), Some(100.40), true, Some(false))),
    Some(Seq(NicBand("name", 200.10, Some(200.20), Some(200.30), 200.40, 200.50)))
  )

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
