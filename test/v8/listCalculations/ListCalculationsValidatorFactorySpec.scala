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

package v8.listCalculations

import shared.controllers.validators.Validator
import shared.utils.UnitSpec
import v8.listCalculations.def1.Def1_ListCalculationsValidator
import v8.listCalculations.def2.Def2_ListCalculationsValidator
import v8.listCalculations.def3.Def3_ListCalculationsValidator
import v8.listCalculations.model.request.ListCalculationsRequestData

import javax.inject.Singleton

@Singleton
class ListCalculationsValidatorFactorySpec extends UnitSpec {

  private val validNino = "ZG903729C"

  private val validatorFactory = new ListCalculationsValidatorFactory

  "validator()" when {

    "given any request for pre 2023" should {
      "return the Validator for schema definition 1" in {
        val validTaxYear = "2022-23"
        val result: Validator[ListCalculationsRequestData] =
          validatorFactory.validator(validNino, taxYear = validTaxYear, None)

        result.shouldBe(a[Def1_ListCalculationsValidator])
      }
    }
    "given any request for tax years 2023-24, 2024-5" should {
      "return the Validator for schema definition 2 for 23-24" in {
        val validTaxYear = "2023-24"
        val result: Validator[ListCalculationsRequestData] =
          validatorFactory.validator(validNino, taxYear = validTaxYear, None)

        result.shouldBe(a[Def2_ListCalculationsValidator])
      }
      "return the Validator for schema definition 2 for 24-25" in {
        val validTaxYear = "2024-25"
        val result: Validator[ListCalculationsRequestData] =
          validatorFactory.validator(validNino, taxYear = validTaxYear, None)

        result.shouldBe(a[Def2_ListCalculationsValidator])
      }
    }
    "given any request for post 2025-26" should {
      "return the Validator for schema definition 3" in {
        val validTaxYear = "2025-26"
        val result: Validator[ListCalculationsRequestData] =
          validatorFactory.validator(validNino, taxYear = validTaxYear, None)

        result.shouldBe(a[Def3_ListCalculationsValidator])
      }
    }

  }

}
