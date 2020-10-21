/*
 * Copyright 2020 HM Revenue & Customs
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

import v1.controllers.requestParsers.validators.validations._
import v1.models.errors.MtdError
import v1.models.request.crystallisation.{CrystallisationRawData, CrystallisationRequestBody}

class CrystallisationValidator extends Validator[CrystallisationRawData] {

  private val validationSet = List(parameterFormatValidation, parameterRuleValidation, bodyFormatValidation, bodyValueValidation)

  override def validate(data: CrystallisationRawData): List[MtdError] = {
    run(validationSet, data).distinct
  }

  private def parameterFormatValidation: CrystallisationRawData => List[List[MtdError]] = (data: CrystallisationRawData) => {
    List(
      NinoValidation.validate(data.nino),
      TaxYearValidation.validate(data.taxYear)
    )
  }

  private def parameterRuleValidation: CrystallisationRawData => List[List[MtdError]] = { data =>
    List(
      TaxYearNotSupportedValidation.validate(data.taxYear)
    )
  }

  private def bodyFormatValidation: CrystallisationRawData => List[List[MtdError]] = (data: CrystallisationRawData) => {
    List(
      JsonFormatValidation.validate[CrystallisationRequestBody](data.body)
    )
  }

  private def bodyValueValidation: CrystallisationRawData => List[List[MtdError]] = (data: CrystallisationRawData) => {
    val requestBodyData: CrystallisationRequestBody = data.body.as[CrystallisationRequestBody]

    List(
      CalculationIdValidation.validate(requestBodyData.calculationId)
    )
  }
}