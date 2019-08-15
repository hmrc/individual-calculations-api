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

package v1.controllers.selfAssessment

import play.api.libs.json.Json
import play.api.mvc.Result
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.http.HeaderCarrier
import v1.controllers.ControllerBaseSpec
import v1.mocks.requestParsers.MockListCalculationsParser
import v1.mocks.services.{MockEnrolmentsAuthService, MockListCalculationsService, MockMtdIdLookupService}
import v1.models.domain.selfAssessment.{CalculationListItem, CalculationRequestor, CalculationType, ListCalculationsResponse}
import v1.models.errors._
import v1.models.outcomes.ResponseWrapper
import v1.models.requestData.selfAssessment.{ListCalculationsRawData, ListCalculationsRequest}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ListCalculationsControllerSpec
    extends ControllerBaseSpec
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockListCalculationsParser
    with MockListCalculationsService {

  trait Test {
    val hc = HeaderCarrier()

    val controller = new ListCalculationsController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      listCalculationsParser = mockListCalculationsParser,
      listCalculationsService = mockListCalculationsService,
      cc = cc
    )

    MockedMtdIdLookupService.lookup(nino).returns(Future.successful(Right("test-mtd-id")))
    MockedEnrolmentsAuthService.authoriseUser()
  }

  private val nino          = "AA123456A"
  private val taxYear       =  Some("2017-18")
  private val correlationId = "X-123"

  val responseBody = Json.parse(
    """{
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

  "handleRequest" should {
    "return OK with list of calculations" when {
      "happy path" in new Test {
        MockListCalculationsParser
          .parse(rawData)
          .returns(Right(requestData))

        MockListCalculationsService
          .listCalculations(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        val result: Future[Result] = controller.listCalculations(nino, taxYear)(fakeGetRequest)

        status(result) shouldBe OK
        contentAsJson(result) shouldBe responseBody
        header("X-CorrelationId", result) shouldBe Some(correlationId)
      }
    }

    "return 404 NotFoundError" when {
      "an empty is is returned from the back end" in new Test{
        MockListCalculationsParser
          .parse(rawData)
          .returns(Right(requestData))

        MockListCalculationsService
          .listCalculations(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, ListCalculationsResponse(Nil)))))

        val result: Future[Result] = controller.listCalculations(nino, taxYear)(fakeGetRequest)

        status(result) shouldBe NOT_FOUND
        contentAsJson(result) shouldBe Json.toJson(NotFoundError)
        header("X-CorrelationId", result) shouldBe Some(correlationId)
      }
    }

    "return the error as per spec" when {
      "parser errors occur" must {
        def errorsFromParserTester(error: MtdError, expectedStatus: Int): Unit = {
          s"a ${error.code} error is returned from the parser" in new Test {

            MockListCalculationsParser
              .parse(rawData)
              .returns(Left(ErrorWrapper(Some(correlationId), MtdErrors(BAD_REQUEST, error))))

            val result: Future[Result] = controller.listCalculations(nino, taxYear)(fakeGetRequest)

            status(result) shouldBe expectedStatus
            contentAsJson(result) shouldBe Json.toJson(error)
            header("X-CorrelationId", result) shouldBe Some(correlationId)
          }
        }

        val input = Seq(
          (BadRequestError, BAD_REQUEST),
          (NinoFormatError, BAD_REQUEST),
          (TaxYearFormatError, BAD_REQUEST),
          (RuleTaxYearNotSupportedError, BAD_REQUEST),
          (RuleTaxYearRangeExceededError, BAD_REQUEST),
          (DownstreamError, INTERNAL_SERVER_ERROR)
        )

        input.foreach(args => (errorsFromParserTester _).tupled(args))
      }

      "service errors occur" must {
        def serviceErrors(mtdError: MtdError, expectedStatus: Int): Unit = {
          s"a $mtdError error is returned from the service" in new Test {

            MockListCalculationsParser
              .parse(rawData)
              .returns(Right(requestData))

            MockListCalculationsService
              .listCalculations(requestData)
              .returns(Future.successful(Left(ErrorWrapper(Some(correlationId), MtdErrors(???,  mtdError)))))

            val result: Future[Result] = controller.listCalculations(nino, taxYear)(fakeGetRequest)

            status(result) shouldBe expectedStatus
            contentAsJson(result) shouldBe Json.toJson(mtdError)
            header("X-CorrelationId", result) shouldBe Some(correlationId)
          }
        }

        val input = Seq(
          (BadRequestError, BAD_REQUEST),
          (NinoFormatError, BAD_REQUEST),
          (TaxYearFormatError, BAD_REQUEST),
          (RuleTaxYearNotSupportedError, BAD_REQUEST),
          (RuleTaxYearRangeExceededError, BAD_REQUEST),
          (DownstreamError, INTERNAL_SERVER_ERROR)
        )

        input.foreach(args => (serviceErrors _).tupled(args))
      }
    }
  }
}
