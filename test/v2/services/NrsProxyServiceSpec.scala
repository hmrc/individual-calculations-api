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

package v2.services

import v2.mocks.connectors.MockNrsProxyConnector
import v2.models.domain.CrystallisationRequestBody

import scala.concurrent.Future

class NrsProxyServiceSpec extends ServiceSpec {

  trait Test extends MockNrsProxyConnector {
    lazy val service = new NrsProxyService(mockNrsProxyConnector)
  }

  "NrsProxyService" should {
    "call the Nrs Proxy connector" when {
      "the connector is valid" in new Test {

        MockNrsProxyConnector.submit(nino.toString, CrystallisationRequestBody("4557ecb5-fd32-48cc-81f5-e6acd1099f3c"))
          .returns(Future.successful((): Unit))

        await(service.submit(nino.toString, CrystallisationRequestBody("4557ecb5-fd32-48cc-81f5-e6acd1099f3c"))) shouldBe ((): Unit)

      }
    }

  }

}
