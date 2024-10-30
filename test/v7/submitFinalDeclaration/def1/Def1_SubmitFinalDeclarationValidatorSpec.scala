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

package v7.submitFinalDeclaration.def1

import api.errors.{RuleSubmissionFailedError, FormatCalculationTypeError}
import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import shared.models.domain.{CalculationId, Nino, TaxYear}
import shared.models.errors._
import shared.utils.UnitSpec
import v7.common.model.{CalculationType, `confirm-amendment`, `final-declaration`}
import v7.submitFinalDeclaration.model.request.Def1_SubmitFinalDeclarationRequestData

class Def1_SubmitFinalDeclarationValidatorSpec extends UnitSpec {

  private implicit val correlationId: String = "1234"

  private val validNino          = "ZG903729C"
  private val validTaxYear       = "2017-18"
  private val validFinalDeclaration = "final-declaration"
  private val validCalculationId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"

  private val parsedNino          = Nino(validNino)
  private val parsedTaxYear       = TaxYear.fromMtd(validTaxYear)
  private val parsedCalculationId = CalculationId(validCalculationId)
  private val parsedFinalDeclaration: CalculationType = `final-declaration`

  private def validator(nino: String, taxYear: String, calculationId: String, calculationType: String) =
    new Def1_SubmitFinalDeclarationValidator(nino, taxYear, calculationId, calculationType)

  private def ruleValidator(nino: Nino, taxYear: TaxYear,
                            calculationId: CalculationId, calculationType: CalculationType): Validated[Seq[MtdError], Def1_SubmitFinalDeclarationRequestData] =
    Def1_SubmitFinalDeclarationRulesValidator.validateBusinessRules(Def1_SubmitFinalDeclarationRequestData(nino, taxYear, calculationId, calculationType)
    )

  "running a validation" should {
    "return the parsed domain object" when {
      "a valid request is supplied" in {
        val result = validator(validNino, validTaxYear, validCalculationId, validFinalDeclaration).validateAndWrapResult()
        result shouldBe Right(Def1_SubmitFinalDeclarationRequestData(parsedNino, parsedTaxYear, parsedCalculationId, parsedFinalDeclaration))
      }
    }

    "return NinoFormatError error" when {
      "an invalid nino is supplied" in {
        val result = validator("A12344A", validTaxYear, validCalculationId, validFinalDeclaration).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, NinoFormatError)
        )
      }
    }

    "return CalculationIdFormatError error" when {
      "an invalid calculationId is supplied" in {
        val result = validator(validNino, validTaxYear, "bad id", validFinalDeclaration).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, CalculationIdFormatError)
        )
      }
    }

    "return TaxYearFormatError error" when {
      "an invalid tax year is supplied" in {
        val result = validator(validNino, "201718", validCalculationId, validFinalDeclaration).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, TaxYearFormatError)
        )
      }
    }

    "return RuleTaxYearRangeInvalidError error" when {
      "an invalid tax year is supplied" in {
        val result = validator(validNino, "2017-19", validCalculationId, validFinalDeclaration).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, RuleTaxYearRangeInvalidError)
        )
      }
    }
    "return formatCalculationTypeError error" when {
      "an invalid tax year is supplied" in {
        val result = validator(validNino, validTaxYear, validCalculationId, "notValid").validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, FormatCalculationTypeError)
        )
      }
    }

    "return multiple errors" when {
      "multiple invalid parameters are provided" in {
        val result = validator("not-a-nino", validTaxYear, "bad id", validFinalDeclaration).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            BadRequestError,
            Some(List(CalculationIdFormatError, NinoFormatError))
          )
        )
      }
    }

    "Def1_SubmitFinalDeclarationRulesValidatorSpec.validateBusinessRules" should {
      val validNino = Nino("ZG903729C")
      val taxYear1780 = TaxYear.fromMtd("2017-18")
      val taxYear2082 = TaxYear.fromMtd("2025-26")
      val confirmAmendment: CalculationType = `confirm-amendment`

      "return a Rule submission error" in {
        ruleValidator(validNino, taxYear1780, parsedCalculationId, confirmAmendment) shouldBe Invalid(Seq(RuleSubmissionFailedError))
      }
      "return valid request data" in {
        ruleValidator(validNino, taxYear2082, parsedCalculationId, confirmAmendment) shouldBe Valid(Def1_SubmitFinalDeclarationRequestData(validNino, taxYear2082, parsedCalculationId, confirmAmendment))
      }
    }

  }

}
