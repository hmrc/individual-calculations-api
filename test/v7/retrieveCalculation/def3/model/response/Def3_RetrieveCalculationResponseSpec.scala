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

package v7.retrieveCalculation.def3.model.response

import config.CalculationsFeatureSwitches
import org.scalatest.Inside
import play.api.Configuration
import play.api.libs.json.Json
import shared.models.utils.JsonErrorValidators
import shared.utils.UnitSpec
import v7.retrieveCalculation.def3.model.Def3_CalculationFixture
import v7.retrieveCalculation.def3.model.response.calculation.Calculation
import v7.retrieveCalculation.def3.model.response.calculation.transitionProfit.TransitionProfit
import v7.retrieveCalculation.models.response.Def3_RetrieveCalculationResponse

class Def3_RetrieveCalculationResponseSpec extends UnitSpec with Def3_CalculationFixture with JsonErrorValidators with Inside {

  "Def2_RetrieveCalculationResponse" must {
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

  "Def2_RetrieveCalculationResponse adjustFields" when {
    val fullResponse = calculationDownstreamJson.as[Def3_RetrieveCalculationResponse]

    val ignoredTaxYear = "ignoredTaxYear"

    def featureSwitchesWith(enabled: Boolean) =
      CalculationsFeatureSwitches(Configuration("retrieveTransitionProfit.enabled" -> enabled))

    "the retrieveTransitionProfit featureSwitchWith is on" must {
      val featureSwitches = featureSwitchesWith(enabled = true)

      "leave the transitionProfit field" in {

        fullResponse.adjustFields(featureSwitches, ignoredTaxYear) shouldBe fullResponse
      }
    }

    "the retrieveTransitionProfit featureSwitchWith is off" must {
      val featureSwitches = featureSwitchesWith(enabled = false)

      "remove transitionProfit" in {
        val calculation = inside(fullResponse.calculation) { case Some(calculation) => calculation }

        val calculationWithoutTransitionProfit = calculation.copy(transitionProfit = None)

        fullResponse.adjustFields(featureSwitches, ignoredTaxYear) shouldBe
          fullResponse.copy(calculation = Some(calculationWithoutTransitionProfit))
      }

      "also remove calculation if no other fields are defined in it" in {
        val onlyTransitionProfitCalculation = Calculation(
          None, None, None, None, None, None, None, None, None, None, None, None, None, None, None,
          None, None, None, None, None, None, None, None, None, None, None, None, None, None,
          transitionProfit = Some(TransitionProfit(totalTaxableTransitionProfit = Some(123), transitionProfitDetail = None))
        )

        val response = fullResponse.copy(calculation = Some(onlyTransitionProfitCalculation))

        response.adjustFields(featureSwitches, ignoredTaxYear) shouldBe response.copy(calculation = None)
      }
    }
  }

}
