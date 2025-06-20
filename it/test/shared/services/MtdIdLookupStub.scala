/*
 * Copyright 2025 HM Revenue & Customs
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

package shared.services

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.Status.OK
import play.api.libs.json.Json
import shared.support.WireMockMethods

object MtdIdLookupStub extends WireMockMethods {

  private def lookupUrl(nino: String): String = s"/mtd-identifier-lookup/nino/$nino"

  def ninoFound(nino: String): StubMapping =
    when(method = GET, uri = lookupUrl(nino))
      .thenReturn(status = OK, body = Json.obj("mtdbsa" -> "1234567890"))

  def error(nino: String, status: Int): StubMapping =
    when(method = GET, uri = lookupUrl(nino))
      .thenReturn(status, body = Json.obj())

}
