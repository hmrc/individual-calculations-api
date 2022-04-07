/*
 * Copyright 2022 HM Revenue & Customs
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

package v2.controllers.requestParsers

import javax.inject.Inject
import v2.controllers.requestParsers.validators.CrystallisationValidator
import v2.models.domain.{CrystallisationRequestBody, DownstreamTaxYear, Nino}
import v2.models.request.crystallisation.{CrystallisationRawData, CrystallisationRequest}

class CrystallisationRequestParser @Inject() (val validator: CrystallisationValidator)
    extends RequestParser[CrystallisationRawData, CrystallisationRequest] {

  override protected def requestFor(data: CrystallisationRawData): CrystallisationRequest =
    CrystallisationRequest(Nino(data.nino), DownstreamTaxYear.fromMtd(data.taxYear), data.body.json.as[CrystallisationRequestBody].calculationId)

}
