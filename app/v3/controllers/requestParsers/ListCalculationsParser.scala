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

package v3.controllers.requestParsers

import v3.controllers.requestParsers.validators.ListCalculationsValidator
import v3.models.domain.{Nino, TaxYear}
import v3.models.request._

import javax.inject.{Inject, Singleton}

@Singleton
class ListCalculationsParser @Inject() (val validator: ListCalculationsValidator)
    extends RequestParser[ListCalculationsRawData, ListCalculationsRequest] {

  override protected def requestFor(data: ListCalculationsRawData): ListCalculationsRequest = ListCalculationsRequest(
    Nino(data.nino),
    data.taxYear.map(TaxYear.fromMtd)
  )

}
