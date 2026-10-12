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

package v8.retrieveCalculation.def1.model.response.calculation.employmentAndPensionsIncome

import api.utils.UnitSpec
import play.api.libs.json.{JsArray, JsError, JsValue, Json}

class StudentLoansSpec extends UnitSpec {

  val model: StudentLoans = StudentLoans(Some(5000.99), Some(5000.99))

  val json: JsValue = Json.parse(
    """
      |{
      |  "uglDeductionAmount": 5000.99,
      |  "pglDeductionAmount": 5000.99
      |}
      |""".stripMargin
  )

  "reads" when {
    "passed valid JSON" should {
      "return a valid model" in {
        json.as[StudentLoans] shouldBe model
      }
    }
  }

  "writes" when {
    "passed valid model" should {
      "return valid JSON" in {
        Json.toJson(model) shouldBe json
      }
    }
  }

  "format" when {
    "passed the wrong type" should {
      "return an error" in {
        Json.parse("""{"uglDeductionAmount": "FIVE"}""").validate[StudentLoans] shouldBe a[JsError]
      }
    }

    "passed an invalid JSON structure" should {
      "return an error" in {
        JsArray.empty.validate[StudentLoans] shouldBe a[JsError]
      }
    }
  }

}
