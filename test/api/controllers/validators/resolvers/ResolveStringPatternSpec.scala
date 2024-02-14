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

package api.controllers.validators.resolvers

import api.models.errors.TaxYearFormatError
import cats.data.Validated.{Invalid, Valid}
import support.UnitSpec

class ResolveStringPatternSpec extends UnitSpec {

  private val resolveTaxYearPattern = ResolveStringPattern("20[1-9][0-9]-[1-9][0-9]".r, TaxYearFormatError)

  "ResolveStringPattern" should {
    "return the input value" when {
      "given a matching string" in {
        val result = resolveTaxYearPattern("2024-25")
        result shouldBe Valid("2024-25")
      }
    }

    "return the correct error" when {
      "given a non-matching string and no override error" in {
        val result = resolveTaxYearPattern("does-not-match-regex")
        result shouldBe Invalid(List(TaxYearFormatError))
      }
    }

  }

}
