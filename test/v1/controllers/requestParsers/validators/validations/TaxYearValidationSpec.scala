/*
 * Copyright 2020 HM Revenue & Customs
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
import v1.models.errors.{RuleTaxYearRangeInvalidError, TaxYearFormatError}
import v1.models.utils.JsonErrorValidators

class TaxYearValidationSpec extends UnitSpec with JsonErrorValidators {

  "validate" should {
    "return no errors" when {
      "when a valid tax year is supplied" in {

        val validTaxYear = "2018-19"
        val validationResult = TaxYearValidation.validate(validTaxYear)
        validationResult.isEmpty shouldBe true

      }
    }

    "return an error" when {
      "when an invalid tax year format is supplied" in {

        val invalidTaxYear = "2019"
        val validationResult = TaxYearValidation.validate(invalidTaxYear)
        validationResult.isEmpty shouldBe false
        validationResult.length shouldBe 1
        validationResult.head shouldBe TaxYearFormatError

      }
    }

    "the difference in years is greater than 1 year" in {

      val invalidTaxYear = "2017-19"
      val validationResult = TaxYearValidation.validate(invalidTaxYear)
      validationResult.isEmpty shouldBe false
      validationResult.length shouldBe 1
      validationResult.head shouldBe RuleTaxYearRangeInvalidError

    }

    "the end year is before the start year" in {

      val invalidTaxYear = "2018-17"
      val validationResult = TaxYearValidation.validate(invalidTaxYear)
      validationResult.isEmpty shouldBe false
      validationResult.length shouldBe 1
      validationResult.head shouldBe RuleTaxYearRangeInvalidError

    }

    "the start and end years are the same" in {

      val invalidTaxYear = "2017-17"
      val validationResult = TaxYearValidation.validate(invalidTaxYear)
      validationResult.isEmpty shouldBe false
      validationResult.length shouldBe 1
      validationResult.head shouldBe RuleTaxYearRangeInvalidError

    }

  }
}
