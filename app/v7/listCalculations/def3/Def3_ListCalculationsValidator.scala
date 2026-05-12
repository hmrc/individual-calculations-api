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

package v7.listCalculations.def3

import api.errors.RuleCalculationTypeNotAllowed
import cats.data.Validated
import cats.implicits.catsSyntaxTuple3Semigroupal
import shared.controllers.validators.Validator
import shared.controllers.validators.resolvers.{ResolveNino, ResolveTaxYearMinimum}
import shared.models.domain.TaxYear
import shared.models.errors.MtdError
import v7.common.model.domain.{CalculationType, `confirm-amendment`, `final-declaration`, `in-year`, `intent-to-amend`, `intent-to-finalise`}
import v7.common.model.resolver.ResolveListCalculationType
import v7.common.model.resolver.ResolveListCalculationType.{ResolverOps, resolveValid}
import v7.listCalculations.model.request.{Def3_ListCalculationsRequestData, ListCalculationsRequestData}

object Def3_ListCalculationsValidator {
  private val listCalculationsMinimumTaxYear = TaxYear.fromMtd("2017-18")

  private val resolveTaxYear = ResolveTaxYearMinimum(listCalculationsMinimumTaxYear).resolver

}

class Def3_ListCalculationsValidator(nino: String, taxYear: String, calculationType: Option[String]) extends Validator[ListCalculationsRequestData] {
  import Def3_ListCalculationsValidator._

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
