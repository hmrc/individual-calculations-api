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

package v1.controllers

import play.api.libs.json.Json
import play.api.mvc.Result
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.http.HeaderCarrier
import utils.Logging
import v1.handling.RequestDefn
import v1.mocks.requestParsers.MockListCalculationsParser
import v1.mocks.services.{MockEnrolmentsAuthService, MockMtdIdLookupService, MockStandardService}
import v1.models.errors._
import v1.models.outcomes.ResponseWrapper
import v1.models.request.{ListCalculationsRawData, ListCalculationsRequest}
import v1.models.response.listCalculationsResponse.{CalculationListItem, ListCalculationsResponse}
import v1.models.response.{CalculationListItem, CalculationRequestor, CalculationType}
import v1.support.BackendResponseMappingSupport

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ListCalculationsControllerSpec
    extends ControllerBaseSpec
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockListCalculationsParser
    with MockStandardService {

  trait Test {
    val hc = HeaderCarrier()

    val controller = new ListCalculationsController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      listCalculationsParser = mockListCalculationsParser,
      service = mockStandardService,
      cc = cc
    )

    MockedMtdIdLookupService.lookup(nino).returns(Future.successful(Right("test-mtd-id")))
    MockedEnrolmentsAuthService.authoriseUser()
  }

  private val nino          = "AA123456A"
  private val taxYear       = Some("2017-18")
  private val correlationId = "X-123"

  val responseBody = Json.parse("""{
      |  "calculations": [
      |    {
      |      "id": "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
      |      "calculationTimestamp": "2019-03-17T09:22:59Z",
      |      "type": "inYear",
      |      "requestedBy": "hmrc"
      |    }
      |  ]
      |}""".stripMargin)

  val response = ListCalculationsResponse(
    Seq(
      CalculationListItem(
        id = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
        calculationTimestamp = "2019-03-17T09:22:59Z",
        `type` = CalculationType.inYear,
        requestedBy = Some(CalculationRequestor.hmrc)
      )
    ))

  private val rawData     = ListCalculationsRawData(nino, taxYear)
  private val requestData = ListCalculationsRequest(Nino(nino), taxYear)

  private def uri = "/input/uri"

  "handleRequest" should {
    "return OK with list of calculations" when {
      "happy path" in new Test {
        MockListCalculationsParser
          .parse(rawData)
          .returns(Right(requestData))

        MockStandardService
          .doService(RequestDefn.Get(uri).withOptionalParams("taxYear" -> taxYear), OK)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        val result: Future[Result] = controller.listCalculations(nino, taxYear)(fakeGetRequest(uri))

        status(result) shouldBe OK
        contentAsJson(result) shouldBe responseBody
        header("X-CorrelationId", result) shouldBe Some(correlationId)
      }
    }

    "return 404 NotFoundError" when {
      "an empty is is returned from the back end" in new Test {
        MockListCalculationsParser
          .parse(rawData)
          .returns(Right(requestData))

        MockStandardService
          .doService(RequestDefn.Get(uri).withOptionalParams("taxYear" -> taxYear), OK)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, ListCalculationsResponse(Nil)))))

        val result: Future[Result] = controller.listCalculations(nino, taxYear)(fakeGetRequest(uri))

        status(result) shouldBe NOT_FOUND
        contentAsJson(result) shouldBe Json.toJson(NotFoundError)
        header("X-CorrelationId", result) shouldBe Some(correlationId)
      }
    }

    "map service error mapping according to spec" in new Test with BackendResponseMappingSupport with Logging {
      MockListCalculationsParser
        .parse(rawData)
        .returns(Right(requestData))

      import controller.endpointLogContext

      val mappingChecks = allChecks[ListCalculationsResponse, ListCalculationsResponse](
        ("FORMAT_NINO", BAD_REQUEST, NinoFormatError, BAD_REQUEST),
        ("FORMAT_TAX_YEAR", BAD_REQUEST, TaxYearFormatError, BAD_REQUEST),
        ("RULE_TAX_YEAR_NOT_SUPPORTED", BAD_REQUEST, RuleTaxYearNotSupportedError, BAD_REQUEST),
        ("RULE_TAX_YEAR_RANGE_EXCEEDED", BAD_REQUEST, RuleTaxYearRangeExceededError, BAD_REQUEST),
        ("MATCHING_RESOURCE_NOT_FOUND", NOT_FOUND, NotFoundError, NOT_FOUND),
        ("INTERNAL_SERVER_ERROR", INTERNAL_SERVER_ERROR, DownstreamError, INTERNAL_SERVER_ERROR)
      )

      MockStandardService
        .doServiceWithMappings(mappingChecks)
        .returns(Future.successful(Right(ResponseWrapper(correlationId, ListCalculationsResponse(Nil)))))

      val result: Future[Result] = controller.listCalculations(nino, taxYear)(fakeGetRequest(uri))

      header("X-CorrelationId", result) shouldBe Some(correlationId)
    }
  }
}
