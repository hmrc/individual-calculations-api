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

package v6.retrieveCalculation.def1

import shared.controllers.validators.Validator
import shared.controllers.validators.resolvers.{ResolveCalculationId, ResolveNino, ResolveTaxYearMinMax}
import shared.models.domain.TaxYear
import shared.models.errors.{MtdError, RuleTaxYearForVersionNotSupportedError, RuleTaxYearNotSupportedError}
import cats.data.Validated
import cats.implicits._
import v6.retrieveCalculation.models.request.{Def1_RetrieveCalculationRequestData, RetrieveCalculationRequestData}

object Def1_RetrieveCalculationValidator {
  private val retrieveCalculationsMinimumTaxYear = TaxYear.fromMtd("2017-18")
  private val retrieveCalculationsMaximumSupportedTaxYear = TaxYear.fromMtd("2024-25")
  private val resolveTaxYear                     = ResolveTaxYearMinMax(
    (retrieveCalculationsMinimumTaxYear, retrieveCalculationsMaximumSupportedTaxYear),
    RuleTaxYearNotSupportedError,
    RuleTaxYearForVersionNotSupportedError)
}

class Def1_RetrieveCalculationValidator(nino: String, taxYear: String, calculationId: String) extends Validator[RetrieveCalculationRequestData] {

  import Def1_RetrieveCalculationValidator._

  def validate: Validated[Seq[MtdError], RetrieveCalculationRequestData] =
    (
      ResolveNino(nino),
      resolveTaxYear(taxYear),
      ResolveCalculationId(calculationId)
    ).mapN(Def1_RetrieveCalculationRequestData)

}
