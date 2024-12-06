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

package v7.listCalculations

import shared.controllers.validators.Validator
import v7.listCalculations.def1.Def1_ListCalculationsValidator
import v7.listCalculations.def2.Def2_ListCalculationsValidator
import v7.listCalculations.def3.Def3_ListCalculationsValidator
import v7.listCalculations.model.request.ListCalculationsRequestData
import v7.listCalculations.schema.ListCalculationsSchema

import javax.inject.Singleton

@Singleton
class ListCalculationsValidatorFactory {

  def validator(nino: String, taxYear: String, calculationType: Option[String]): Validator[ListCalculationsRequestData] =
    ListCalculationsSchema.schemaFor(taxYear) match {
      case ListCalculationsSchema.Def1 => new Def1_ListCalculationsValidator(nino, taxYear, calculationType)
      case ListCalculationsSchema.Def2 => new Def2_ListCalculationsValidator(nino, taxYear, calculationType)
      case ListCalculationsSchema.Def3 => new Def3_ListCalculationsValidator(nino, taxYear, calculationType)
    }

}