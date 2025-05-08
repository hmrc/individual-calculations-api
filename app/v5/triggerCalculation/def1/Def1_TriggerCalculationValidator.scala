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

package v5.triggerCalculation.def1

import api.errors.FinalDeclarationFormatError
import shared.controllers.validators.Validator
import shared.controllers.validators.resolvers.{ResolveBoolean, ResolveNino, ResolveTaxYearMinMax}
import shared.models.domain.TaxYear
import shared.models.errors.{MtdError, RuleTaxYearForVersionNotSupportedError, RuleTaxYearNotSupportedError}
import cats.data.Validated
import cats.data.Validated.Valid
import cats.implicits._
import v5.triggerCalculation.model.request.{Def1_TriggerCalculationRequestData, TriggerCalculationRequestData}

object Def1_TriggerCalculationValidator {

  private val triggerCalculationMinimumTaxYear = TaxYear.fromMtd("2017-18")
  private val triggerCalculationMaximumSupportedTaxYear = TaxYear.fromMtd("2024-25")
  private val resolveTaxYear                     = ResolveTaxYearMinMax(
    (triggerCalculationMinimumTaxYear, triggerCalculationMaximumSupportedTaxYear),
    RuleTaxYearNotSupportedError,
    RuleTaxYearForVersionNotSupportedError)

  private val resolveFinalDeclaration = ResolveBoolean(FinalDeclarationFormatError)

}

class Def1_TriggerCalculationValidator(nino: String, taxYear: String, finalDeclaration: Option[String])
    extends Validator[TriggerCalculationRequestData] {

  import Def1_TriggerCalculationValidator._

  def validate: Validated[Seq[MtdError], TriggerCalculationRequestData] =
    (
      ResolveNino(nino),
      resolveTaxYear(taxYear),
      finalDeclaration match {
        case Some(finalDeclaration) => resolveFinalDeclaration(finalDeclaration)
        case None                   => Valid(false)
      }
    ).mapN(Def1_TriggerCalculationRequestData)

}
