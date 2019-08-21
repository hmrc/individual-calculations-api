/*
 * Copyright 2019 HM Revenue & Customs
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
import com.fasterxml.jackson.databind.deser.std.UntypedObjectDeserializer.Vanilla
import v1.controllers.requestParsers.validators.validations.{CalculationIdValidation, NinoValidation}
import v1.models.errors.{CalculationIdFormatError, MtdError}
import v1.models.requestData.selfAssessment.RetrieveCalculationMetadataRawData

class RetrieveCalculationMetadataValidator extends Validator[RetrieveCalculationMetadataRawData]{
  private val validationSet = List(parameterFormatValidation)

  private def parameterFormatValidation: RetrieveCalculationMetadataRawData => List[List[MtdError]] = {
    data => List(NinoValidation.validate(data.nino),
      CalculationIdValidation.validate(data.calculationId))
  }
  override def validate(data: RetrieveCalculationMetadataRawData): List[MtdError] = {
    run(validationSet,data).distinct
  }
}
