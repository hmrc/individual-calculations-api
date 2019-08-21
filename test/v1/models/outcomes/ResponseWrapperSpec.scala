/*
 * Copyright 2019 HM Revenue & Customs
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
package v1.models.outcomes

import support.UnitSpec
import v1.models.errors.{ ErrorWrapper, MtdError, MtdErrors }

class ResponseWrapperSpec extends UnitSpec {
  "ResponseMapper" when {
    "mapping" should {
      "work" in {
        ResponseWrapper("id", "someString").map(_.toLowerCase) shouldBe ResponseWrapper("id", "somestring")
      }
    }

    "converting to error" when {
      // WLOG
      val errors = MtdErrors(400, MtdError("CODE", "message"))

      "does not map partial function" should {
        "leave as is" in {
          val responseWrapper = ResponseWrapper("id", "nonempty")
          responseWrapper.toErrorWhen { case "" => errors } shouldBe Right(responseWrapper)
        }
      }

      "matches partial function" should {
        "map to the error" in {
          ResponseWrapper("id", "").toErrorWhen { case "" => errors } shouldBe Left(ErrorWrapper(Some("id"), errors))
        }
      }
    }

  }
}
