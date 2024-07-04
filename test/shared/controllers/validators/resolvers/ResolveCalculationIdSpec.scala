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

package shared.controllers.validators.resolvers

import cats.data.Validated.{Invalid, Valid}
import shared.models.domain.CalculationId
import shared.models.errors.CalculationIdFormatError
import shared.utils.UnitSpec

class ResolveCalculationIdSpec extends UnitSpec {

  "ResolveCalculationId" should {
    "return no errors" when {
      "given a valid Calculation ID" in {
        val value  = "a54ba782-5ef4-47f4-ab72-495406665ca9"
        val result = ResolveCalculationId(value)
        result shouldBe Valid(CalculationId(value))
      }
    }

    "return an error" when {
      "given an invalid CalculationId" in {
        val result = ResolveCalculationId("not-a-calculation-id")
        result shouldBe Invalid(List(CalculationIdFormatError))
      }
    }
  }

}
