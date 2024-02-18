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

package v4.controllers.requestParsers

import api.controllers.requestParsers.RequestParser
import api.models.domain.{Nino, TaxYear}
import v4.controllers.requestParsers.validators.TriggerCalculationValidator
import v4.models.request.{TriggerCalculationRawData, TriggerCalculationRequestData}

import javax.inject.Inject

class TriggerCalculationParser @Inject() (val validator: TriggerCalculationValidator)
    extends RequestParser[TriggerCalculationRawData, TriggerCalculationRequestData] {

  override protected def requestFor(data: TriggerCalculationRawData): TriggerCalculationRequestData = {
    val nino             = Nino(data.nino)
    val taxYear          = TaxYear.fromMtd(data.taxYear)
    val finalDeclaration = data.finalDeclaration.exists(_.toBoolean)

    TriggerCalculationRequestData(nino, taxYear, finalDeclaration)
  }

}
