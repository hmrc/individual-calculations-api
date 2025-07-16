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

package v8.triggerCalculation.def1

import api.errors.RuleCalculationTypeNotAllowed
import cats.data.Validated
import cats.implicits._
import shared.controllers.validators.Validator
import shared.controllers.validators.resolvers.ResolverSupport._
import shared.controllers.validators.resolvers.{ResolveNino, ResolveTaxYearMinimum}
import shared.models.domain.TaxYear
import shared.models.errors.MtdError
import v8.common.model.domain.{Either24or25Downstream, Post26Downstream, Pre24Downstream, `intent-to-amend`}
import v8.common.model.resolver.ResolveTriggerCalculationType
import v8.triggerCalculation.model.request.{Def1_TriggerCalculationRequestData, TriggerCalculationRequestData}

object Def1_TriggerCalculationValidator {

  private val triggerCalculationMinimumTaxYear = TaxYear.fromMtd("2017-18")
  private val resolveTaxYear                   = ResolveTaxYearMinimum(triggerCalculationMinimumTaxYear)
}

class Def1_TriggerCalculationValidator(nino: String, taxYear: String, calculationType: String)
  extends Validator[TriggerCalculationRequestData] {

  import Def1_TriggerCalculationValidator._

  def validate: Validated[Seq[MtdError], TriggerCalculationRequestData] =
    (
      ResolveNino(nino),
      resolveTaxYear(taxYear),
      ResolveTriggerCalculationType(calculationType)
    ).mapN(Def1_TriggerCalculationRequestData.apply)
      .andThen(validateRules)
      .map(provideTysDownstream)

  private def provideTysDownstream(request: TriggerCalculationRequestData): TriggerCalculationRequestData = {

    val tysDownstream = {
      val taxYear = request.taxYear.year
      if (taxYear <= 2023) {
        Pre24Downstream
      } else if (taxYear == 2024 || taxYear == 2025) {
        Either24or25Downstream
      } else {
        //must be 2026 or later
        Post26Downstream
      }
    }

    Def1_TriggerCalculationRequestData(request.nino, request.taxYear, request.calculationType, tysDownstream)
  }

  private val validateRules = {

    val validateCalcTypeForTaxYear = (request: TriggerCalculationRequestData) =>
      request.calculationType match {
        case `intent-to-amend` if TaxYear.fromMtd(taxYear).year <= 2025 =>  Some(List(RuleCalculationTypeNotAllowed))
        case _ => None
      }

    resolveValid[TriggerCalculationRequestData]
      .thenValidate(validateCalcTypeForTaxYear)
  }

}
