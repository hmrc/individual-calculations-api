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

import api.controllers.requestParsers.validators.Validator
import api.controllers.requestParsers.validators.validations.{CalculationIdValidation, NinoValidation, TaxYearValidation}
import api.models.errors.MtdError
import v4.models.request.SubmitFinalDeclarationRawData

class SubmitFinalDeclarationValidator extends Validator[SubmitFinalDeclarationRawData] {

  private val validationSet = List(parserValidation)

  private def parserValidation: SubmitFinalDeclarationRawData => List[List[MtdError]] = { data =>
    List(
      NinoValidation.validate(data.nino),
      TaxYearValidation.validate(data.taxYear),
      CalculationIdValidation.validate(data.calculationId)
    )
  }

  override def validate(data: SubmitFinalDeclarationRawData): List[MtdError] = run(validationSet, data).distinct
}
