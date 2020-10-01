/*
 * Copyright 2020 HM Revenue & Customs
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

package v1.models.response.calculationWrappers

import support.UnitSpec
import v1.fixtures.calculationWrappers.EoyEstimateWrapperOrErrorFixture._
import v1.models.response.calculationWrappers.EoyEstimateWrapperOrError._


class EoyEstimateWrapperOrErrorSpec extends UnitSpec {

  "EoyEstimateWrapperOrError" when {
    "read from valid JSON with a non-zero error count" should {
      "return an EoyErrorMessages object" in {
        eoyEstimateWrapperJsonWithErrors.as[EoyEstimateWrapperOrError] shouldBe EoyErrorMessages
      }
    }

    "read from valid JSON with no error count" should {
      "produce the expected EoyEstimateWrapper object" in {
        eoyEstimateWrapperJsonWithoutErrorCount.as[EoyEstimateWrapperOrError] shouldBe wrappedEoyEstimateModel(calculationType = Some("inYear"))
      }
    }

    "read from valid JSON with an error count of zero" should {
      "produce the expected EoyEstimateWrapper object" in {
        eoyEstimateWrapperJsonWithoutErrors.as[EoyEstimateWrapperOrError] shouldBe {
          wrappedEoyEstimateModel(calculationType = Some("inYear"), calculationErrorCount = Some(0))
        }
      }
    }

    "read from valid JSON with no calculation type" should {
      "produce the expected EoyEstimateWrapper object" in {
        eoyEstimateWrapperJsonWithoutCalculationType.as[EoyEstimateWrapperOrError] shouldBe wrappedEoyEstimateModel()
      }
    }

    "read from valid JSON with a calculation type of crystallisation" should {
      "produce an EoyCrystallisedError object" in {
        edyEstimateWrapperJsonCrystallised.as[EoyEstimateWrapperOrError] shouldBe EoyCrystallisedError
      }
    }
  }
}
