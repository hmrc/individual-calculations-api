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
import v1.models.response.getIncomeTaxAndNics.detail.TaxDeductedAtSource

class TaxDeductedAtSourceSpec extends UnitSpec {

  val json: JsValue = Json.parse(
    """
      |{
      | "ukLandAndProperty" : 100.25,
      | "savings" : 200.25
      |}
    """.stripMargin)

  val model = TaxDeductedAtSource(Some(100.25), Some(200.25))

  "TaxDeductedAtSource" should {

    "read correctly from json" in {
      json.validate[TaxDeductedAtSource] shouldBe JsSuccess(model)
    }

    "write correctly to json" in {
      Json.toJson(model) shouldBe json
    }
  }
}
