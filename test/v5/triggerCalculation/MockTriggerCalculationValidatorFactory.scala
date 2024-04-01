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

package v5.triggerCalculation

import api.controllers.validators.{MockValidatorFactory, Validator}
import org.scalamock.handlers.CallHandler
import v5.triggerCalculation.model.request.TriggerCalculationRequestData

trait MockTriggerCalculationValidatorFactory extends MockValidatorFactory[TriggerCalculationRequestData] {

  val mockTriggerCalculationValidatorFactory: TriggerCalculationValidatorFactory = mock[TriggerCalculationValidatorFactory]

  def validator(): CallHandler[Validator[TriggerCalculationRequestData]] =
    (mockTriggerCalculationValidatorFactory.validator(_: String, _: String, _: Option[String])).expects(*, *, *)

}
