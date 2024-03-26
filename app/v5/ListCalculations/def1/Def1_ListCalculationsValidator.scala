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

package v5.ListCalculations.def1

import api.controllers.validators.Validator
import api.controllers.validators.resolvers.ResolverSupport._
import api.controllers.validators.resolvers.{ResolveNino, ResolveTaxYear}
import api.models.domain.TaxYear
import api.models.errors.{MtdError, RuleTaxYearNotSupportedError}
import cats.data.Validated
import cats.implicits.catsSyntaxTuple2Semigroupal
import v5.ListCalculations.model.request.{Def1_ListCalculationsRequestData, ListCalculationsRequestData}

class Def1_ListCalculationsValidator(nino: String, taxYear: Option[String]) extends Validator[ListCalculationsRequestData] {

  private val listCalculationsMinimumTaxYear = TaxYear.fromMtd("2017-18")

  private val resolveTaxYear = ResolveTaxYear.resolver.resolveOptionallyWithDefault(TaxYear.currentTaxYear) thenValidate
    satisfiesMin(listCalculationsMinimumTaxYear, RuleTaxYearNotSupportedError)

  def validate: Validated[Seq[MtdError], ListCalculationsRequestData] =
    (
      ResolveNino(nino),
      resolveTaxYear(taxYear)
    ).mapN(Def1_ListCalculationsRequestData)

}
