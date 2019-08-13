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

import v1.mocks.{MockAppConfig, MockHttpClient}
import v1.models.domain.selfAssessment.{CalculationListItem, CalculationType, ListCalculationsResponse}
import v1.models.errors.{BackendErrorCode, BackendErrors}
import v1.models.requestData.selfAssessment.ListCalculationsRequest

import scala.concurrent.Future

class IndividualCalculationsConnectorSpec extends ConnectorSpec {


  class Test extends MockHttpClient with MockAppConfig {
    MockedAppConfig.backendBaseUrl returns baseUrl
    MockedAppConfig.backendToken returns "individual-calculations-token"

    val connector: IndividualCalculationsConnector = new IndividualCalculationsConnector(mockAppConfig, mockHttpClient)
  }

  val test = Seq("test1", "test2")

  "listTaxCalculations" should {

    "return a successful response" when {

      "valid data is found with query parameters" in new Test {
        val request = ListCalculationsRequest(nino, Some("2019"))
        val expected = Right(ListCalculationsResponse(Seq(CalculationListItem("id", "timestamp", CalculationType.inYear, None))))

        MockedHttpClient.get(s"$baseUrl/individual/calculations/${request.nino.nino}/self-assessment", Seq(("taxYear", "2019")))
          .returns(Future.successful(expected))

        await(connector.listTaxCalculations(request)) shouldBe expected
      }

      "valid data is found without query parameters" in new Test {
        val request = ListCalculationsRequest(nino, None)
        val expected = Right(ListCalculationsResponse(Seq(CalculationListItem("id", "timestamp", CalculationType.inYear, None))))

        MockedHttpClient.get(s"$baseUrl/individual/calculations/${request.nino.nino}/self-assessment")
          .returns(Future.successful(expected))

        await(connector.listTaxCalculations(request)) shouldBe expected
      }
    }

    "return an backend error response" when {

      "an error response is returned from the backend" in new Test {
        val request = ListCalculationsRequest(nino, None)
        val expected = Left(BackendErrors.single(BackendErrorCode("BACKEND ERROR CODE")))

        MockedHttpClient.get(s"$baseUrl/individual/calculations/${request.nino.nino}/self-assessment")
          .returns(Future.successful(expected))

        await(connector.listTaxCalculations(request)) shouldBe expected
      }
    }

    "return an exception" when {

      "when an unexpected error is returned" in new Test {
        val request = ListCalculationsRequest(nino, None)

        MockedHttpClient.get(s"$baseUrl/individual/calculations/${request.nino.nino}/self-assessment")
          .returns(Future.failed(new Exception("unexpected exception")))

        the[Exception] thrownBy await(connector.listTaxCalculations(request)) should have message "unexpected exception"
      }
    }
  }
}
