/*
 * Copyright 2022 HM Revenue & Customs
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

package v3.controllers.requestParsers.validators.validations

import config.FixedConfig
import v3.models.domain.TaxYear
import v3.models.errors.{MtdError, RuleTaxYearNotSupportedError}

object TaxYearNotSupportedValidation extends FixedConfig {

  // @param taxYear In format YYYY-YY
  def validate(mtdTaxYear: String): List[MtdError] = {
    val taxYear = Integer.parseInt(TaxYear.fromMtd(mtdTaxYear).toDownstream)

    if (taxYear < minimumTaxYear) List(RuleTaxYearNotSupportedError) else NoValidationErrors
  }

}
