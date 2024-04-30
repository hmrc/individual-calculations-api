/*
 * Copyright 2023 HM Revenue & Customs
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

package v5.retrieveCalculation.def1.model.response.calculation.dividendsIncome

import api.models.utils.JsonErrorValidators
import play.api.libs.json.{JsValue, Json}
import support.UnitSpec
import v5.retrieveCalculation.def1.model.response.common.IncomeSourceType

class UkDividendsSpec extends UnitSpec with JsonErrorValidators {

  val model: UkDividends = UkDividends(
    Some("000000000000210"),
    Some(IncomeSourceType.`uk-dividends`),
    Some(5000),
    Some(5000)
  )

  val downstreamJson: JsValue = Json.parse(
    """
      |{
      |  "incomeSourceId": "000000000000210",
      |  "incomeSourceType": "10",
      |  "dividends": 5000,
      |  "otherUkDividends": 5000
      |}
      |""".stripMargin
  )

  val mtdJson: JsValue = Json.parse(
    """
      |{
      |  "incomeSourceId": "000000000000210",
      |  "incomeSourceType": "uk-dividends",
      |  "dividends": 5000,
      |  "otherUkDividends": 5000
      |}
      |""".stripMargin
  )

  "reads" when {
    "passed valid JSON" should {
      "return a valid model" in {
        downstreamJson.as[UkDividends] shouldBe model
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
