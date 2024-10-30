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

import cats.data.Validated
import cats.implicits._
import shared.controllers.validators.Validator
import shared.controllers.validators.resolvers.{ResolveCalculationId, ResolveNino, ResolveTaxYear}
import shared.models.errors.MtdError
import v7.common.model.resolver.ResolveCalculationType
import v7.submitFinalDeclaration.model.request.{Def1_SubmitFinalDeclarationRequestData, SubmitFinalDeclarationRequestData}

class Def1_SubmitFinalDeclarationValidator(nino: String, taxYear: String, calculationId: String, calculationType: String)
    extends Validator[SubmitFinalDeclarationRequestData] {

  def validate: Validated[Seq[MtdError], SubmitFinalDeclarationRequestData] =
    (
      ResolveNino(nino),
      ResolveTaxYear(taxYear),
      ResolveCalculationId(calculationId),
      ResolveCalculationType(calculationType)
    ).mapN(Def1_SubmitFinalDeclarationRequestData) andThen Def1_SubmitFinalDeclarationRulesValidator.validateBusinessRules

}
