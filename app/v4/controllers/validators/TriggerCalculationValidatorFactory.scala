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

package v4.controllers.validators

import api.controllers.validators.Validator
import api.controllers.validators.resolvers.{ResolveBoolean, ResolveNino, ResolveTaxYearMinimum}
import api.models.domain.TaxYear
import api.models.errors.{FinalDeclarationFormatError, MtdError}
import cats.data.Validated
import cats.data.Validated.Valid
import cats.implicits._
import v4.models.request.TriggerCalculationRequestData

import javax.inject.Singleton

@Singleton
class TriggerCalculationValidatorFactory {

  private val triggerCalculationMinimumTaxYear = TaxYear.fromMtd("2017-18")
  private val resolveTaxYear                   = ResolveTaxYearMinimum(triggerCalculationMinimumTaxYear)

  private val resolveFinalDeclaration = ResolveBoolean(FinalDeclarationFormatError)

  def validator(nino: String, taxYear: String, finalDeclaration: Option[String]): Validator[TriggerCalculationRequestData] =
    new Validator[TriggerCalculationRequestData] {

      def validate: Validated[Seq[MtdError], TriggerCalculationRequestData] =
        (
          ResolveNino(nino),
          resolveTaxYear(taxYear),
          finalDeclaration match {
            case Some(finalDeclaration) => resolveFinalDeclaration(finalDeclaration)
            case None                   => Valid(false)
          }
        ).mapN(TriggerCalculationRequestData)

    }

}
