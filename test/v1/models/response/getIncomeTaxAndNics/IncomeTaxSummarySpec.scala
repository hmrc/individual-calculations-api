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

class IncomeTaxSummarySpec extends UnitSpec {

  val json: JsValue = Json.parse(
    """
      |{
      | "incomeTaxCharged" : 2000.00,
      | "incomeTaxDueAfterReliefs" : 1525.22,
      | "incomeTaxDueAfterGiftAid" : 120.10
      |}
    """.stripMargin)

  val model = IncomeTaxSummary(2000.00, Some(1525.22), Some(120.10))

  "IncomeTaxSummary" should {

    "read from json correctly" when {

      "provided with valid json" in {
        json.as[IncomeTaxSummary] shouldBe model
      }
    }

    "write to json correctly" when {

      "a valid model is provided" in {
        Json.toJson(model) shouldBe json
      }
    }
  }
}
