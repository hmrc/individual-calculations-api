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

package v8.listCalculations.def1

import shared.models.domain.{Nino, TaxYear}
import shared.models.errors._
import shared.utils.UnitSpec
import v8.listCalculations.model.request.Def1_ListCalculationsRequestData

class Def1_ListCalculationsValidatorSpec extends UnitSpec {

  private implicit val correlationId: String = "1234"

  private val validNino     = "ZG903729C"
  private val validTaxYear  = "2017-18"
  private val emptyCalcType = None

  private val parsedNino      = Nino(validNino)
  private val parsedTaxYear   = TaxYear.fromMtd(validTaxYear)
  private val parsedCalcType  = None

  private def validator(nino: String, taxYear: String) =
    new Def1_ListCalculationsValidator(nino, taxYear, emptyCalcType)

  "validator" should {
    "return the parsed domain object" when {
      "a valid request is supplied with a tax year" in {
        val result = validator(validNino, validTaxYear).validateAndWrapResult()
        result shouldBe Right(Def1_ListCalculationsRequestData(parsedNino, parsedTaxYear, parsedCalcType))
      }
    }

    "return NinoFormatError error" when {
      "an invalid nino is supplied" in {
        val result = validator("A12344A", validTaxYear).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, NinoFormatError)
        )
      }
    }

    "return TaxYearFormatError error" when {
      "an invalid tax year is supplied" in {
        val result = validator(validNino, "201718").validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, TaxYearFormatError)
        )
      }
    }

    "return RuleTaxYearNotSupportedError error" when {
      "an out of range tax year is supplied" in {
        val result = validator(validNino, "2016-17").validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, RuleTaxYearNotSupportedError)
        )
      }
    }

    "return RuleTaxYearRangeInvalidError error" when {
      "an invalid tax year range is supplied" in {
        val result = validator(validNino, "2017-19").validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, RuleTaxYearRangeInvalidError)
        )
      }
    }

    "return multiple errors" when {
      "multiple invalid parameters are provided" in {
        val result = validator("not-a-nino", "2017-19").validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            BadRequestError,
            Some(List(NinoFormatError, RuleTaxYearRangeInvalidError))
          )
        )
      }
    }
  }

}
