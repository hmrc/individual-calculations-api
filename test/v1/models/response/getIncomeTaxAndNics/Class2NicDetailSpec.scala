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

import play.api.libs.json.{JsSuccess, Json}
import support.UnitSpec

class Class2NicDetailSpec extends UnitSpec {

  val model = Class2NicDetail(Some(100.25), Some(200.25), Some(300.25), Some(400.25), true, Some(false))

  val json = Json.parse(
    """
      |{
      | "weeklyRate" : 100.25,
      | "weeks" : 200.25,
      | "limit" : 300.25,
      | "apportionedLimit" : 400.25,
      | "underSmallProfitThreshold" : true,
      | "actualClass2Nic" : false
      |}
    """.stripMargin)

  "Class2NicDetail" should {

    "write correctly to json" in {
      Json.toJson(model) shouldBe json
    }

    "read correctly from json" in {
      json.validate[Class2NicDetail] shouldBe JsSuccess(model)
    }
  }
}
