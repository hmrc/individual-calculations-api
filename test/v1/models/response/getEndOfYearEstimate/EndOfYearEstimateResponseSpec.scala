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

package v1.models.response.getEndOfYearEstimate

import play.api.libs.json.{JsSuccess, Json}
import support.UnitSpec
import v1.fixtures.getEndOfYearEstimate.EndOfYearEstimateResponseFixture

class EndOfYearEstimateResponseSpec extends UnitSpec {

  "EndOfYearEstimate" should {

    "read correctly from json" when {

      "provided with a valid json source" in {
        val result = EndOfYearEstimateResponseFixture.backendJson.validate[EndOfYearEstimateResponse]
        result shouldBe a[JsSuccess[_]]
        result.get shouldBe EndOfYearEstimateResponseFixture.model
      }
    }

    "write correctly to json" when {

      "provided with a valid model" in {
        Json.toJson(EndOfYearEstimateResponseFixture.model) shouldBe EndOfYearEstimateResponseFixture.outputJson
      }
    }
  }
}
