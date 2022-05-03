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

package v3.controllers.requestParsers.validators

import support.UnitSpec
import v3.models.errors.{FinalDeclarationFormatError, NinoFormatError, RuleTaxYearNotSupportedError, RuleTaxYearRangeInvalidError, TaxYearFormatError}
import v3.models.request.TriggerCalculationRawData

class TriggerCalculationValidatorSpec extends UnitSpec {

  private val validNino             = "AA123456A"
  private val validTaxYear          = "2017-18"
  private val validFinalDeclaration = "true"

  val validator = new TriggerCalculationValidator()

  "running a validation" should {
    "return no errors" when {
      "a valid request is supplied" in {
        validator.validate(TriggerCalculationRawData(validNino, validTaxYear, Some(validFinalDeclaration))) shouldBe List()
      }
    }

    "return NinoFormatError error" when {
      "an invalid nino is supplied" in {
        validator.validate(TriggerCalculationRawData("A12344A", validTaxYear, Some(validFinalDeclaration))) shouldBe
          List(NinoFormatError)
      }
    }

    "return TaxYearFormatError error" when {
      "an invalid tax year is supplied" in {
        validator.validate(TriggerCalculationRawData(validNino, "20178", Some(validFinalDeclaration))) shouldBe
          List(TaxYearFormatError)
      }
    }

    "return RuleTaxYearRangeInvalid error" when {
      "a tax year with a range higher than 1 is supplied" in {
        validator.validate(TriggerCalculationRawData(validNino, "2019-21", Some(validFinalDeclaration))) shouldBe
          List(RuleTaxYearRangeInvalidError)
      }
    }

    "return RuleTaxYearNotSupportedError error" when {
      "an out of range tax year is supplied" in {
        validator.validate(TriggerCalculationRawData(validNino, "2016-17", Some(validFinalDeclaration))) shouldBe
          List(RuleTaxYearNotSupportedError)
      }
    }

    "return FinalDeclarationFormatError error" when {
      "an invalid final declaration is supplied" in {
        validator.validate(TriggerCalculationRawData(validNino, validTaxYear, Some("error"))) shouldBe
          List(FinalDeclarationFormatError)
      }
    }
  }

}
