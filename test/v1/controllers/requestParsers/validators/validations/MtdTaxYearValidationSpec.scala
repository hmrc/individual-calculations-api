/*
 * Copyright 2019 HM Revenue & Customs
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

package v1.controllers.requestParsers.validators.validations

import support.UnitSpec
import v1.models.errors.RuleTaxYearNotSupportedError
import v1.models.utils.JsonErrorValidators

class MtdTaxYearValidationSpec extends UnitSpec with JsonErrorValidators {

  "validate" should {
    "return no errors" when {
      "a tax year greater than 2017 is supplied" in {

        val validTaxYear = "2018-19"
        val validationResult = MtdTaxYearValidation.validate(validTaxYear, RuleTaxYearNotSupportedError)
        validationResult.isEmpty shouldBe true

      }

      "the minimum allowed tax year is supplied" in {
        val validTaxYear = "2017-18"
        val validationResult = MtdTaxYearValidation.validate(validTaxYear, RuleTaxYearNotSupportedError)
        validationResult.isEmpty shouldBe true
      }

    }

    "return the given error" when {
      "a tax year below 2017 is supplied" in {

        val invalidTaxYear = "2015-16"
        val validationResult = MtdTaxYearValidation.validate(invalidTaxYear, RuleTaxYearNotSupportedError)
        validationResult.isEmpty shouldBe false
        validationResult.length shouldBe 1
        validationResult.head shouldBe RuleTaxYearNotSupportedError
      }
    }
  }
}
