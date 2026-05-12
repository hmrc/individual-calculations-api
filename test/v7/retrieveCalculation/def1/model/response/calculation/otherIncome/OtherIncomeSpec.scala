/*
 * Copyright 2026 HM Revenue & Customs
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

package v7.retrieveCalculation.def1.model.response.calculation.otherIncome

import play.api.libs.json.{JsValue, Json}
import shared.utils.UnitSpec
import v7.retrieveCalculation.def1.model.Def1_CalculationFixture

class OtherIncomeSpec extends UnitSpec with Def1_CalculationFixture {

  val model: OtherIncome = otherIncome

  val downstreamJson: JsValue = Json.parse(
    """
      |{
      |      "totalOtherIncome": 2000.00,
      |      "postCessationIncome": {
      |        "totalPostCessationReceipts": 2000.00,
      |        "postCessationReceipts": [
      |          {
      |            "amount": 100.00,
      |            "taxYearIncomeToBeTaxed": "2019-20"
      |          }
      |        ]
      |      }
      |    }
      |""".stripMargin
  )

  val mtdJson: JsValue = Json.parse(
    """
      |{
      |      "totalOtherIncome": 2000.00,
      |      "postCessationIncome": {
      |        "totalPostCessationReceipts": 2000.00,
      |        "postCessationReceipts": [
      |          {
      |            "amount": 100.00,
      |            "taxYearIncomeToBeTaxed": "2019-20"
      |          }
      |        ]
      |      }
      |    }
      |""".stripMargin
  )

  "reads" when {
    "passed valid JSON" should {
      "return a valid model" in {
        downstreamJson.as[OtherIncome] shouldBe model
      }
    }
  }

  "writes" when {
    "passed valid model" should {
      "return valid JSON" in {
        Json.toJson(model) shouldBe mtdJson
      }
    }
  }

}
