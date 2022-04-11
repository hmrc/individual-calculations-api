/*
 * Copyright 2022 HM Revenue & Customs
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

package v3.models.response.retrieveCalculation.calculation.savingsAndGainsIncome

import play.api.libs.json.Json
import support.UnitSpec
import v3.models.response.common.IncomeSourceType
import v3.models.utils.JsonErrorValidators

class UkSavingsAndGainsIncomeSpec extends UnitSpec with JsonErrorValidators{

  val model = UkSavingsAndGainsIncome(
    "testId",
    IncomeSourceType.`uk-savings-and-gains`,
  "")

  "reads" when {
    "passed valid JSON" should {
      "return a valid model" in {
        foreignIncomeTaxCreditRelief shouldBe json.as[ForeignIncomeTaxCreditRelief]
      }
    }
  }
  "writes" when {
    "passed valid model" should {
      "return valid JSON" in {
        Json.toJson(foreignIncomeTaxCreditRelief) shouldBe json
      }
    }
  }
}
