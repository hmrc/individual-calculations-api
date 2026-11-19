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

package v8.triggerCalculation

import api.controllers.validators.Validator
import api.utils.UnitSpec
import v8.triggerCalculation.model.request.TriggerCalculationRequestData

class TriggerCalculationValidatorFactorySpec extends UnitSpec {

  private val validNino = "ZG903729C"

  val validatorFactory = new TriggerCalculationValidatorFactory

  "validator()" when {

    "given any request regardless of tax year" should {
      Seq("2017-18", "2023-24", "2025-26", "2026-27", "invalid").foreach { taxYear =>
        s"return the validator for tax year $taxYear" in {
          val result: Validator[TriggerCalculationRequestData] =
            validatorFactory.validator(validNino, taxYear, "in-year")

          result.shouldBe(a[TriggerCalculationValidator])
        }
      }
    }

  }

}
