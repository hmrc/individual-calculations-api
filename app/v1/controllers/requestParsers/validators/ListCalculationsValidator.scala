/*
 * Copyright 2019 HM Revenue & Customs
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

package v1.controllers.requestParsers.validators

import config.FixedConfig
import v1.controllers.requestParsers.validators.validations.{MtdTaxYearValidation, NinoValidation, TaxYearValidation}
import v1.models.errors.MtdError
import v1.models.requestData.selfAssessment.ListCalculationsRawData

class ListCalculationsValidator extends Validator[ListCalculationsRawData] with FixedConfig {

  private val validationSet = List(parameterFormatValidation, parameterRuleValidation)

  private def parameterFormatValidation: ListCalculationsRawData => List[List[MtdError]] = { data =>
    List(
      NinoValidation.validate(data.nino),
      data.taxYear.map(taxYear => TaxYearValidation.validate(taxYear)).getOrElse(Nil)
    )
  }

  private def parameterRuleValidation: ListCalculationsRawData => List[List[MtdError]] = { data =>
    List(
      data.taxYear.map(taxYear => MtdTaxYearValidation.validate(taxYear, minimumTaxYear)).getOrElse(Nil)
    )
  }

  override def validate(data: ListCalculationsRawData): List[MtdError] = {
    run(validationSet, data).distinct
  }
}
