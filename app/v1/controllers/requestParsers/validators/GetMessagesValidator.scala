/*
 * Copyright 2021 HM Revenue & Customs
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

import v1.controllers.requestParsers.validators.validations.{ CalculationIdValidation, NinoValidation, MessageTypeValidation}
import v1.models.errors.MtdError
import v1.models.request.GetMessagesRawData

class GetMessagesValidator extends Validator[GetMessagesRawData] {
  private val validationSet = List(parameterFormatValidation)

  override def validate(data: GetMessagesRawData): List[MtdError] = {
    run(validationSet, data).distinct
  }

  private def parameterFormatValidation: GetMessagesRawData => List[List[MtdError]] = { data =>
    List(NinoValidation.validate(data.nino), CalculationIdValidation.validate(data.calculationId), MessageTypeValidation.validateList(data.queryData))
  }
}