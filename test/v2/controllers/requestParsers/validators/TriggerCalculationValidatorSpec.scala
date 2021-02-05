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

package v2.controllers.requestParsers.validators

import play.api.libs.json.Json
import play.api.mvc.AnyContentAsJson
import support.UnitSpec
import v2.models.errors.{NinoFormatError, RuleIncorrectOrEmptyBodyError, RuleTaxYearNotSupportedError, TaxYearFormatError}
import v2.models.request.TriggerCalculationRawData

class TriggerCalculationValidatorSpec extends UnitSpec {

  private val validNino = "AA123456A"
  private val validTaxYear = "2017-18"

  val validator = new TriggerCalculationValidator()

  "running a validation" should {
    "return no errors" when {
      "a valid request is supplied" in {
        validator.validate(TriggerCalculationRawData(validNino, AnyContentAsJson(Json.obj("taxYear" -> validTaxYear)))) shouldBe empty
      }
    }

    "return NinoFormatError error" when {
      "an invalid nino is supplied" in {
        validator.validate(TriggerCalculationRawData("A12344A", AnyContentAsJson(Json.obj("taxYear" -> validTaxYear)))) shouldBe
          List(NinoFormatError)
      }
    }

    "return TaxYearFormatError error" when {
      "an invalid tax year is supplied" in {
        validator.validate(TriggerCalculationRawData(validNino, AnyContentAsJson(Json.obj("taxYear" -> "20178")))) shouldBe
          List(TaxYearFormatError)
      }
    }

    "return RuleTaxYearNotSupportedError error" when {
      "an out of range tax year is supplied" in {
        validator.validate(
          TriggerCalculationRawData(validNino, AnyContentAsJson(Json.obj("taxYear" -> "2016-17")))) shouldBe
          List(RuleTaxYearNotSupportedError)
      }
    }

    "return IncorrectOrEmptyBodyError error" when {
      "an empty json body is supplied" in {
        validator.validate(
          TriggerCalculationRawData(validNino, AnyContentAsJson(Json.obj()))) shouldBe
          List(RuleIncorrectOrEmptyBodyError)
      }
    }
  }
}