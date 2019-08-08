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

  // WLOG
  val minTaxYear = 2020

  "validate" should {
    "return no errors" when {
      "a tax year greater than minimum is supplied" in {
        val validationResult = MtdTaxYearValidation.validate("2020-21",  minTaxYear)
        validationResult shouldBe empty
      }

      "a tax year equal to minimum is supplied" in {
        val validationResult = MtdTaxYearValidation.validate("2019-20",  minTaxYear)
        validationResult shouldBe empty
      }
    }

    "return the given error" when {
      "a tax year is below the minimum is supplied" in {
        val validationResult = MtdTaxYearValidation.validate("2018-19", minTaxYear)
        validationResult shouldBe List(RuleTaxYearNotSupportedError)
      }
    }
  }
}
