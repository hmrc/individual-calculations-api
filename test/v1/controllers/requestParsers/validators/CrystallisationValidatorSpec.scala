/*
 * Copyright 2021 HM Revenue & Customs
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

package v1.controllers.requestParsers.validators

import play.api.libs.json.Json
import play.api.mvc.AnyContentAsJson
import support.UnitSpec
import v1.models.errors.{CalculationIdFormatError, NinoFormatError, RuleIncorrectOrEmptyBodyError, RuleTaxYearNotSupportedError, RuleTaxYearRangeInvalidError, TaxYearFormatError}
import v1.models.request.crystallisation.CrystallisationRawData

class CrystallisationValidatorSpec extends UnitSpec {
  private val validNino = "AA123456A"
  private val validTaxYear = "2017-18"
  private val calculationId = "a1e8057e-fbbc-47a8-a8b4-78d9f015c253"

  val validator = new CrystallisationValidator()

  "running a validation" should {
    "return no errors" when {
      "a valid request is supplied" in {
        validator.validate(CrystallisationRawData(validNino, validTaxYear, AnyContentAsJson(Json.obj("calculationId" -> calculationId)))) shouldBe empty
      }
    }

    "return NinoFormatError error" when {
      "an invalid nino is supplied" in {
        validator.validate(CrystallisationRawData("A12344A", validTaxYear, AnyContentAsJson(Json.obj("calculationId" -> calculationId)))) shouldBe
          List(NinoFormatError)
      }
    }

    "return TaxYearFormatError error" when {
      "an invalid tax year is supplied" in {
        validator.validate(CrystallisationRawData(validNino, "notATaxYear", AnyContentAsJson(Json.obj("calculationId" -> calculationId)))) shouldBe
          List(TaxYearFormatError)
      }
    }

    "return RuleTaxYearNotSupportedError error" when {
      "an out of range tax year is supplied" in {
        validator.validate(
          CrystallisationRawData(validNino, "2010-11", AnyContentAsJson(Json.obj("calculationId" -> calculationId)))) shouldBe
          List(RuleTaxYearNotSupportedError)
      }
    }

    "return RuleTaxYearRangeInvalidError error" when {
      "tax year range is too large" in {
        validator.validate(
          CrystallisationRawData(validNino, "2018-20", AnyContentAsJson(Json.obj("calculationId" -> calculationId)))) shouldBe
          List(RuleTaxYearRangeInvalidError)
      }
    }

    "return IncorrectOrEmptyBodyError error" when {
      "an empty json body is supplied" in {
        validator.validate(
          CrystallisationRawData(validNino, validTaxYear, AnyContentAsJson(Json.obj()))) shouldBe
          List(RuleIncorrectOrEmptyBodyError)
      }
    }

    "return IncorrectOrEmptyBodyError error" when {
      "request body format is invalid" in {
        validator.validate(
          CrystallisationRawData(validNino, validTaxYear, AnyContentAsJson(Json.obj("calculationId" -> true)))) shouldBe
          List(RuleIncorrectOrEmptyBodyError)
      }
    }

    "return CalculationIdFormatError error" when {
      "request body contains a calculation id string with incorrect format" in {
        validator.validate(
          CrystallisationRawData(validNino, validTaxYear, AnyContentAsJson(Json.obj("calculationId" -> "notAnId")))) shouldBe
          List(CalculationIdFormatError)
      }
    }
  }
}
