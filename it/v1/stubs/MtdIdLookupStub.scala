/*
 * Copyright 2021 HM Revenue & Customs
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

package v1.stubs

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.Status._
import play.api.libs.json.Json
import support.WireMockMethods

object MtdIdLookupStub extends WireMockMethods {

  private def lookupUrl(nino: String): String = s"/mtd-identifier-lookup/nino/$nino"

  def ninoFound(nino: String): StubMapping = {
    when(method = GET, uri = lookupUrl(nino))
      .thenReturn(status = OK, body = Json.obj("mtdbsa" -> "12345678"))
  }

  def unauthorised(nino: String): StubMapping = {
    when(method = GET, uri = lookupUrl(nino))
      .thenReturn(status = FORBIDDEN, body = Json.obj())
  }

  def badRequest(nino: String): StubMapping = {
    when(method = GET, uri = lookupUrl(nino))
      .thenReturn(status = BAD_REQUEST, body = Json.obj())
  }

  def internalServerError(nino: String): StubMapping = {
    when(method = GET, uri = lookupUrl(nino))
      .thenReturn(status = INTERNAL_SERVER_ERROR, body = Json.obj())
  }

}
