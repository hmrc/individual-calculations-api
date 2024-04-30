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

package v5.retrieveCalculation.def1.model.response

import api.models.utils.JsonErrorValidators
import play.api.libs.json.Json
import support.UnitSpec
import v5.retrieveCalculation.def1.model.Def1_CalculationFixture
import v5.retrieveCalculation.models.response.Def1_RetrieveCalculationResponse

class Def1_RetrieveCalculationResponseSpec extends UnitSpec with Def1_CalculationFixture with JsonErrorValidators {

  "RetrieveCalculationResponse" must {
    "allow conversion from downstream JSON to MTD JSON" when {
      "JSON contains every field" in {
        val model = calculationDownstreamJson.as[Def1_RetrieveCalculationResponse]
        Json.toJson(model) shouldBe calculationMtdJson
      }
    }

    "have the correct fields optional" when {
      testJsonAllPropertiesOptionalExcept[Def1_RetrieveCalculationResponse](calculationDownstreamJson)("metadata", "inputs")
    }

    "return the correct TaxDeductedAtSource" in {
      taxDeductedAtSource.withoutTaxTakenOffTradingIncome shouldBe taxDeductedAtSource.copy(taxTakenOffTradingIncome = None)
    }

  }

}
