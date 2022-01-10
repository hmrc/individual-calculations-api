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

package v2.controllers

import mocks.MockIdGenerator
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.{AnyContentAsJson, Result}
import uk.gov.hmrc.http.HeaderCarrier
import v2.mocks.requestParsers.MockCrystallisationRequestParser
import v2.mocks.services.{MockAuditService, MockCrystallisationService, MockEnrolmentsAuthService, MockMtdIdLookupService, MockNrsProxyService}
import v2.models.domain.Nino
import v2.models.audit.{AuditError, AuditEvent, AuditResponse, GenericAuditDetail}
import v2.models.domain.{CrystallisationRequestBody, DesTaxYear}
import v2.models.errors._
import v2.models.outcomes.ResponseWrapper
import v2.models.request.crystallisation.{CrystallisationRawData, CrystallisationRequest}
import v2.models.response.common.DesUnit

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CrystallisationControllerSpec
  extends ControllerBaseSpec
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockCrystallisationService
    with MockNrsProxyService
    with MockAuditService
    with MockCrystallisationRequestParser
    with MockIdGenerator {

  val nino: String = "AA123456A"
  val taxYear: String = "2019-20"
  val calculationId: String = "4557ecb5-fd32-48cc-81f5-e6acd1099f3c"
  val requestBody: JsObject = Json.obj("calculationId" -> calculationId)
  val correlationId: String = "a1e8057e-fbbc-47a8-a8b4-78d9f015c253"

  val crystallisationRequestBody: CrystallisationRequestBody = CrystallisationRequestBody(
    calculationId = calculationId
  )

  val rawData: CrystallisationRawData = CrystallisationRawData(
    nino = nino,
    taxYear = taxYear,
    body = AnyContentAsJson(requestBody)
  )

  val requestData: CrystallisationRequest = CrystallisationRequest(
    nino = Nino(nino),
    taxYear = DesTaxYear.fromMtd(taxYear),
    calculationId = calculationId
  )

  def event(auditResponse: AuditResponse): AuditEvent[GenericAuditDetail] =
    AuditEvent(
      auditType = "SubmitSelfAssessmentCrystallisationDeclaration",
      transactionName = "crystallisation",
      detail = GenericAuditDetail(
        userType = "Individual",
        agentReferenceNumber = None,
        pathParams = Map("nino" -> nino, "taxYear" -> taxYear),
        requestBody = Some(requestBody),
        `X-CorrelationId` = correlationId,
        auditResponse = auditResponse
      )
    )

  trait Test {
    val hc: HeaderCarrier = HeaderCarrier()

    val controller = new CrystallisationController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      requestParser = mockCrystallisationRequestParser,
      service = mockCrystallisationService,
      nrsProxyService = mockNrsProxyService,
      auditService = mockAuditService,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    MockedMtdIdLookupService.lookup(nino).returns(Future.successful(Right("test-mtd-id")))
    MockedEnrolmentsAuthService.authoriseUser()
    MockIdGenerator.getCorrelationId.returns(correlationId)
  }

  "CrystallisationController" should {
    "return NO_CONTENT" when {
      "happy path" in new Test {

        MockCrystallisationRequestParser
          .parse(rawData)
          .returns(Right(requestData))

        MockCrystallisationService
          .submitIntent(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, DesUnit))))

        MockNrsProxyService
          .submit(nino, crystallisationRequestBody)
          .returns(Future.successful((): Unit))

        val result: Future[Result] = controller.declareCrystallisation(nino, taxYear)(fakePostRequest(requestBody))

        status(result) shouldBe NO_CONTENT
        contentAsString(result) shouldBe ""
        header("X-CorrelationId", result) shouldBe Some(correlationId)

        val auditResponse: AuditResponse = AuditResponse(NO_CONTENT, None, None)
        MockedAuditService.verifyAuditEvent(event(auditResponse)).once
      }
    }

    "return the error as per spec" when {
      "parser errors occur" must {
        def errorsFromParserTester(error: MtdError, expectedStatus: Int): Unit = {
          s"a ${error.code} error is returned from the parser" in new Test {

            MockCrystallisationRequestParser
              .parse(rawData)
              .returns(Left(ErrorWrapper(correlationId, error, None, expectedStatus)))

            val result: Future[Result] = controller.declareCrystallisation(nino, taxYear)(fakePostRequest(requestBody))

            status(result) shouldBe expectedStatus
            contentAsJson(result) shouldBe Json.toJson(error)
            header("X-CorrelationId", result) shouldBe Some(correlationId)

            val auditResponse: AuditResponse = AuditResponse(expectedStatus, Some(Seq(AuditError(error.code))), None)
            MockedAuditService.verifyAuditEvent(event(auditResponse)).once
          }
        }

        val input = Seq(
          (BadRequestError, BAD_REQUEST),
          (NinoFormatError, BAD_REQUEST),
          (TaxYearFormatError, BAD_REQUEST),
          (RuleTaxYearRangeInvalidError, BAD_REQUEST),
          (RuleTaxYearNotSupportedError, BAD_REQUEST),
          (RuleIncorrectOrEmptyBodyError, BAD_REQUEST),
          (CalculationIdFormatError, BAD_REQUEST)
        )

        input.foreach(args => (errorsFromParserTester _).tupled(args))
      }

      "service errors occur" must {
        def serviceErrors(mtdError: MtdError, expectedStatus: Int): Unit = {
          s"a $mtdError error is returned from the service" in new Test {

            MockCrystallisationRequestParser
              .parse(rawData)
              .returns(Right(requestData))

            MockCrystallisationService
              .submitIntent(requestData)
              .returns(Future.successful(Left(ErrorWrapper(correlationId, mtdError, None, expectedStatus))))

            MockNrsProxyService
              .submit(nino, crystallisationRequestBody)
              .returns(Future.successful((): Unit))

            val result: Future[Result] = controller.declareCrystallisation(nino, taxYear)(fakePostRequest(requestBody))

            status(result) shouldBe expectedStatus
            contentAsJson(result) shouldBe Json.toJson(mtdError)
            header("X-CorrelationId", result) shouldBe Some(correlationId)

            val auditResponse: AuditResponse = AuditResponse(expectedStatus, Some(Seq(AuditError(mtdError.code))), None)
            MockedAuditService.verifyAuditEvent(event(auditResponse)).once
          }
        }

        val input = Seq(
          (NinoFormatError, BAD_REQUEST),
          (TaxYearFormatError, BAD_REQUEST),
          (CalculationIdFormatError, BAD_REQUEST),
          (NotFoundError, NOT_FOUND),
          (RuleIncomeSourcesChangedError, FORBIDDEN),
          (RuleRecentSubmissionsExistError, FORBIDDEN),
          (RuleResidencyChangedError, FORBIDDEN),
          (RuleIncomeSourcesInvalid, FORBIDDEN),
          (RuleNoIncomeSubmissionsExistError, FORBIDDEN),
          (RuleSubmissionFailed, FORBIDDEN),
          (RuleFinalDeclarationReceivedError, FORBIDDEN),
          (DownstreamError, INTERNAL_SERVER_ERROR)
        )

        input.foreach(args => (serviceErrors _).tupled(args))
      }
    }
  }
}