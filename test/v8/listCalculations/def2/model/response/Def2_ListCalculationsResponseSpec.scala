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

package v8.listCalculations.def2.model.response

import play.api.libs.json.Json
import shared.utils.UnitSpec
import v8.listCalculations.def2.model.Def2_ListCalculationsFixture
import v8.listCalculations.model.response.Def2_ListCalculationsResponse


class Def2_ListCalculationsResponseSpec extends UnitSpec with Def2_ListCalculationsFixture {

  "ListCalculationsResponse" when {
    "read from downstream JSON" must {
      "return the expected data model" in {
        listCalculationsDownstreamJson.as[Def2_ListCalculationsResponse[Def2_Calculation]] shouldBe listCalculationsResponseModel
      }
    }

    "written to MTD JSON" must {
      "produce the expected JSON body" in {
        Json.toJson(listCalculationsResponseModel) shouldBe listCalculationsMtdJson
      }
    }
  }
}
