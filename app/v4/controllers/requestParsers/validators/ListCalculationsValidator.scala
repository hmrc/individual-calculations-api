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

package v4.controllers.requestParsers.validators

import api.controllers.requestParsers.validators.ValidatorOld
import api.controllers.requestParsers.validators.validations.{NinoValidation, TaxYearNotSupportedValidation, TaxYearValidation}
import api.models.errors.MtdError
import v4.models.request.ListCalculationsRawData

class ListCalculationsValidator extends ValidatorOld[ListCalculationsRawData] {

  private val validationSet = List(parserValidation, ruleValidation)

  private def parserValidation: ListCalculationsRawData => List[List[MtdError]] = { data =>
    List(
      NinoValidation.validate(data.nino),
      data.taxYear.fold(List.empty[MtdError])(taxYear => TaxYearValidation.validate(taxYear))
    )
  }

  private def ruleValidation: ListCalculationsRawData => List[List[MtdError]] = { data =>
    List(
      data.taxYear.fold(List.empty[MtdError])(taxYear => TaxYearNotSupportedValidation.validate(taxYear))
    )
  }

  override def validate(data: ListCalculationsRawData): List[MtdError] = run(validationSet, data).distinct
}
