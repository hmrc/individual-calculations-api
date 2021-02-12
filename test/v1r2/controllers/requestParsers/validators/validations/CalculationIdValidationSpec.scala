/*
 * Copyright 2021 HM Revenue & Customs
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

package v1r2.controllers.requestParsers.validators.validations

import support.UnitSpec
import v1r2.models.errors.CalculationIdFormatError

class CalculationIdValidationSpec extends UnitSpec {

  "validate" should {
    "return no errors" when {
      "when a valid calculationId is provided" in {
        val validCalculationId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"
        val validationResult = CalculationIdValidation.validate(validCalculationId)
        validationResult.isEmpty shouldBe true
      }
    }

    "return an error" when {
      "an invalid calculationId is provided" in {
        val invalidCalculationId = "invalidCalculationId"
        val validationResult = CalculationIdValidation.validate(invalidCalculationId)
        validationResult should not be empty
        validationResult.length shouldBe 1
        validationResult.head shouldBe CalculationIdFormatError
      }
    }
  }
}