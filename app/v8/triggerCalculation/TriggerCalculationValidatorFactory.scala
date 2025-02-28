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

package v8.triggerCalculation

import shared.controllers.validators.Validator
import v8.triggerCalculation.def1.Def1_TriggerCalculationValidator
import v8.triggerCalculation.model.request.TriggerCalculationRequestData
import v8.triggerCalculation.schema.TriggerCalculationSchema

import javax.inject.Singleton

@Singleton
class TriggerCalculationValidatorFactory {

  def validator(nino: String,
                taxYear: String,
                calculationType: String,
                schema: TriggerCalculationSchema): Validator[TriggerCalculationRequestData] =
    schema match {
      case TriggerCalculationSchema.Def1 => new Def1_TriggerCalculationValidator(nino, taxYear, calculationType)
    }

}
