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

package v5.submitFinalDeclaration

import api.controllers.validators.Validator
import api.controllers.validators.resolvers.{ResolveCalculationId, ResolveNino, ResolveTaxYear}
import api.models.errors.MtdError
import cats.data.Validated
import cats.implicits.catsSyntaxTuple3Semigroupal
import v4.models.request.SubmitFinalDeclarationRequestData

import javax.inject.Singleton

@Singleton
class SubmitFinalDeclarationValidatorFactory {

  def validator(nino: String, taxYear: String, calculationId: String): Validator[SubmitFinalDeclarationRequestData] =
    new Validator[SubmitFinalDeclarationRequestData] {

      def validate: Validated[Seq[MtdError], SubmitFinalDeclarationRequestData] =
        (
          ResolveNino(nino),
          ResolveTaxYear(taxYear),
          ResolveCalculationId(calculationId)
        ).mapN(SubmitFinalDeclarationRequestData)

    }

}
