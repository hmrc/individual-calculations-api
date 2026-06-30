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

package v9.listCalculations.def3

import api.controllers.validators.Validator
import api.controllers.validators.resolvers.{ResolveNino, ResolveTaxYearMinimum}
import api.errors.RuleCalculationTypeNotAllowed
import api.models.domain.TaxYear
import api.models.errors.MtdError
import cats.data.Validated
import cats.implicits.catsSyntaxTuple3Semigroupal
import v9.common.model.domain.*
import v9.common.model.resolver.ResolveListCalculationType
import v9.common.model.resolver.ResolveListCalculationType.{ResolverOps, resolveValid}
import v9.listCalculations.model.request.{Def3_ListCalculationsRequestData, ListCalculationsRequestData}

object Def3_ListCalculationsValidator {
  private val listCalculationsMinimumTaxYear = TaxYear.fromMtd("2017-18")

  private val resolveTaxYear = ResolveTaxYearMinimum(listCalculationsMinimumTaxYear).resolver

}

class Def3_ListCalculationsValidator(nino: String, taxYear: String, calculationType: Option[String]) extends Validator[ListCalculationsRequestData] {
  import Def3_ListCalculationsValidator.*

  def validate: Validated[Seq[MtdError], ListCalculationsRequestData] =
    (
      ResolveNino(nino),
      resolveTaxYear(taxYear),
      ResolveListCalculationType(calculationType)
    ).mapN(Def3_ListCalculationsRequestData.apply)
      .andThen(validateRules)

  private val validateRules = {
    def isValidCalcTypeForDef3(calcType: CalculationType): Boolean =
      Seq(`in-year`, `final-declaration`, `intent-to-finalise`, `intent-to-amend`, `confirm-amendment`)
        .contains(calcType)

    val validateCalcTypeForTaxYear = (request: ListCalculationsRequestData) =>
      request.calculationType.flatMap {
        case ct if isValidCalcTypeForDef3(ct) => None
        case _                                => Some(List(RuleCalculationTypeNotAllowed))
      }

    resolveValid[ListCalculationsRequestData]
      .thenValidate(validateCalcTypeForTaxYear)
  }

}
