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

package v3.connectors

import mocks.{MockAppConfig, MockHttpClient}
import uk.gov.hmrc.http.HeaderCarrier
import v3.models.domain.{EmptyJsonBody, Nino, TaxYear}
import v3.models.outcomes.ResponseWrapper
import v3.models.request.SubmitFinalDeclarationRequest

import scala.concurrent.Future


class SubmitFinalDeclarationConnectorSpec extends ConnectorSpec {

  val nino: String           = "AA111111A"
  val taxYear: TaxYear        =  TaxYear("2021-22")
  val calculationId: String  = "4557ecb5-fd32-48cc-81f5-e6acd1099f3c"

  val request: SubmitFinalDeclarationRequest = SubmitFinalDeclarationRequest(
    Nino(nino),
    taxYear,
    calculationId
  )

  class Test extends MockHttpClient with MockAppConfig {

    val connector: SubmitFinalDeclarationConnector = new SubmitFinalDeclarationConnector(
      http = mockHttpClient,
      appConfig = mockAppConfig
    )

    MockAppConfig.desBaseUrl returns baseUrl
    MockAppConfig.desToken returns "des-token"
    MockAppConfig.desEnvironment returns "des-environment"
    MockAppConfig.desEnvironmentHeaders returns Some(allowedDesHeaders)
  }

  "Submit Final Declaration" should {
    "return a success response" in new Test {
      val outcome = Right(ResponseWrapper(correlationId, {}))

      implicit val hc: HeaderCarrier = HeaderCarrier(otherHeaders = otherHeaders ++ Seq("Content-Type" -> "application/json"))
      val requiredDesHeadersPost: Seq[(String, String)] = requiredDesHeaders ++ Seq("Content-Type" -> "application/json")

      MockedHttpClient
        .post(
          url = s"$baseUrl/income-tax/calculation/nino/$nino/${taxYear.toDownstream}/$calculationId/crystallise",
          config = dummyDesHeaderCarrierConfig,
          body = EmptyJsonBody,
          requiredHeaders = requiredDesHeadersPost,
          excludedHeaders = Seq("AnotherHeader" -> "HeaderValue")
        )
        .returns(Future.successful(outcome))

      await(connector.submitFinalDeclaration(request)) shouldBe outcome
    }
  }
}
