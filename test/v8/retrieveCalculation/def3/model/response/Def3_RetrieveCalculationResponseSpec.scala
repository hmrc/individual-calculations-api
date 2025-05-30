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

package v8.retrieveCalculation.def3.model.response

import org.scalatest.Inside
import play.api.libs.json.Json
import shared.models.utils.JsonErrorValidators
import shared.utils.UnitSpec
import v8.retrieveCalculation.def3.model.Def3_CalculationFixture
import v8.retrieveCalculation.models.response.Def3_RetrieveCalculationResponse

class Def3_RetrieveCalculationResponseSpec extends UnitSpec with Def3_CalculationFixture with JsonErrorValidators with Inside {

  "Def3_RetrieveCalculationResponse" must {
    "allow conversion from downstream JSON to MTD JSON" when {
      "JSON contains every field" in {
        val model = calculationDownstreamJson.as[Def3_RetrieveCalculationResponse]
        Json.toJson(model) shouldBe calculationMtdJson
      }
    }

    "have the correct fields optional" when {
      testAllOptionalJsonFieldsExcept[Def3_RetrieveCalculationResponse](calculationDownstreamJson)("metadata", "inputs")
    }
  }

}
