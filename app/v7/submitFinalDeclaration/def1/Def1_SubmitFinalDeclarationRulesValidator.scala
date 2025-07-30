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

package v7.submitFinalDeclaration.def1

import api.errors.RuleSubmissionFailedError
import cats.data.Validated
import cats.data.Validated.Invalid
import shared.controllers.validators.RulesValidator
import shared.models.errors.MtdError
import v7.common.model.domain.`confirm-amendment`
import v7.submitFinalDeclaration.model.request.Def1_SubmitFinalDeclarationRequestData

object Def1_SubmitFinalDeclarationRulesValidator extends RulesValidator[Def1_SubmitFinalDeclarationRequestData] {

  def validateBusinessRules(parsed: Def1_SubmitFinalDeclarationRequestData): Validated[Seq[MtdError], Def1_SubmitFinalDeclarationRequestData] = {

    def validatedCalculationType(request: Def1_SubmitFinalDeclarationRequestData): Validated[Seq[MtdError], Unit] = {
      request.calculationType match {
        case `confirm-amendment` if request.taxYear.year < 2026 => Invalid(Seq(RuleSubmissionFailedError))
        case _                                                  => valid
      }
    }

    combine(
      validatedCalculationType(parsed)
    ).onSuccess(parsed)
  }

}
