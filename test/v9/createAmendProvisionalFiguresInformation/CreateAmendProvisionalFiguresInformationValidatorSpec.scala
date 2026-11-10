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
import api.models.errors.{
  ErrorWrapper,
  FormatExpectedDateError,
  FormatSupplementaryInformationError,
  RuleExpectedDateNotInFutureError,
  RuleIncorrectOrEmptyBodySubmittedError,
  RuleProvisionalFiguresIndicatorInvalidError,
  RuleTaxYearNotEndedError
}
import api.utils.UnitSpec

import java.time.{Clock, Instant, LocalDate, ZoneOffset}
import play.api.libs.json.{JsValue, Json}
import v9.createAmendProvisionalFiguresInformation.CreateAmendProvisionalFiguresInformationFixtures.validMtdRequestJson
import v9.createAmendProvisionalFiguresInformation.model.request.{
  CreateAmendProvisionalFiguresInformationRequestBody,
  CreateAmendProvisionalFiguresInformationRequestData
}

class CreateAmendProvisionalFiguresInformationValidatorSpec extends UnitSpec {

  private implicit val correlationId: String = "1234"

  private val validNino    = "ZG903729C"
  private val validTaxYear = "2027-28"

  private implicit val clock: Clock = Clock.fixed(Instant.parse("2026-11-09T00:00:00Z"), ZoneOffset.UTC)

  private val expectedDateValue: String = LocalDate.now(ZoneOffset.UTC).plusYears(1).toString

  private def validator(body: JsValue, temporalValidationEnabled: Boolean = false) =
    new CreateAmendProvisionalFiguresInformationValidator(validNino, validTaxYear, body, temporalValidationEnabled, clock)

  "CreateAmendProvisionalFiguresInformationValidator" should {
    "parse the body and path parameters when the request is valid" in {
      validator(validMtdRequestJson).validateAndWrapResult() shouldBe Right(
        CreateAmendProvisionalFiguresInformationRequestData(
          Nino(validNino),
          TaxYear.fromMtd(validTaxYear),
          CreateAmendProvisionalFiguresInformationRequestBody(
            provisionalFiguresIndicator = true,
            expectedDate = Some(expectedDateValue),
            supplementaryInformation = Some("Provisional figures used pending final confirmation.")
          )
        )
      )
    }

    "return an incorrect or empty body error with the missing mandatory field path" in {
      validator(Json.obj()).validateAndWrapResult() shouldBe Left(ErrorWrapper(correlationId, RuleIncorrectOrEmptyBodySubmittedError))
      validator(Json.obj("expectedDate" -> "2027-10-05")).validateAndWrapResult() shouldBe
        Left(ErrorWrapper(correlationId, RuleIncorrectOrEmptyBodySubmittedError.withPath("/provisionalFiguresIndicator")))
    }

    "require the provisional figures indicator to be true" in {
      validator(Json.obj("provisionalFiguresIndicator" -> false)).validateAndWrapResult() shouldBe
        Left(ErrorWrapper(correlationId, RuleProvisionalFiguresIndicatorInvalidError.withPath("/provisionalFiguresIndicator")))
    }

    "validate the expected date format and that it is in the future" in {
      validator(Json.obj("provisionalFiguresIndicator" -> true, "expectedDate" -> "2027-02-30")).validateAndWrapResult() shouldBe
        Left(ErrorWrapper(correlationId, FormatExpectedDateError.withPath("/expectedDate")))

      validator(Json.obj("provisionalFiguresIndicator" -> true, "expectedDate" -> "2026-10-31")).validateAndWrapResult() shouldBe
        Left(ErrorWrapper(correlationId, RuleExpectedDateNotInFutureError.withPath("/expectedDate")))
    }

    "reject invalid supplementary information" in {
      validator(Json.obj("provisionalFiguresIndicator" -> true, "supplementaryInformation" -> "   ")).validateAndWrapResult() shouldBe
        Left(ErrorWrapper(correlationId, FormatSupplementaryInformationError.withPath("/supplementaryInformation")))
    }

    "return a tax year not ended error when temporal validation is enabled" in {
      validator(validMtdRequestJson, temporalValidationEnabled = true).validateAndWrapResult() shouldBe
        Left(ErrorWrapper(correlationId, RuleTaxYearNotEndedError))
    }
  }

}
