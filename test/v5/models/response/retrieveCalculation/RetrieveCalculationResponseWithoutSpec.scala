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

package v5.models.response.retrieveCalculation

import support.UnitSpec

class RetrieveCalculationResponseWithoutSpec extends UnitSpec with CalculationFixture {

  "withoutTotalAllowanceAndDeductions" when {
    "calculation.isDefined returns true" should {
      "return the response with the calculation" in {
        val calculation = calcWithoutEndOfYearEstimate
        val result      = minimalCalculationR8bResponse.withoutTotalAllowanceAndDeductions
        result shouldBe minimalCalculationR8bResponse.copy(calculation = Some(calculation))
      }
    }

    "calculation.isDefined returns false" should {
      "return the response with no calculation" in {
        val response = minimalCalculationR8bResponse.copy(calculation = Some(emptyCalculation))
        val result   = response.withoutTotalAllowanceAndDeductions
        result shouldBe minimalCalculationR8bResponse.copy(calculation = None)
      }
    }
  }

  "withoutBasicExtension" when {
    "calculation.isDefined returns true" should {
      "return the response with the calculation" in {
        val calculation = calcWithoutBasicExtension
        val result      = minimalCalculationR8bResponse.withoutBasicExtension
        result shouldBe minimalCalculationR8bResponse.copy(calculation = Some(calculation))
      }
    }

    "calculation.isDefined returns false" should {
      "return the response with no calculation" in {
        val response = minimalCalculationR8bResponse.copy(calculation = Some(emptyCalculation))
        val result   = response.withoutBasicExtension
        result shouldBe minimalCalculationR8bResponse.copy(calculation = None)
      }
    }
  }

  "withoutOffPayrollWorker" when {
    "calculation.isDefined returns true" should {
      "return the response with the calculation" in {
        val calculation = calcWithoutOffPayrollWorker
        val result      = minimalCalculationR8bResponse.withoutOffPayrollWorker
        result shouldBe minimalCalculationR8bResponse.copy(calculation = Some(calculation))
      }
    }

    "calculation.isDefined returns false" should {
      "return the response with no calculation" in {
        val response = minimalCalculationR8bResponse.copy(calculation = Some(emptyCalculation))
        val result   = response.withoutOffPayrollWorker
        result shouldBe minimalCalculationR8bResponse.copy(calculation = None)
      }
    }
  }

  "withoutUnderLowerProfitThreshold" when {
    "calculation.isDefined returns true" should {
      "return the response with the calculation" in {
        val calculation = calcWithoutUnderLowerProfitThreshold
        val result      = minimalCalculationR8bResponse.withoutUnderLowerProfitThreshold
        result shouldBe minimalCalculationR8bResponse.copy(calculation = Some(calculation))
      }
    }

    "calculation.isDefined returns false" should {
      "return the response with no calculation" in {
        val response = minimalCalculationR8bResponse.copy(calculation = Some(emptyCalculation))
        val result   = response.withoutOffPayrollWorker
        result shouldBe minimalCalculationR8bResponse.copy(calculation = None)
      }
    }
  }

}
