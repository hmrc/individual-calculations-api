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

package shared.controllers.validators

import cats.data.Validated.Invalid
import play.api.http.Status
import shared.models.errors.MtdError
import shared.utils.UnitSpec

class AlwaysErrorsValidatorSpec extends UnitSpec {

  "AlwaysErrorsValidator" must {
    "always return the errors that it is constructed with" in {
      val errors = Seq(MtdError("E1", "", Status.BAD_REQUEST), MtdError("E2", "", Status.BAD_REQUEST))

      AlwaysErrorsValidator(errors).validate shouldBe Invalid(errors)
    }
  }

}
