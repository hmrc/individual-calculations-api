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
import v1.mocks.requestParsers.MockSampleRequestDataParser
import v1.mocks.services.{MockAuditService, MockEnrolmentsAuthService, MockMtdIdLookupService, MockSampleService}
import v1.models.audit.{AuditError, AuditEvent, SampleAuditDetail, SampleAuditResponse}
import v1.models.domain.{SampleRequestBody, SampleResponse}
import v1.models.errors._
import v1.models.outcomes.ResponseWrapper
import v1.models.requestData.{DesTaxYear, SampleRawData, SampleRequestData}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SampleControllerSpec
    extends ControllerBaseSpec
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockSampleRequestDataParser
    with MockSampleService
    with MockAuditService {

  trait Test {
    val hc = HeaderCarrier()

    val controller = new SampleController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      requestDataParser = mockRequestDataParser,
      sampleService = mockSampleService,
      auditService = mockAuditService,
      cc = cc
    )

    MockedMtdIdLookupService.lookup(nino).returns(Future.successful(Right("test-mtd-id")))
    MockedEnrolmentsAuthService.authoriseUser()
  }

  private val nino          = "AA123456A"
  private val taxYear       = "2017-18"
  private val correlationId = "X-123"

  private val requestBodyJson = Json.parse("""{
      |  "data" : "someData"
      |}
    """.stripMargin)

  private val responseBody = Json.parse("""{
      |  "responseData" : "result"
      |}
    """.stripMargin)

  private val requestBody = SampleRequestBody("someData")

  private val rawData     = SampleRawData(nino, taxYear, requestBodyJson)
  private val requestData = SampleRequestData(Nino(nino), DesTaxYear.fromMtd(taxYear), requestBody)

  "handleRequest" should {
    "return CREATED" when {
      "happy path" in new Test {

        MockSampleRequestDataParser
          .parse(rawData)
          .returns(Right(requestData))

        MockSampleService
          .doServiceThing(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, SampleResponse("result")))))

        val result: Future[Result] = controller.handleRequest(nino, taxYear)(fakePostRequest(requestBodyJson))

        status(result) shouldBe CREATED
        contentAsJson(result) shouldBe responseBody
        header("X-CorrelationId", result) shouldBe Some(correlationId)

        val detail = SampleAuditDetail("Individual", None, nino, taxYear, correlationId, SampleAuditResponse(CREATED, None))
        val event: AuditEvent[SampleAuditDetail] =
          AuditEvent[SampleAuditDetail]("sampleAuditType", "sample-transaction-type", detail)
        MockedAuditService.verifyAuditEvent(event).once
      }
    }

    "return the error as per spec" when {
      "parser errors occur" must {
        def errorsFromParserTester(error: MtdError, expectedStatus: Int): Unit = {
          s"a ${error.code} error is returned from the parser" in new Test {

            MockSampleRequestDataParser
              .parse(rawData)
              .returns(Left(ErrorWrapper(Some(correlationId), error, None)))

            val result: Future[Result] = controller.handleRequest(nino, taxYear)(fakePostRequest(requestBodyJson))

            status(result) shouldBe expectedStatus
            contentAsJson(result) shouldBe Json.toJson(error)
            header("X-CorrelationId", result) shouldBe Some(correlationId)

            val detail = SampleAuditDetail(
              "Individual",
              None,
              nino,
              taxYear,
              header("X-CorrelationId", result).get,
              SampleAuditResponse(expectedStatus, Some(Seq(AuditError(error.code))))
            )
            val event: AuditEvent[SampleAuditDetail] =
              AuditEvent[SampleAuditDetail]("sampleAuditType", "sample-transaction-type", detail)
            MockedAuditService.verifyAuditEvent(event).once
          }
        }

        val input = Seq(
          (BadRequestError, BAD_REQUEST),
          (NinoFormatError, BAD_REQUEST),
          (TaxYearFormatError, BAD_REQUEST),
          (RuleTaxYearNotSupportedError, BAD_REQUEST),
          (RuleTaxYearRangeExceededError, BAD_REQUEST),
          (RuleIncorrectOrEmptyBodyError, BAD_REQUEST),
          (DownstreamError, INTERNAL_SERVER_ERROR)
        )

        input.foreach(args => (errorsFromParserTester _).tupled(args))
      }

      "service errors occur" must {
        def serviceErrors(mtdError: MtdError, expectedStatus: Int): Unit = {
          s"a $mtdError error is returned from the service" in new Test {

            MockSampleRequestDataParser
              .parse(rawData)
              .returns(Right(requestData))

            MockSampleService
              .doServiceThing(requestData)
              .returns(Future.successful(Left(ErrorWrapper(Some(correlationId), mtdError))))

            val result: Future[Result] = controller.handleRequest(nino, taxYear)(fakePostRequest(requestBodyJson))

            status(result) shouldBe expectedStatus
            contentAsJson(result) shouldBe Json.toJson(mtdError)
            header("X-CorrelationId", result) shouldBe Some(correlationId)

            val detail = SampleAuditDetail(
              "Individual",
              None,
              nino,
              taxYear,
              header("X-CorrelationId", result).get,
              SampleAuditResponse(expectedStatus, Some(Seq(AuditError(mtdError.code))))
            )
            val event: AuditEvent[SampleAuditDetail] =
              AuditEvent[SampleAuditDetail]("sampleAuditType", "sample-transaction-type", detail)
            MockedAuditService.verifyAuditEvent(event).once
          }
        }

        val input = Seq(
          (NinoFormatError, BAD_REQUEST),
          (TaxYearFormatError, BAD_REQUEST),
          (NotFoundError, NOT_FOUND),
          (DownstreamError, INTERNAL_SERVER_ERROR)
        )

        input.foreach(args => (serviceErrors _).tupled(args))
      }
    }
  }
}
