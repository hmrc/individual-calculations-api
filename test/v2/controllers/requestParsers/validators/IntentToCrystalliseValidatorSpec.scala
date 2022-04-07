/*
 * Copyright 2022 HM Revenue & Customs
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

package v2.controllers.requestParsers.validators

import support.UnitSpec
import v2.models.errors.{NinoFormatError, RuleTaxYearNotSupportedError, TaxYearFormatError}
import v2.models.request.intentToCrystallise.IntentToCrystalliseRawData

class IntentToCrystalliseValidatorSpec extends UnitSpec {

  private val validNino    = "AA123456A"
  private val validTaxYear = "2017-18"

  val validator = new IntentToCrystalliseValidator()

  "running a validation" should {
    "return no errors" when {
      "a valid request is supplied" in {
        validator.validate(IntentToCrystalliseRawData(validNino, validTaxYear)) shouldBe empty
      }
    }

    "return NinoFormatError error" when {
      "an invalid nino is supplied" in {
        validator.validate(IntentToCrystalliseRawData("A12344A", validTaxYear)) shouldBe List(NinoFormatError)
      }
    }

    "return TaxYearFormatError error" when {
      "an invalid tax year is supplied" in {
        validator.validate(IntentToCrystalliseRawData(validNino, "20178")) shouldBe List(TaxYearFormatError)
      }
    }

    "return RuleTaxYearNotSupportedError error" when {
      "an out of range tax year is supplied" in {
        validator.validate(IntentToCrystalliseRawData(validNino, "2016-17")) shouldBe List(RuleTaxYearNotSupportedError)
      }
    }

    "return multiple errors" when {
      "request supplied has multiple errors" in {
        validator.validate(IntentToCrystalliseRawData("A12344A", "20178")) shouldBe List(NinoFormatError, TaxYearFormatError)
      }
    }
  }

}
