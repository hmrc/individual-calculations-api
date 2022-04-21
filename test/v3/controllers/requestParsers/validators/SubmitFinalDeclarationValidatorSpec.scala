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
import v3.models.errors.{CalculationIdFormatError, NinoFormatError, RuleTaxYearNotSupportedError, RuleTaxYearRangeInvalidError, TaxYearFormatError}
import v3.models.request.SubmitFinalDeclarationRawData

class SubmitFinalDeclarationValidatorSpec extends UnitSpec {

  val validNino        = "AA123456A"
  val validTaxYear     = "2017-18"
  val calcId           = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"

  val validator = new SubmitFinalDeclarationValidator()

  "running a validation" should {
    "return no errors" when {
      "a valid request is supplied" in {
        validator.validate(SubmitFinalDeclarationRawData(validNino, validTaxYear, calcId)) shouldBe empty
      }
    }

    "return NinoFormatError error" when {
      "an invalid nino is supplied" in {
        validator.validate(SubmitFinalDeclarationRawData("A12344A", validTaxYear, calcId)) shouldBe
          List(NinoFormatError)
      }
    }

    "return CalculationIdFormatError error" when {
      "an invalid calculationId is supplied" in {
        validator.validate(SubmitFinalDeclarationRawData(validNino, validTaxYear, "f2fb30e5-4ab6-4a29-b3c1-c726425")) shouldBe
          List(CalculationIdFormatError)
      }

    "return TaxYearFormatError error" when {
      "an invalid tax year is supplied" in {
        validator.validate(SubmitFinalDeclarationRawData(validNino, "20178", calcId)) shouldBe
          List(TaxYearFormatError)
      }
    }

    "return RuleTaxYearRangeInvalidError error" when {
      "an invalid tax year is supplied" in {
        validator.validate(SubmitFinalDeclarationRawData(validNino, "2016-18", calcId)) shouldBe
          List(RuleTaxYearRangeInvalidError)
        }
      }


    "return RuleTaxYearNotSupportedError error" when {
      "an out of range tax year is supplied" in {
        validator.validate(SubmitFinalDeclarationRawData(validNino, "2016-17", calcId)) shouldBe
          List(RuleTaxYearNotSupportedError)
      }
    }
  }

}


}
