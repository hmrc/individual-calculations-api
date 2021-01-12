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

package v1.controllers.requestParsers.validators

import support.UnitSpec
import v1.models.errors.{CalculationIdFormatError, NinoFormatError}
import v1.models.request.GetCalculationRawData

class GetCalculationValidatorSpec extends UnitSpec {
  private val validNino = "AA112233A"
  private val validCalculationId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"

  val validator = new GetCalculationValidator()

  "running a validation" should {
    "return no errors" when {
      "a valid request is supplied" in {
        validator.validate(GetCalculationRawData(validNino, validCalculationId)) shouldBe empty
      }
    }
    "return NinoFormatError error" when {
      "an invalid nino is supplied" in {
        validator.validate(GetCalculationRawData("AA111111E", validCalculationId)) shouldBe
          List(NinoFormatError)
      }
    }
    "return CalculationIdFormatError error" when {
      "an invalid calculationId is supplied" in {
        validator.validate(GetCalculationRawData(validNino, "f2fb30e5/4ab6/4a29/b3c1/c7264259ff1c")) shouldBe
          List(CalculationIdFormatError)
      }
    }
    "return multiple errors" when {
      "request supplied has multiple errors" in {
        validator.validate(GetCalculationRawData("AA111111E", "f2fb30e5/4ab6/4a29/b3c1/c7264259ff1c")) shouldBe
          List(NinoFormatError, CalculationIdFormatError)
      }
    }
  }


}
