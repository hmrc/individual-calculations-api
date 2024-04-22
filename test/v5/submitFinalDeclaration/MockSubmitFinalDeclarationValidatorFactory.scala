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

package v5.submitFinalDeclaration

import api.controllers.validators.{MockValidatorFactory, Validator}
import org.scalamock.handlers.CallHandler
import v5.submitFinalDeclaration.model.request.SubmitFinalDeclarationRequestData
import v5.submitFinalDeclaration.schema.SubmitFinalDeclarationSchema

trait MockSubmitFinalDeclarationValidatorFactory extends MockValidatorFactory[SubmitFinalDeclarationRequestData] {

  val mockSubmitFinalDeclarationValidatorFactory: SubmitFinalDeclarationValidatorFactory = mock[SubmitFinalDeclarationValidatorFactory]

  def validator(): CallHandler[Validator[SubmitFinalDeclarationRequestData]] =
    (mockSubmitFinalDeclarationValidatorFactory.validator(_: String, _: String, _: String, _: SubmitFinalDeclarationSchema)).expects(*, *, *, *)

}
