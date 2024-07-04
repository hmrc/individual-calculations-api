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
import shared.models.domain.BusinessId
import shared.models.errors.BusinessIdFormatError
import shared.utils.UnitSpec

class ResolveBusinessIdSpec extends UnitSpec {

  "ResolveBusinessId" should {
    "return no errors" when {
      "given a valid business ID" in {
        val validBusinessId = "XAIS12345678901"
        val result          = ResolveBusinessId(validBusinessId)
        result shouldBe Valid(BusinessId(validBusinessId))
      }
    }

    "return an error" when {
      "given an invalid business ID" in {
        val invalidBusinessId = "XAXAIS65271982AD"
        val result            = ResolveBusinessId(invalidBusinessId)
        result shouldBe Invalid(List(BusinessIdFormatError))
      }
    }
  }

}
