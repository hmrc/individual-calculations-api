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

package v1.models.response.getAllowancesDeductionsAndReliefs

import play.api.libs.json.Json
import support.UnitSpec
import v1.fixtures.AllowancesDeductionsAndReliefsFixture._

class AllowancesDeductionsAndReliefsSpec extends UnitSpec {

  "reading from json" must {
    "read from allowancesDeductionsAndReliefs object of backend response" in {
      val responseJson = Json.obj("allowancesDeductionsAndReliefs" -> allowancesDeductionsAndReliefsJson)

      responseJson.as[AllowancesDeductionsAndReliefs] shouldBe allowancesDeductionsAndReliefsModel
    }
  }

  "writing to Json" must {
    "work as per example in tech spec" in {
      Json.toJson(allowancesDeductionsAndReliefsModel) shouldBe allowancesDeductionsAndReliefsJson
    }
  }

  "isEmpty" should {
    def responseWithSummary(summary: CalculationSummary): AllowancesDeductionsAndReliefs =
      AllowancesDeductionsAndReliefs(summary = summary, detail = CalculationDetail(None, None))

    "return true" when {
      "summary totalAllowancesAndDeductions AND totalReliefs are not present" in {
        responseWithSummary(CalculationSummary(None, None)).isEmpty shouldBe true
      }

      "summary totalAllowancesAndDeductions AND totalReliefs are present and both less than 1" in {
        responseWithSummary(CalculationSummary(Some(0), Some(0))).isEmpty shouldBe true
      }

      "either of summary totalAllowancesAndDeductions OR totalReliefs is present but less than 1" in {
        responseWithSummary(CalculationSummary(Some(0), None)).isEmpty shouldBe true
        responseWithSummary(CalculationSummary(None, Some(0))).isEmpty shouldBe true
      }
    }

    "return false" when {
      "either of summary totalAllowancesAndDeductions AND totalReliefs is present and at least 1" in {
        responseWithSummary(CalculationSummary(Some(1), None)).isEmpty shouldBe false
        responseWithSummary(CalculationSummary(None, Some(1))).isEmpty shouldBe false
      }

      "summary totalAllowancesAndDeductions AND totalReliefs are present both are at least one" in {
        responseWithSummary(CalculationSummary(Some(1), Some(1))).isEmpty shouldBe false
      }
    }
  }
}
