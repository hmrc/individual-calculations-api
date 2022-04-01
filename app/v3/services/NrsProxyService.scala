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

package v3.services

import javax.inject.{Inject, Singleton}
import uk.gov.hmrc.http.HeaderCarrier
import v3.connectors.NrsProxyConnector
import v3.models.domain.CrystallisationRequestBody

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class NrsProxyService @Inject()(val connector: NrsProxyConnector) {

  def submit(nino: String, body: CrystallisationRequestBody)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Unit] = {

    connector.submit(nino, body)
  }

}
