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

package v7.listCalculations.def1

import cats.data.Validated
import cats.implicits.catsSyntaxTuple2Semigroupal
import shared.controllers.validators.Validator
import shared.controllers.validators.resolvers.{ResolveNino, ResolveTaxYearMinimum}
import shared.models.domain.TaxYear
import shared.models.errors.MtdError
import v7.listCalculations.model.request.{Def1_ListCalculationsRequestData, ListCalculationsRequestData}

object Def1_ListCalculationsValidator {
  private val listCalculationsMinimumTaxYear = TaxYear.fromMtd("2017-18")

  private val resolveTaxYear = ResolveTaxYearMinimum(listCalculationsMinimumTaxYear).resolver

}

class Def1_ListCalculationsValidator(nino: String, taxYear: String) extends Validator[ListCalculationsRequestData] {
  import Def1_ListCalculationsValidator._

  def validate: Validated[Seq[MtdError], ListCalculationsRequestData] =
    (
      ResolveNino(nino),
      resolveTaxYear(taxYear)
    ).mapN(Def1_ListCalculationsRequestData)

}
