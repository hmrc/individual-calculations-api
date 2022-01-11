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

package v2.models.response.calculationWrappers

import play.api.libs.json.JsError
import support.UnitSpec
import v2.fixtures.calculationWrappers.CalculationWrapperOrErrorFixture._
import v2.models.response.calculationWrappers.CalculationWrapperOrError.ErrorsInCalculation

class CalculationWrapperOrErrorSpec extends UnitSpec {

  "CalculationWrapperOrError" when {
    "read from valid JSON with a non-zero error count" should {
      "produce an ErrorsInCalculation object" in {
        calculationWrapperJsonWithErrors.as[WrappedCalculation] shouldBe ErrorsInCalculation
      }
    }

    "read from valid JSON with an error count of zero" should {
      "produce the expected CalculationWrapper object" in {
        calculationWrapperJsonWithoutErrors.as[WrappedCalculation] shouldBe wrappedCalculation
      }
    }

    "read from valid JSON with no error count present" should {
      "produce the expected CalculationWrapper object" in {
        calculationWrapperJsonWithoutErrorCount.as[WrappedCalculation] shouldBe wrappedCalculation
      }
    }

    "read from invalid JSON with no metadata present" should {
      "produce a JsError" in {
        calculationWrapperJsonWithoutMetadata.validate[WrappedCalculation] shouldBe a[JsError]
      }
    }

    "read from invalid JSON with an error count of zero, but no calculation present" should {
      "produce a JsError" in {
        calculationWrapperJsonWithoutCalculation.validate[WrappedCalculation] shouldBe a[JsError]
      }
    }
  }
}