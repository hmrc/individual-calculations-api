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

import play.api.libs.json.JsValue
import uk.gov.hmrc.http.HeaderCarrier
import v3.connectors.NrsProxyConnector

import javax.inject.{Inject, Singleton}

@Singleton
class NrsProxyService @Inject() (val connector: NrsProxyConnector) {

  def submitAsync(nino: String, notableEvent: String, body: JsValue)(implicit hc: HeaderCarrier): Unit = {
    connector.submit(nino, notableEvent, body)
  }

}
