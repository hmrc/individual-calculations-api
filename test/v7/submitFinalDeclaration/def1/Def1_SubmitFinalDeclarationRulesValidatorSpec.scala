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

package v7.submitFinalDeclaration.def1

import api.errors.RuleSubmissionFailedError
import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import shared.models.domain.{CalculationId, Nino, TaxYear}
import shared.models.errors.MtdError
import shared.utils.UnitSpec
import v7.submitFinalDeclaration.model.request.Def1_SubmitFinalDeclarationRequestData
import v7.submitFinalDeclaration.model.request.domain.{CalculationType, `confirm-amendment`}

class Def1_SubmitFinalDeclarationRulesValidatorSpec extends UnitSpec {

  private val validNino          = Nino("ZG903729C")
  private val taxYear1780       = TaxYear.fromMtd("2017-18")
  private val taxYear2082       = TaxYear.fromMtd("2025-26")
  private val calculationId = CalculationId("f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c")
  private val confirmAmendment: CalculationType = `confirm-amendment`

  private def ruleValidator(nino: Nino, taxYear: TaxYear,
                            calculationId: CalculationId, calculationType: CalculationType): Validated[Seq[MtdError], Def1_SubmitFinalDeclarationRequestData] =
    Def1_SubmitFinalDeclarationRulesValidator.validateBusinessRules(Def1_SubmitFinalDeclarationRequestData(nino, taxYear, calculationId, calculationType)
  )

  "Def1_SubmitFinalDeclarationRulesValidatorSpec.validateBusinessRules" should {
    "return a Rule submission error" in {
      ruleValidator(validNino, taxYear1780, calculationId, confirmAmendment) shouldBe Invalid(Seq(RuleSubmissionFailedError))
    }
    "return valid request data" in {
      ruleValidator(validNino, taxYear2082, calculationId, confirmAmendment) shouldBe Valid(Def1_SubmitFinalDeclarationRequestData(validNino, taxYear2082, calculationId, confirmAmendment))
    }
  }

}
