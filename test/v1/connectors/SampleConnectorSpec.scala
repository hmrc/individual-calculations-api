/*
 * Copyright 2019 HM Revenue & Customs
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

package v1.connectors

import uk.gov.hmrc.domain.Nino
import v1.mocks.{MockAppConfig, MockHttpClient}
import v1.models.backend.BackendSampleResponse
import v1.models.domain.{EmptyJsonBody, SampleRequestBody}
import v1.models.outcomes.ResponseWrapper
import v1.models.requestData.{TaxYear, SampleRequestData}

import scala.concurrent.Future

class SampleConnectorSpec extends ConnectorSpec {

  val taxYear = "2018"
  val nino = Nino("AA123456A")
  val calcId = "041f7e4d-87b9-4d4a-a296-3cfbdf92f7e2"

  class Test extends MockHttpClient with MockAppConfig {
    val connector: SampleConnector = new SampleConnector(http = mockHttpClient, appConfig = mockAppConfig)

    val desRequestHeaders = Seq("Authorization" -> s"Bearer backend-token")
    MockedAppConfig.backendBaseUrl returns baseUrl
    MockedAppConfig.backendToken returns "backend-token"
  }

  "doService" must {
    val request = SampleRequestData(nino, taxYear, SampleRequestBody("someData"))

    "post an empty body and return the result" in new Test {
      val outcome = Right(ResponseWrapper(correlationId, BackendSampleResponse(calcId)))

      MockedHttpClient
        .post(s"$baseUrl/income-tax/nino/${nino.nino}/taxYear/${taxYear}/someService", EmptyJsonBody, "Authorization" -> s"Bearer backend-token")
        .returns(Future.successful(outcome))

      await(connector.doConnectorThing(request)) shouldBe outcome
    }
  }
}
