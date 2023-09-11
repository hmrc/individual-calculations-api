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

package v4.services

import play.api.libs.json.JsValue
import support.UnitSpec
import uk.gov.hmrc.http.HeaderCarrier
import v4.mocks.connectors.MockNrsProxyConnector

import java.util.concurrent.ConcurrentLinkedQueue

trait StubNrsProxyService { _: MockNrsProxyConnector with UnitSpec =>

  private val called = new ConcurrentLinkedQueue[NrsProxyCall]()

  protected val stubNrsProxyService: NrsProxyService = new NrsProxyService(mockNrsProxyConnector) {

    override def submit(nino: String, notableEvent: String, body: JsValue)(implicit hc: HeaderCarrier): Unit =
      called.add(NrsProxyCall(nino, notableEvent, body))

  }

  def verifyNrsProxyService(expectations: NrsProxyCall*): Unit = {
    if (called.isEmpty) fail(s"No calls were made to this service, expected ${expectations.length}")
    called should contain theSameElementsAs expectations
  }

  def resetNrsProxyService(): Unit = called.clear()

  protected case class NrsProxyCall(nino: String, notableEvent: String, body: JsValue)
}
