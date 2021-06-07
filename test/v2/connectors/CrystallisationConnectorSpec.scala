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

package v2.connectors

import mocks.{MockAppConfig, MockHttpClient}
import uk.gov.hmrc.http.HeaderCarrier
import v2.models.domain.{DesTaxYear, EmptyJsonBody, Nino}
import v2.models.outcomes.ResponseWrapper
import v2.models.request.crystallisation.CrystallisationRequest
import v2.models.response.common.DesUnit

import scala.concurrent.Future

class CrystallisationConnectorSpec extends ConnectorSpec {

  val nino: String = "AA111111A"
  val taxYear: DesTaxYear = DesTaxYear.fromMtd(taxYear = "2021-22")
  val calculationId: String = "4557ecb5-fd32-48cc-81f5-e6acd1099f3c"

  val request: CrystallisationRequest = CrystallisationRequest(
    Nino(nino),
    taxYear,
    calculationId
  )

  class Test extends MockHttpClient with MockAppConfig {

    val connector: CrystallisationConnector = new CrystallisationConnector(
      http = mockHttpClient,
      appConfig = mockAppConfig
    )

    MockAppConfig.desBaseUrl returns baseUrl
    MockAppConfig.desToken returns "des-token"
    MockAppConfig.desEnvironment returns "des-environment"
    MockAppConfig.desEnvironmentHeaders returns Some(allowedDesHeaders)
  }

  "CrystallisationConnector" when {
    ".declareCrystallisation" should {
      "return a success upon HttpClient success" in new Test {
        val outcome = Right(ResponseWrapper(correlationId, DesUnit))

        implicit val hc: HeaderCarrier = HeaderCarrier(otherHeaders = otherHeaders ++ Seq("Content-Type" -> "application/json"))
        val requiredDesHeadersPost: Seq[(String, String)] = requiredDesHeaders ++ Seq("Content-Type" -> "application/json")

        MockedHttpClient
          .post(
            url = s"$baseUrl/income-tax/calculation/nino/$nino/${taxYear.value}/$calculationId/crystallise",
            config = dummyHeaderCarrierConfig,
            body = EmptyJsonBody,
            requiredHeaders = requiredDesHeadersPost,
            excludedHeaders = Seq("AnotherHeader" -> "HeaderValue")
          ).returns(Future.successful(outcome))

        await(connector.declareCrystallisation(request)) shouldBe outcome
      }
    }
  }
}