/*
 * Copyright 2026 HM Revenue & Customs
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

package v9.createAmendProvisionalFiguresInformation

import api.models.domain.{Nino, TaxYear}
import api.models.errors.*
import api.utils.UnitSpec
import play.api.libs.json.{JsValue, Json}
import v9.createAmendProvisionalFiguresInformation.CreateAmendProvisionalFiguresInformationFixtures.*
import v9.createAmendProvisionalFiguresInformation.model.request.{
  CreateAmendProvisionalFiguresInformationRequestBody,
  CreateAmendProvisionalFiguresInformationRequestData
}

import java.time.{Clock, Instant, LocalDate, ZoneOffset}

class CreateAmendProvisionalFiguresInformationValidatorSpec extends UnitSpec {

  private implicit val correlationId: String = "1234"

  private val validNino: String    = "AA123456A"
  private val validTaxYear: String = "2026-27"

  private val parsedNino: Nino       = Nino(validNino)
  private val parsedTaxYear: TaxYear = TaxYear.fromMtd(validTaxYear)

  private implicit val clock: Clock = Clock.fixed(Instant.parse("2026-11-09T00:00:00Z"), ZoneOffset.UTC)

  "running a validation" should {
    "return no errors" when {
      "parse the body and path parameters when the request is valid" in new Test {
        val result: Either[ErrorWrapper, CreateAmendProvisionalFiguresInformationRequestData] =
          validator(validNino, validTaxYear, validMtdRequestJson).validateAndWrapResult()

        result shouldBe Right(CreateAmendProvisionalFiguresInformationRequestData(parsedNino, parsedTaxYear, validRequestBody))
      }

      "a minimum valid request is provided" in new Test {
        val result: Either[ErrorWrapper, CreateAmendProvisionalFiguresInformationRequestData] =
          validator(validNino, validTaxYear, validMinimumMtdRequestJson).validateAndWrapResult()

        result shouldBe Right(CreateAmendProvisionalFiguresInformationRequestData(parsedNino, parsedTaxYear, validMinimumRequestBody))
      }
    }

    "return a NinoFormatError" when {
      "an invalid Nino is supplied" in new Test {
        val invalidNino = "A12344A"
        val result: Either[ErrorWrapper, CreateAmendProvisionalFiguresInformationRequestData] =
          validator(invalidNino, validTaxYear, validMtdRequestJson).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, NinoFormatError))
      }
    }

    "return a TaxYearFormatError" when {
      "an invalid tax year format is supplied" in new Test {
        val result: Either[ErrorWrapper, CreateAmendProvisionalFiguresInformationRequestData] =
          validator(validNino, "201718", validMtdRequestJson).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, TaxYearFormatError))
      }
    }

    "return a RuleTaxYearNotSupportedError" when {
      "an out of range tax year is supplied" in new Test {
        val result: Either[ErrorWrapper, CreateAmendProvisionalFiguresInformationRequestData] =
          validator(validNino, "2016-17", validMtdRequestJson).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, RuleTaxYearNotSupportedError))
      }
    }

    "return a RuleTaxYearRangeInvalidError" when {
      "an invalid tax year range is supplied" in new Test {
        val result: Either[ErrorWrapper, CreateAmendProvisionalFiguresInformationRequestData] =
          validator(validNino, "2017-19", validMtdRequestJson).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, RuleTaxYearRangeInvalidError))
      }
    }

    "return an incorrect or empty body error with the missing mandatory field path" in new Test {
      validator(validNino, validTaxYear, Json.obj()).validateAndWrapResult() shouldBe Left(ErrorWrapper(correlationId, RuleIncorrectOrEmptyBodyError))
      validator(validNino, validTaxYear, Json.obj("expectedDate" -> "2027-10-05")).validateAndWrapResult() shouldBe
        Left(ErrorWrapper(correlationId, RuleIncorrectOrEmptyBodyError.withPath("/provisionalFiguresIndicator")))
    }

    "return an incorrect body error for wrong type fields" when {
      "provisionalFiguresIndicator is not a boolean" in new Test {
        val result: Either[ErrorWrapper, CreateAmendProvisionalFiguresInformationRequestData] =
          validator(validNino, validTaxYear, Json.obj("provisionalFiguresIndicator" -> "true")).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, RuleIncorrectOrEmptyBodyError.withPath("/provisionalFiguresIndicator")))
      }

      "expectedDate is not a string" in new Test {
        val result: Either[ErrorWrapper, CreateAmendProvisionalFiguresInformationRequestData] =
          validator(validNino, validTaxYear, Json.obj("provisionalFiguresIndicator" -> true, "expectedDate" -> 12345)).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, RuleIncorrectOrEmptyBodyError.withPath("/expectedDate")))
      }

      "supplementaryInformation is not a string" in new Test {
        val result: Either[ErrorWrapper, CreateAmendProvisionalFiguresInformationRequestData] =
          validator(
            validNino,
            validTaxYear,
            Json.obj("provisionalFiguresIndicator" -> true, "supplementaryInformation" -> Json.obj("nested" -> "value"))
          ).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, RuleIncorrectOrEmptyBodyError.withPath("/supplementaryInformation")))
      }
    }

    "require the provisional figures indicator to be true" in new Test {
      validator(validNino, validTaxYear, Json.obj("provisionalFiguresIndicator" -> false)).validateAndWrapResult() shouldBe
        Left(ErrorWrapper(correlationId, RuleProvisionalFiguresIndicatorInvalidError.withPath("/provisionalFiguresIndicator")))
    }

    "validate the expected date format and that it is in the future" in new Test {
      validator(validNino, validTaxYear, Json.obj("provisionalFiguresIndicator" -> true, "expectedDate" -> "2027-02-30"))
        .validateAndWrapResult() shouldBe
        Left(ErrorWrapper(correlationId, ExpectedDateFormatError.withPath("/expectedDate")))

      validator(validNino, validTaxYear, Json.obj("provisionalFiguresIndicator" -> true, "expectedDate" -> "2026-10-31"))
        .validateAndWrapResult() shouldBe
        Left(ErrorWrapper(correlationId, RuleExpectedDateNotInFutureError.withPath("/expectedDate")))
    }

    "return a RuleExpectedDateNotInFutureError when the expectedDate is today" in new Test {
      val today: String = LocalDate.now(clock).toString

      val result: Either[ErrorWrapper, CreateAmendProvisionalFiguresInformationRequestData] =
        validator(validNino, validTaxYear, Json.obj("provisionalFiguresIndicator" -> true, "expectedDate" -> today))
          .validateAndWrapResult()

      result shouldBe Left(ErrorWrapper(correlationId, RuleExpectedDateNotInFutureError.withPath("/expectedDate")))
    }

    "return no errors when the expectedDate is tomorrow" in new Test {
      val tomorrow: String = LocalDate.now(clock).plusDays(1).toString

      val result: Either[ErrorWrapper, CreateAmendProvisionalFiguresInformationRequestData] =
        validator(validNino, validTaxYear, Json.obj("provisionalFiguresIndicator" -> true, "expectedDate" -> tomorrow))
          .validateAndWrapResult()

      result shouldBe Right(
        CreateAmendProvisionalFiguresInformationRequestData(
          parsedNino,
          parsedTaxYear,
          CreateAmendProvisionalFiguresInformationRequestBody(
            provisionalFiguresIndicator = true,
            expectedDate = Some(tomorrow),
            supplementaryInformation = None
          )
        ))
    }

    "reject invalid supplementary information" in new Test {
      validator(validNino, validTaxYear, Json.obj("provisionalFiguresIndicator" -> true, "supplementaryInformation" -> "   "))
        .validateAndWrapResult() shouldBe
        Left(ErrorWrapper(correlationId, SupplementaryInformationFormatError.withPath("/supplementaryInformation")))
    }

    "reject supplementary information with invalid characters" in new Test {
      val result: Either[ErrorWrapper, CreateAmendProvisionalFiguresInformationRequestData] =
        validator(validNino, validTaxYear, Json.obj("provisionalFiguresIndicator" -> true, "supplementaryInformation" -> "Invalid™Char"))
          .validateAndWrapResult()

      result shouldBe Left(ErrorWrapper(correlationId, SupplementaryInformationFormatError.withPath("/supplementaryInformation")))
    }

    "reject supplementary information exceeding 4000 characters" in new Test {
      val longString: String = "A" * 4001
      val result: Either[ErrorWrapper, CreateAmendProvisionalFiguresInformationRequestData] =
        validator(validNino, validTaxYear, Json.obj("provisionalFiguresIndicator" -> true, "supplementaryInformation" -> longString))
          .validateAndWrapResult()

      result shouldBe Left(ErrorWrapper(correlationId, SupplementaryInformationFormatError.withPath("/supplementaryInformation")))
    }

    "return multiple errors" when {
      "request supplied has multiple body errors" in new Test {
        val result: Either[ErrorWrapper, CreateAmendProvisionalFiguresInformationRequestData] =
          validator(validNino, validTaxYear, Json.obj("provisionalFiguresIndicator" -> false, "expectedDate" -> "2026-10-31"))
            .validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            BadRequestError,
            Some(
              List(
                RuleExpectedDateNotInFutureError.withPath("/expectedDate"),
                RuleProvisionalFiguresIndicatorInvalidError.withPath("/provisionalFiguresIndicator")
              )
            )
          )
        )
      }

      "request supplied has multiple path parameter errors" in new Test {
        val result: Either[ErrorWrapper, CreateAmendProvisionalFiguresInformationRequestData] =
          validator("A12344A", "2017-19", validMtdRequestJson).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            BadRequestError,
            Some(List(NinoFormatError, RuleTaxYearRangeInvalidError))
          )
        )
      }
    }

    "return a tax year not ended error when temporal validation is enabled" in new Test {
      validator(validNino, validTaxYear, validMtdRequestJson, temporalValidationEnabled = true).validateAndWrapResult() shouldBe
        Left(ErrorWrapper(correlationId, RuleTaxYearNotEndedError))
    }
  }

  trait Test {

    def validator(ninoValue: String, taxYearValue: String, body: JsValue, temporalValidationEnabled: Boolean = false) =
      new CreateAmendProvisionalFiguresInformationValidator(ninoValue, taxYearValue, body, temporalValidationEnabled)

  }

}
