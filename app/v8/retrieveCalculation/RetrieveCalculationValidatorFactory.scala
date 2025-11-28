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

package v8.retrieveCalculation

import shared.controllers.validators.Validator
import v8.retrieveCalculation.def1.Def1_RetrieveCalculationValidator
import v8.retrieveCalculation.def2.Def2_RetrieveCalculationValidator
import v8.retrieveCalculation.def3.Def3_RetrieveCalculationValidator
import v8.retrieveCalculation.def4.Def4_RetrieveCalculationValidator
import v8.retrieveCalculation.models.request.RetrieveCalculationRequestData
import v8.retrieveCalculation.schema.RetrieveCalculationSchema

import javax.inject.Singleton

@Singleton
class RetrieveCalculationValidatorFactory {

  def validator(nino: String, taxYear: String, calculationId: String, schema: RetrieveCalculationSchema): Validator[RetrieveCalculationRequestData] =
    schema match {
      case RetrieveCalculationSchema.Def1 => new Def1_RetrieveCalculationValidator(nino, taxYear, calculationId)
      case RetrieveCalculationSchema.Def2 => new Def2_RetrieveCalculationValidator(nino, taxYear, calculationId)
      case RetrieveCalculationSchema.Def3 => new Def3_RetrieveCalculationValidator(nino, taxYear, calculationId)
      case RetrieveCalculationSchema.Def4 => new Def4_RetrieveCalculationValidator(nino, taxYear, calculationId)
    }

}
