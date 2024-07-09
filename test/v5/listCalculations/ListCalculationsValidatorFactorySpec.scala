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

package v5.listCalculations

import shared.controllers.validators.Validator
import shared.utils.UnitSpec
import v5.listCalculations.def1.Def1_ListCalculationsValidator
import v5.listCalculations.model.request.ListCalculationsRequestData
import v5.listCalculations.schema.ListCalculationsSchema

import javax.inject.Singleton

@Singleton
class ListCalculationsValidatorFactorySpec extends UnitSpec {

  private val validNino    = "ZG903729C"
  private val validTaxYear = "2017-18"

  private val validatorFactory = new ListCalculationsValidatorFactory

  "validator()" when {

    "given any request for schema definition 1" should {
      "return the Validator for schema definition 1" in {
        val result: Validator[ListCalculationsRequestData] =
          validatorFactory.validator(validNino, taxYear = Some(validTaxYear), ListCalculationsSchema.Def1)

        result shouldBe a[Def1_ListCalculationsValidator]
      }
    }

  }

}
