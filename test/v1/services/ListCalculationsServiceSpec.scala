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

package v1.services

import uk.gov.hmrc.http.HeaderCarrier
import v1.connectors.{BackendOutcome, IndividualCalculationsConnector}
import v1.models.domain.selfAssessment.{CalculationListItem, CalculationType, ListCalculationsResponse}
import v1.models.errors._
import v1.models.outcomes.ResponseWrapper
import v1.models.requestData.selfAssessment.ListCalculationsRequest

import scala.concurrent.{ExecutionContext, Future}

class ListCalculationsServiceSpec extends ServiceSpec {

  class Test(connectorResponse: Future[BackendOutcome[ListCalculationsResponse]], expectedRequest: ListCalculationsRequest) {
    val mockConnector: IndividualCalculationsConnector = mock[IndividualCalculationsConnector]

    (mockConnector.listTaxCalculations(_: ListCalculationsRequest)(_: HeaderCarrier, _: ExecutionContext)).expects(where { (request, _, _) =>
      request == expectedRequest
    }).returns(connectorResponse)

    val service = new ListCalculationsService(mockConnector)
  }

  "List Calculations Service" should {

    "return valid data" when {
      val request = ListCalculationsRequest(nino, Some("2019"))
      val expected = Right(ResponseWrapper("correlationId",
        ListCalculationsResponse(Seq(CalculationListItem("id", "timestamp", CalculationType.inYear, None)))))

      "provided with a successful response from the connector" in new Test(Future.successful(expected), request) {
        await(service.listCalculations(request)) shouldBe expected
      }
    }

    "return a provided error response" when {
      val errorMap = Map(
        "MATCHING_RESOURCE_NOT_FOUND" -> NotFoundError,
        "INTERNAL_SERVER_ERROR" -> DownstreamError,
        "FORMAT_TAX_YEAR" -> TaxYearFormatError,
        "FORMAT_NINO" -> NinoFormatError
      )

      errorMap.foreach { error =>
        val request = ListCalculationsRequest(nino, Some("2019"))
        val expected = Left(ResponseWrapper("correlationId", BackendErrors.single(BackendErrorCode(error._1))))

        s"provided with an expected error with code ${error._1}" in new Test(Future.successful(expected), request) {
          await(service.listCalculations(request)) shouldBe Left(ErrorWrapper(Some("correlationId"), error._2, None))
        }
      }
    }

    "return a converted error response" when {
      val request = ListCalculationsRequest(nino, Some("2019"))
      val expected = Left(ResponseWrapper("correlationId", BackendErrors.single(BackendErrorCode("NON MATCHING CODE"))))

      "provided with an unexpected error from the backend" in new Test(Future.successful(expected), request) {
        await(service.listCalculations(request)) shouldBe Left(ErrorWrapper(Some("correlationId"), DownstreamError, None))
      }
    }
  }
}
