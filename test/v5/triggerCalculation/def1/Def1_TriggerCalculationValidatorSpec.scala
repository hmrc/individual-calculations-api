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

package v5.triggerCalculation.def1

import api.models.domain.{Nino, TaxYear}
import api.models.errors._
import support.UnitSpec
import v5.triggerCalculation.model.request.Def1_TriggerCalculationRequestData

class Def1_TriggerCalculationValidatorSpec extends UnitSpec {

  private implicit val correlationId: String = "1234"

  private val validNino             = "AA123456A"
  private val validTaxYear          = "2017-18"
  private val validFinalDeclaration = "true"

  private val parsedNino             = Nino(validNino)
  private val parsedTaxYear          = TaxYear.fromMtd(validTaxYear)
  private val parsedFinalDeclaration = true

  private def validator(nino: String, taxYear: String, finalDeclaration: Option[String]) =
    new Def1_TriggerCalculationValidator(nino, taxYear, finalDeclaration)

  "validator" should {
    "return the parsed domain object" when {
      "a valid request is supplied with some finalDeclaration value" in {
        val result = validator(validNino, validTaxYear, Some(validFinalDeclaration)).validateAndWrapResult()
        result shouldBe Right(Def1_TriggerCalculationRequestData(parsedNino, parsedTaxYear, parsedFinalDeclaration))
      }

      "a valid request is supplied without a finalDeclaration value" in {
        val result = validator(validNino, validTaxYear, None).validateAndWrapResult()
        result shouldBe Right(Def1_TriggerCalculationRequestData(parsedNino, parsedTaxYear, false))
      }

    }

    "return NinoFormatError error" when {
      "an invalid nino is supplied" in {
        val result = validator("A12344A", validTaxYear, Some(validFinalDeclaration)).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, NinoFormatError)
        )
      }
    }

    "return TaxYearFormatError error" when {
      "an invalid tax year is supplied" in {
        val result = validator(validNino, "20178", Some(validFinalDeclaration)).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, TaxYearFormatError)
        )
      }
    }

    "return RuleTaxYearRangeInvalid error" when {
      "a tax year with a range higher than 1 is supplied" in {
        val result = validator(validNino, "2019-21", Some(validFinalDeclaration)).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, RuleTaxYearRangeInvalidError)
        )
      }
    }

    "return RuleTaxYearNotSupportedError error" when {
      "an out of range tax year is supplied" in {
        val result = validator(validNino, "2016-17", Some(validFinalDeclaration)).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, RuleTaxYearNotSupportedError)
        )
      }
    }

    "return FinalDeclarationFormatError error" when {
      "an invalid final declaration is supplied" in {
        val result = validator(validNino, validTaxYear, Some("error")).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, FinalDeclarationFormatError)
        )
      }
    }

    "return multiple errors" when {
      "multiple invalid parameters are provided" in {
        val result = validator("not-a-nino", validTaxYear, Some("error")).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            BadRequestError,
            Some(List(FinalDeclarationFormatError, NinoFormatError))
          )
        )
      }
    }
  }

}
