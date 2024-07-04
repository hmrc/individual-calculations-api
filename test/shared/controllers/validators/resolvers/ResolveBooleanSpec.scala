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
import shared.models.errors.BadRequestError
import shared.utils.UnitSpec

class ResolveBooleanSpec extends UnitSpec {

  "ResolveBoolean" should {

    "return no errors" when {
      "given the string 'true'" in {
        val result = ResolveBoolean("true", BadRequestError)
        result shouldBe Valid(true)
      }

      "given the upper case string 'TRUE'" in {
        val result = ResolveBoolean("TRUE", BadRequestError)
        result shouldBe Valid(true)
      }

      "given the string 'false'" in {
        val result = ResolveBoolean("false", BadRequestError)
        result shouldBe Valid(false)
      }
    }

    "return an error" when {
      "given an invalid string" in {
        val result = ResolveBoolean("invalid", BadRequestError)
        result shouldBe Invalid(List(BadRequestError))
      }
    }
  }

}
