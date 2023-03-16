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
package v3.models.response.retrieveCalculation

import support.UnitSpec
import v3.models.response.retrieveCalculation.calculation.Calculation

class CalculationSpec extends UnitSpec with CalculationFixture {

  "update model" when {
    "removeTotalAllowanceAndDeductions is called" in {
      val calculation: Calculation = calcWithoutEndOfYearEstimate
      minimalCalculationResponse.withoutTotalAllowanceAndDeductions shouldBe minimalCalculationResponse.copy(calculation = Some(calculation))
    }
    "removeBasicExtension is called" in {
      val calculation: Calculation = calcWithoutBasicExtension
      minimalCalculationResponse.withoutBasicExtension shouldBe minimalCalculationResponse.copy(calculation = Some(calculation))
    }
    "removeOffPayrollWorker is called" in {
      val calculation: Calculation = calcWithoutOffPayrollWorker
      minimalCalculationResponse.withoutOffPayrollWorker shouldBe minimalCalculationResponse.copy(calculation = Some(calculation))
    }
    "removeUnderLowerProfitThreshold is called" in {
      val calculation: Calculation = calcWithoutUnderLowerProfitThreshold
      minimalCalculationResponse.withoutUnderLowerProfitThreshold shouldBe minimalCalculationResponse.copy(calculation = Some(calculation))
    }
  }

}
