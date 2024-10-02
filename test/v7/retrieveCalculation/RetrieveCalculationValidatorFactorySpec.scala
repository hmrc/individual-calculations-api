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

package v7.retrieveCalculation

import shared.utils.UnitSpec
import v7.retrieveCalculation.def1.Def1_RetrieveCalculationValidator
import v7.retrieveCalculation.def2.Def2_RetrieveCalculationValidator
import v7.retrieveCalculation.schema.RetrieveCalculationSchema

class RetrieveCalculationValidatorFactorySpec extends UnitSpec {

  private val validNino          = "ZG903729C"
  private val validTaxYear       = "2017-18"
  private val validCalculationId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"

  private val validatorFactory = new RetrieveCalculationValidatorFactory

  "validator factory" when {

    "given any request for schema 1" should {
      "return the Validator for schema definition 1" in {
        validatorFactory.validator(validNino, validTaxYear, validCalculationId, RetrieveCalculationSchema.Def1) shouldBe
          a[Def1_RetrieveCalculationValidator]
      }
    }

    "given any request for schema 2" should {
      "return the Validator for schema definition 2" in {
        validatorFactory.validator(validNino, validTaxYear, validCalculationId, RetrieveCalculationSchema.Def2) shouldBe
          a[Def2_RetrieveCalculationValidator]
      }
    }

  }

}
