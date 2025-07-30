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

package v5.submitFinalDeclaration.def1

import shared.models.domain.{CalculationId, Nino, TaxYear}
import shared.models.errors._
import shared.utils.UnitSpec
import v5.submitFinalDeclaration.model.request.Def1_SubmitFinalDeclarationRequestData

class Def1_SubmitFinalDeclarationValidatorSpec extends UnitSpec {

  private implicit val correlationId: String = "1234"

  private val validNino          = "ZG903729C"
  private val validTaxYear       = "2017-18"
  private val validCalculationId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"

  private val parsedNino          = Nino(validNino)
  private val parsedTaxYear       = TaxYear.fromMtd(validTaxYear)
  private val parsedCalculationId = CalculationId(validCalculationId)

  private def validator(nino: String, taxYear: String, calculationId: String) =
    new Def1_SubmitFinalDeclarationValidator(nino, taxYear, calculationId)

  "running a validation" should {
    "return the parsed domain object" when {
      "a valid request is supplied" in {
        val result = validator(validNino, validTaxYear, validCalculationId).validateAndWrapResult()
        result.shouldBe(Right(Def1_SubmitFinalDeclarationRequestData(parsedNino, parsedTaxYear, parsedCalculationId)))
      }
    }

    "return NinoFormatError error" when {
      "an invalid nino is supplied" in {
        val result = validator("A12344A", validTaxYear, validCalculationId).validateAndWrapResult()
        result.shouldBe(Left(ErrorWrapper(correlationId, NinoFormatError)))
      }
    }

    "return CalculationIdFormatError error" when {
      "an invalid calculationId is supplied" in {
        val result = validator(validNino, validTaxYear, "bad id").validateAndWrapResult()
        result.shouldBe(Left(ErrorWrapper(correlationId, CalculationIdFormatError)))
      }
    }

    "return TaxYearFormatError error" when {
      "an invalid tax year is supplied" in {
        val result = validator(validNino, "201718", validCalculationId).validateAndWrapResult()
        result.shouldBe(Left(ErrorWrapper(correlationId, TaxYearFormatError)))
      }
    }

    "return RuleTaxYearRangeInvalidError error" when {
      "an invalid tax year is supplied" in {
        val result = validator(validNino, "2017-19", validCalculationId).validateAndWrapResult()
        result.shouldBe(Left(ErrorWrapper(correlationId, RuleTaxYearRangeInvalidError)))
      }
    }

    "return RuleTaxYearForVersionNotSupportedErrorr error" when {
      "a tax year after 2024-25 is supplied" in {
        val result = validator(validNino, "2025-26", validCalculationId).validateAndWrapResult()
        result.shouldBe(Left(ErrorWrapper(correlationId, RuleTaxYearForVersionNotSupportedError)))
      }
    }

    "return multiple errors" when {
      "multiple invalid parameters are provided" in {
        val result = validator("not-a-nino", validTaxYear, "bad id").validateAndWrapResult()

        result.shouldBe(
          Left(
            ErrorWrapper(
              correlationId,
              BadRequestError,
              Some(List(CalculationIdFormatError, NinoFormatError))
            )
          ))
      }
    }

  }

}
