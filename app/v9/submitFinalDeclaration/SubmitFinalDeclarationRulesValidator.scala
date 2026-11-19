/*
 * Copyright 2026 HM Revenue & Customs
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

package v9.submitFinalDeclaration

import api.controllers.validators.RulesValidator
import api.models.errors.{MtdError, RuleSubmissionFailedError}
import cats.data.Validated
import cats.data.Validated.Invalid
import v9.common.model.domain.`confirm-amendment`
import v9.submitFinalDeclaration.model.request.SubmitFinalDeclarationRequestData

object SubmitFinalDeclarationRulesValidator extends RulesValidator[SubmitFinalDeclarationRequestData] {

  def validateBusinessRules(parsed: SubmitFinalDeclarationRequestData): Validated[Seq[MtdError], SubmitFinalDeclarationRequestData] = {

    def validatedCalculationType(request: SubmitFinalDeclarationRequestData): Validated[Seq[MtdError], Unit] = {
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
