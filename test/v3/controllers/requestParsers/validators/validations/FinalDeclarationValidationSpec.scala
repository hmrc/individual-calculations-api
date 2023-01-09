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

package v3.controllers.requestParsers.validators.validations

import support.UnitSpec
import v3.models.errors.FinalDeclarationFormatError

class FinalDeclarationValidationSpec extends UnitSpec {

  "validate" should {
    "return no errors" when {
      "when a valid finalDeclaration is supplied" in {
        val validFinalDeclaration = Some("true")
        val validationResult      = FinalDeclarationValidation.validateOptional(validFinalDeclaration)
        validationResult.isEmpty shouldBe true
      }
      "when no finalDeclaration is supplied" in {
        val validFinalDeclaration = None
        val validationResult      = FinalDeclarationValidation.validateOptional(validFinalDeclaration)
        validationResult.isEmpty shouldBe true
      }
    }

    "return an error" when {
      "when an invalid finalDeclaration is supplied" in {
        val invalidFinalDeclaration = Some("qwerty")
        val validationResult        = FinalDeclarationValidation.validateOptional(invalidFinalDeclaration)
        validationResult.isEmpty shouldBe false
        validationResult.length shouldBe 1
        validationResult.head shouldBe FinalDeclarationFormatError
      }
    }
  }

}
