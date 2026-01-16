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

package v8.retrieveCalculation.def1.model.response

import play.api.libs.json.Json
import shared.models.utils.JsonErrorValidators
import shared.utils.UnitSpec
import v8.retrieveCalculation.def1.model.Def1_CalculationFixture
import v8.retrieveCalculation.models.response.Def1_RetrieveCalculationResponse

class Def1_RetrieveCalculationResponseSpec extends UnitSpec with Def1_CalculationFixture with JsonErrorValidators {

  "RetrieveCalculationResponse" must {
    "allow conversion from downstream JSON to MTD JSON" when {
      "JSON contains every field" in {
        val model = calculationDownstreamJson.as[Def1_RetrieveCalculationResponse]
        Json.toJson(model) shouldBe calculationMtdJson
      }
    }

    "have the correct fields optional" when {
      testAllOptionalJsonFieldsExcept[Def1_RetrieveCalculationResponse](calculationDownstreamJson)("metadata", "inputs")
    }

    "return the correct TaxDeductedAtSource" in {
      taxDeductedAtSource.withoutTaxTakenOffTradingIncome shouldBe taxDeductedAtSource.copy(taxTakenOffTradingIncome = None)
    }

    "return hasErrors is false when messages.errors is empty" in {
      val model = calculationDownstreamJson
        .deepMerge(
          Json.obj(
            "messages" -> Json.obj(
              "errors" -> Json.arr()
            )
          ))
        .as[Def1_RetrieveCalculationResponse]

      model.hasErrors shouldBe false
    }

    "allow messages to be missing" in {
      val jsonWithoutMessages =
        calculationDownstreamJson - "messages"

      val model = jsonWithoutMessages.as[Def1_RetrieveCalculationResponse]
      model.messages shouldBe None
      model.hasErrors shouldBe false
    }

  }

}
