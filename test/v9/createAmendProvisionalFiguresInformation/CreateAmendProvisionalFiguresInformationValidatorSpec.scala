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

import api.models.errors.{
  BadRequestError,
  ErrorWrapper,
  ExpectedDateFormatError,
  NinoFormatError,
  RuleExpectedDateNotInFutureError,
  RuleIncorrectOrEmptyBodyError,
  RuleProvisionalFiguresIndicatorInvalidError,
  RuleTaxYearNotEndedError,
  RuleTaxYearRangeInvalidError,
  SupplementaryInformationFormatError,
  TaxYearFormatError,
  RuleTaxYearNotSupportedError
}
import api.utils.UnitSpec

import java.time.{Clock, Instant, ZoneOffset}
import play.api.libs.json.{JsValue, Json}
import v9.createAmendProvisionalFiguresInformation.CreateAmendProvisionalFiguresInformationFixtures.{
  nino,
  taxYear,
  validMinimumMtdRequestJson,
  validMinimumRequestData,
  validMtdRequestJson,
  validRequestData
}
import v9.createAmendProvisionalFiguresInformation.model.request.CreateAmendProvisionalFiguresInformationRequestData

class CreateAmendProvisionalFiguresInformationValidatorSpec extends UnitSpec {

  private implicit val correlationId: String = "1234"

  private implicit val clock: Clock = Clock.fixed(Instant.parse("2026-11-09T00:00:00Z"), ZoneOffset.UTC)

  "running a validation" should {
    "return no errors" when {
      "parse the body and path parameters when the request is valid" in new Test {
        val result: Either[ErrorWrapper, CreateAmendProvisionalFiguresInformationRequestData] =
          validator(nino.nino, taxYear.asMtd, validMtdRequestJson).validateAndWrapResult()

        result shouldBe Right(validRequestData)
      }

      "a minimum valid request is provided" in new Test {
        val result: Either[ErrorWrapper, CreateAmendProvisionalFiguresInformationRequestData] =
          validator(nino.nino, taxYear.asMtd, validMinimumMtdRequestJson).validateAndWrapResult()

        result shouldBe Right(validMinimumRequestData)
      }
    }

    "return a NinoFormatError" when {
      "an invalid Nino is supplied" in new Test {
        val invalidNino = "A12344A"
        val result: Either[ErrorWrapper, CreateAmendProvisionalFiguresInformationRequestData] =
          validator(invalidNino, taxYear.asMtd, validMtdRequestJson).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, NinoFormatError))
      }
    }

    "return a TaxYearFormatError" when {
      "an invalid tax year format is supplied" in new Test {
        val result: Either[ErrorWrapper, CreateAmendProvisionalFiguresInformationRequestData] =
          validator(nino.nino, "201718", validMtdRequestJson).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, TaxYearFormatError))
      }
    }

    "return a RuleTaxYearNotSupportedError" when {
      "an out of range tax year is supplied" in new Test {
        val result: Either[ErrorWrapper, CreateAmendProvisionalFiguresInformationRequestData] =
          validator(nino.nino, "2016-17", validMtdRequestJson).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, RuleTaxYearNotSupportedError))
      }
    }

    "return a RuleTaxYearRangeInvalidError" when {
      "an invalid tax year range is supplied" in new Test {
        val result: Either[ErrorWrapper, CreateAmendProvisionalFiguresInformationRequestData] =
          validator(nino.nino, "2017-19", validMtdRequestJson).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, RuleTaxYearRangeInvalidError))
      }
    }

    "return an incorrect or empty body error with the missing mandatory field path" in new Test {
      validator(nino.nino, taxYear.asMtd, Json.obj()).validateAndWrapResult() shouldBe Left(
        ErrorWrapper(correlationId, RuleIncorrectOrEmptyBodyError))
      validator(nino.nino, taxYear.asMtd, Json.obj("expectedDate" -> "2027-10-05")).validateAndWrapResult() shouldBe
        Left(ErrorWrapper(correlationId, RuleIncorrectOrEmptyBodyError.withPath("/provisionalFiguresIndicator")))
    }

    "return an incorrect body error for wrong type fields" when {
      "provisionalFiguresIndicator is not a boolean" in new Test {
        val result: Either[ErrorWrapper, CreateAmendProvisionalFiguresInformationRequestData] =
          validator(nino.nino, taxYear.asMtd, Json.obj("provisionalFiguresIndicator" -> "true")).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, RuleIncorrectOrEmptyBodyError.withPath("/provisionalFiguresIndicator")))
      }

      "expectedDate is not a string" in new Test {
        val result: Either[ErrorWrapper, CreateAmendProvisionalFiguresInformationRequestData] =
          validator(nino.nino, taxYear.asMtd, Json.obj("provisionalFiguresIndicator" -> true, "expectedDate" -> 12345)).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, RuleIncorrectOrEmptyBodyError.withPath("/expectedDate")))
      }

      "supplementaryInformation is not a string" in new Test {
        val result: Either[ErrorWrapper, CreateAmendProvisionalFiguresInformationRequestData] =
          validator(
            nino.nino,
            taxYear.asMtd,
            Json.obj("provisionalFiguresIndicator" -> true, "supplementaryInformation" -> Json.obj("nested" -> "value"))
          ).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, RuleIncorrectOrEmptyBodyError.withPath("/supplementaryInformation")))
      }
    }

    "require the provisional figures indicator to be true" in new Test {
      validator(nino.nino, taxYear.asMtd, Json.obj("provisionalFiguresIndicator" -> false)).validateAndWrapResult() shouldBe
        Left(ErrorWrapper(correlationId, RuleProvisionalFiguresIndicatorInvalidError.withPath("/provisionalFiguresIndicator")))
    }

    "validate the expected date format and that it is in the future" in new Test {
      validator(nino.nino, taxYear.asMtd, Json.obj("provisionalFiguresIndicator" -> true, "expectedDate" -> "2027-02-30"))
        .validateAndWrapResult() shouldBe
        Left(ErrorWrapper(correlationId, ExpectedDateFormatError.withPath("/expectedDate")))

      validator(nino.nino, taxYear.asMtd, Json.obj("provisionalFiguresIndicator" -> true, "expectedDate" -> "2026-10-31"))
        .validateAndWrapResult() shouldBe
        Left(ErrorWrapper(correlationId, RuleExpectedDateNotInFutureError.withPath("/expectedDate")))
    }

    "reject invalid supplementary information" in new Test {
      validator(nino.nino, taxYear.asMtd, Json.obj("provisionalFiguresIndicator" -> true, "supplementaryInformation" -> "   "))
        .validateAndWrapResult() shouldBe
        Left(ErrorWrapper(correlationId, SupplementaryInformationFormatError.withPath("/supplementaryInformation")))
    }

    "reject supplementary information with invalid characters" in new Test {
      val result: Either[ErrorWrapper, CreateAmendProvisionalFiguresInformationRequestData] =
        validator(nino.nino, taxYear.asMtd, Json.obj("provisionalFiguresIndicator" -> true, "supplementaryInformation" -> "Invalid™Char"))
          .validateAndWrapResult()

      result shouldBe Left(ErrorWrapper(correlationId, SupplementaryInformationFormatError.withPath("/supplementaryInformation")))
    }

    "reject supplementary information exceeding 4000 characters" in new Test {
      val longString: String = "A" * 4001
      val result: Either[ErrorWrapper, CreateAmendProvisionalFiguresInformationRequestData] =
        validator(nino.nino, taxYear.asMtd, Json.obj("provisionalFiguresIndicator" -> true, "supplementaryInformation" -> longString))
          .validateAndWrapResult()

      result shouldBe Left(ErrorWrapper(correlationId, SupplementaryInformationFormatError.withPath("/supplementaryInformation")))
    }

    "return multiple errors" when {
      "request supplied has multiple body errors" in new Test {
        val result: Either[ErrorWrapper, CreateAmendProvisionalFiguresInformationRequestData] =
          validator(nino.nino, taxYear.asMtd, Json.obj("provisionalFiguresIndicator" -> false, "expectedDate" -> "2026-10-31"))
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
      validator(nino.nino, taxYear.asMtd, validMtdRequestJson, temporalValidationEnabled = true).validateAndWrapResult() shouldBe
        Left(ErrorWrapper(correlationId, RuleTaxYearNotEndedError))
    }
  }

  trait Test {

    def validator(ninoValue: String, taxYearValue: String, body: JsValue, temporalValidationEnabled: Boolean = false) =
      new CreateAmendProvisionalFiguresInformationValidator(ninoValue, taxYearValue, body, temporalValidationEnabled)

  }

}
