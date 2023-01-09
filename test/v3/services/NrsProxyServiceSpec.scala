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

package v3.services

import v3.fixtures.nrs.NrsFixture
import v3.mocks.connectors.MockNrsProxyConnector

import scala.concurrent.Future

class NrsProxyServiceSpec extends ServiceSpec with NrsFixture with MockNrsProxyConnector {

  val service = new NrsProxyService(mockNrsProxyConnector)

  "NrsProxyService" when {
    "submitting asynchronously" should {
      "forward to the connector" in {
        MockNrsProxyConnector.submit(nino, event, body) returns Future.successful(Right(()))

        service.submitAsync(nino, event, body)
      }
    }
  }

}
