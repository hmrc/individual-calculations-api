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

package v6.listCalculations

import shared.controllers.validators.Validator
import v6.listCalculations.def1.Def1_ListCalculationsValidator
import v6.listCalculations.model.request.ListCalculationsRequestData
import v6.listCalculations.schema.ListCalculationsSchema

import javax.inject.Singleton

@Singleton
class ListCalculationsValidatorFactory {

  def validator(nino: String, taxYear: Option[String], schema: ListCalculationsSchema): Validator[ListCalculationsRequestData] =
    schema match {
      case ListCalculationsSchema.Def1 => new Def1_ListCalculationsValidator(nino, taxYear)
    }

}
