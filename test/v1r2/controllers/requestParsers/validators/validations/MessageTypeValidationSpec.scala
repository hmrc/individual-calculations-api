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
import v1r2.models.errors.TypeFormatError

class MessageTypeValidationSpec extends UnitSpec {

  "validate" should {
    "return no errors" when {
      "when a valid type is supplied" in {

        val validType        = "error"
        val validationResult = MessageTypeValidation.validate(validType)
        validationResult.isEmpty shouldBe true
      }

      "when a list of valid types is supplied" in {

        val validTypes       = Seq("info", "warning", "error")
        val validationResult = MessageTypeValidation.validateList(validTypes)
        validationResult.isEmpty shouldBe true
      }
    }

    "return an error" when {
      "when an invalid type is supplied" in {

        val invalidType      = "errors"
        val validationResult = MessageTypeValidation.validate(invalidType)
        validationResult.isEmpty shouldBe false
        validationResult.length shouldBe 1
        validationResult.head shouldBe TypeFormatError
      }
    }

    "return multiple errors" when {
      "many invalid types are supplied" in {

        val invalidTypes     = Seq("shmerror", "shminfo", "shmarning")
        val validationResult = MessageTypeValidation.validateList(invalidTypes)
        validationResult.isEmpty shouldBe false
        validationResult.length shouldBe 3
        validationResult.distinct shouldBe List(TypeFormatError)
      }
    }

  }
}