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

package v9.retrieveCalculation.def3

import api.controllers.validators.Validator
import api.controllers.validators.resolvers.{ResolveCalculationId, ResolveNino, ResolveTaxYearMinimum}
import api.models.domain.TaxYear
import api.models.errors.MtdError
import cats.data.Validated
import cats.implicits.*
import v9.retrieveCalculation.models.request.{Def3_RetrieveCalculationRequestData, RetrieveCalculationRequestData}

object Def3_RetrieveCalculationValidator {
  private val retrieveCalculationsMinimumTaxYear = TaxYear.fromMtd("2025-26")
  private val resolveTaxYear                     = ResolveTaxYearMinimum(retrieveCalculationsMinimumTaxYear)
}

class Def3_RetrieveCalculationValidator(nino: String, taxYear: String, calculationId: String) extends Validator[RetrieveCalculationRequestData] {
  import Def3_RetrieveCalculationValidator.*

  def validate: Validated[Seq[MtdError], RetrieveCalculationRequestData] =
    (
      ResolveNino(nino),
      resolveTaxYear(taxYear),
      ResolveCalculationId(calculationId)
    ).mapN(Def3_RetrieveCalculationRequestData.apply)

}
