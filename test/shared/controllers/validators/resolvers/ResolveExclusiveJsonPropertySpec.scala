/*
 * Copyright 2024 HM Revenue & Customs
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
import play.api.http.Status.BAD_REQUEST
import play.api.libs.json.{JsObject, JsString}
import shared.models.errors.MtdError
import shared.utils.UnitSpec

class ResolveExclusiveJsonPropertySpec extends UnitSpec {

  private val error = MtdError("BOTH", "Can't have together", BAD_REQUEST)

  private val resolveExclusiveJsonProperty = new ResolveExclusiveJsonProperty(error, "A1", "A2", "A3")

  private def jsonWithFields(names: String*) =
    JsObject(names.map(name => name -> JsString("ignored")))

  "ResolveExclusiveJsonProperty" when {
    "none of the exclusive properties appears" must {
      val json = jsonWithFields("B", "C")

      "validate successfully" in {
        resolveExclusiveJsonProperty.validator(json) shouldBe None
      }

      "resolve successfully" in {
        resolveExclusiveJsonProperty.resolver(json) shouldBe Valid(json)
      }
    }

    "only one of the exclusive properties appears" must {
      val json = jsonWithFields("A1", "B")

      "validate successfully" in {
        resolveExclusiveJsonProperty.validator(json) shouldBe None
      }

      "resolve successfully" in {
        resolveExclusiveJsonProperty.resolver(json) shouldBe Valid(json)
      }
    }

    "more than one of the exclusive properties appears" must {
      val json = jsonWithFields("A2", "A3", "B")

      "return a validation error from the validator" in {
        resolveExclusiveJsonProperty.validator(json) shouldBe Some(Seq(error))
      }

      "return the validation error from the resolver" in {
        resolveExclusiveJsonProperty.resolver(json) shouldBe Invalid(Seq(error))
      }
    }
  }

}
