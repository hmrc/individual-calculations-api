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
import v3.models.errors.{NinoFormatError, RuleTaxYearNotSupportedError, TaxYearFormatError}
import v3.models.request.TriggerCalculationRawData

class TriggerCalculationValidatorSpec extends UnitSpec {

  val validNino        = "AA123456A"
  val validTaxYear     = "2017-18"
  val finalDeclaration = Option(true)

  val validator = new TriggerCalculationValidator()

  "running a validation" should {
    "return no errors" when {
      "a valid request is supplied" in {
        validator.validate(TriggerCalculationRawData(validNino, validTaxYear, finalDeclaration)) shouldBe empty
      }
    }

    "return NinoFormatError error" when {
      "an invalid nino is supplied" in {
        validator.validate(TriggerCalculationRawData("A12344A", validTaxYear, finalDeclaration)) shouldBe
          List(NinoFormatError)
      }
    }

    "return TaxYearFormatError error" when {
      "an invalid tax year is supplied" in {
        validator.validate(TriggerCalculationRawData(validNino, "20178", finalDeclaration)) shouldBe
          List(TaxYearFormatError)
      }
    }

    "return RuleTaxYearNotSupportedError error" when {
      "an out of range tax year is supplied" in {
        validator.validate(TriggerCalculationRawData(validNino, "2016-17", finalDeclaration)) shouldBe
          List(RuleTaxYearNotSupportedError)
      }
    }
  }

}
