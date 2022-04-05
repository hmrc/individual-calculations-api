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
import v3.models.errors._
import v3.models.request.RetrieveCalculationRawData

class RetrieveCalculationValidatorSpec extends UnitSpec {

  private val validNino = "AA123456A"
  private val validTaxYear = "2017-18"
  private val validCalculationId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"

  val validator = new RetrieveCalculationValidator()

  "running a validation" should {
    "return no errors" when {
      "a valid request is supplied" in {
        validator.validate(RetrieveCalculationRawData(validNino, validTaxYear, validCalculationId)) shouldBe empty
      }
    }

    "return NinoFormatError error" when {
      "an invalid nino is supplied" in {
        validator.validate(RetrieveCalculationRawData("A12344A", validTaxYear, validCalculationId)) shouldBe
          List(NinoFormatError)
      }
    }

    "return TaxYearFormatError error" when {
      "an invalid tax year is supplied" in {
        validator.validate(RetrieveCalculationRawData(validNino, "201718", validCalculationId)) shouldBe
          List(TaxYearFormatError)
      }
    }

    "return RuleTaxYearNotSupportedError error" when {
      "an out of range tax year is supplied" in {
        validator.validate(
          RetrieveCalculationRawData(validNino, "2016-17", validCalculationId)) shouldBe
          List(RuleTaxYearNotSupportedError)
      }
    }

    "return RuleTaxYearRangeInvalidError error" when {
      "an invalid tax year range is supplied" in {
        validator.validate(
          RetrieveCalculationRawData(validNino, "2017-19", validCalculationId)) shouldBe
          List(RuleTaxYearRangeInvalidError)
      }
    }

    "return CalculationIdFormatError error" when {
      "an invalid calculation id is supplied" in {
        validator.validate(
          RetrieveCalculationRawData(validNino, validTaxYear, "bad id")) shouldBe
          List(CalculationIdFormatError)
      }
    }
  }
}