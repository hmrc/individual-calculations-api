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

package v3.controllers.requestParsers.validators

import v3.controllers.requestParsers.validators.validations.{NinoValidation, TaxYearNotSupportedValidation, TaxYearValidation}
import v3.models.errors.MtdError
import v3.models.request.TriggerCalculationRawData

class TriggerCalculationValidator extends Validator[TriggerCalculationRawData] {

  private val validationSet = List(parserValidation, ruleValidation)

  private def parserValidation: TriggerCalculationRawData => List[List[MtdError]] = { data =>
    List(
      NinoValidation.validate(data.nino),
      TaxYearValidation.validate(data.taxYear)
    )
  }

  private def ruleValidation: TriggerCalculationRawData => List[List[MtdError]] = { data =>
    List(TaxYearNotSupportedValidation.validate(data.taxYear))
  }

  override def validate(data: TriggerCalculationRawData): List[MtdError] = run(validationSet, data).distinct
}
