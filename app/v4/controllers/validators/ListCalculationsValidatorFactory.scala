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

import shared.controllers.validators.Validator
import shared.controllers.validators.resolvers.ResolverSupport._
import shared.controllers.validators.resolvers.{ResolveNino, ResolveTaxYear}
import shared.models.domain.TaxYear
import shared.models.errors.{MtdError, RuleTaxYearNotSupportedError}
import cats.data.Validated
import cats.implicits._
import v4.models.request.ListCalculationsRequestData

import javax.inject.Singleton

@Singleton
class ListCalculationsValidatorFactory {

  private val listCalculationsMinimumTaxYear = TaxYear.fromMtd("2017-18")

  private val resolveTaxYear = ResolveTaxYear.resolver.resolveOptionallyWithDefault(TaxYear.currentTaxYear) thenValidate
    satisfiesMin(listCalculationsMinimumTaxYear, RuleTaxYearNotSupportedError)

  def validator(nino: String, taxYear: Option[String]): Validator[ListCalculationsRequestData] =
    new Validator[ListCalculationsRequestData] {

      def validate: Validated[Seq[MtdError], ListCalculationsRequestData] =
        (
          ResolveNino(nino),
          resolveTaxYear(taxYear)
        ).mapN(ListCalculationsRequestData)

    }

}
