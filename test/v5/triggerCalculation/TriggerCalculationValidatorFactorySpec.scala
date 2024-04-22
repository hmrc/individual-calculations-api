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

package v5.triggerCalculation

import api.controllers.validators.Validator
import support.UnitSpec
import v5.triggerCalculation.def1.Def1_TriggerCalculationValidator
import v5.triggerCalculation.model.request.TriggerCalculationRequestData
import v5.triggerCalculation.schema.TriggerCalculationSchema

class TriggerCalculationValidatorFactorySpec extends UnitSpec {

  private val validNino             = "AA123456A"
  private val validTaxYear          = "2017-18"
  private val validFinalDeclaration = "true"

  val validatorFactory = new TriggerCalculationValidatorFactory

  "validator()" when {

    "given any request regardless of tax year" should {
      "return the Validator for schema definition 1" in {
        val result: Validator[TriggerCalculationRequestData] =
          validatorFactory.validator(validNino, validTaxYear, Some(validFinalDeclaration), TriggerCalculationSchema.Def1)

        result shouldBe a[Def1_TriggerCalculationValidator]
      }
    }

  }

}
