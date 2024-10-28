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

package v7.submitFinalDeclaration

import shared.controllers.validators.Validator
import shared.utils.UnitSpec
import v7.submitFinalDeclaration.def1.Def1_SubmitFinalDeclarationValidator
import v7.submitFinalDeclaration.model.request.SubmitFinalDeclarationRequestData
import v7.submitFinalDeclaration.schema.SubmitFinalDeclarationSchema

class SubmitFinalDeclarationValidatorFactorySpec extends UnitSpec {

  private val validNino          = "ZG903729C"
  private val taxYear            = "2017-18"
  private val taxYear2526        = "2025-126"
  private val validCalculationId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"
  private val calculationTypeDf  = "final-declaration"
  private val calculationTypeCa  = "confirm-amendment"

  private val validatorFactory = new SubmitFinalDeclarationValidatorFactory

  "validator()" when {

    "given any request regardless of tax year" should {
      "return the Validator for schema definition 1 when calculationType is final-declaration" in {
        val result: Validator[SubmitFinalDeclarationRequestData] =
          validatorFactory.validator(validNino, taxYear, validCalculationId, calculationTypeDf, SubmitFinalDeclarationSchema.Def1)

        result shouldBe a[Def1_SubmitFinalDeclarationValidator]
      }
      "return the Validator for schema definition 1 when confirm-amendment is taxType" in {
        val result: Validator[SubmitFinalDeclarationRequestData] =
          validatorFactory.validator(validNino, taxYear2526, validCalculationId, calculationTypeCa, SubmitFinalDeclarationSchema.Def1)

        result shouldBe a[Def1_SubmitFinalDeclarationValidator]
      }
    }
  }

}
