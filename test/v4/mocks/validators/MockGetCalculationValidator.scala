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

package v4.mocks.validators

import org.scalamock.handlers.CallHandler1
import org.scalamock.scalatest.MockFactory
import v4.controllers.requestParsers.validators.GetCalculationValidator
import v4.models.errors.MtdError
import v4.models.request.GetCalculationRawData

class MockGetCalculationValidator extends MockFactory {

  val mockValidator: GetCalculationValidator = mock[GetCalculationValidator]

  object MockValidator {

    def validate(data: GetCalculationRawData): CallHandler1[GetCalculationRawData, List[MtdError]] = {
      (mockValidator
        .validate(_: GetCalculationRawData))
        .expects(data)
    }

  }

}