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

package v3.controllers

import mocks.MockIdGenerator
import play.api.libs.json.Json
import play.api.mvc.Result
import uk.gov.hmrc.http.HeaderCarrier
import v3.mocks.requestParsers.MockSubmitFinalDeclarationParser
import v3.mocks.services._
import v3.models.audit.{AuditEvent, GenericAuditDetail}
import v3.models.domain.{Nino, TaxYear}
import v3.models.errors._
import v3.models.outcomes.ResponseWrapper
import v3.models.request._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SubmitFinalDeclarationControllerSpec
    extends ControllerBaseSpec
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockSubmitFinalDeclarationService
    with MockSubmitFinalDeclarationParser
    with MockAuditService
    with MockIdGenerator {

  private val nino          = "AA123456A"
  private val taxYear       = "2020-21"
  private val calculationId = "4557ecb5-fd32-48cc-81f5-e6acd1099f3c"
  private val correlationId = "a1e8057e-fbbc-47a8-a8b4-78d9f015c253"

  private def auditError(responseStatus: Int, error: MtdError): AuditEvent[GenericAuditDetail] = {
    val auditValues   = Map("nino" -> nino, "taxYear" -> taxYear, "calculationId" -> calculationId)
    val detail        = GenericAuditDetail("Individual", None, auditValues, None, correlationId,
      versionNumber = "3.0", response = "error", httpStatusCode = responseStatus, calculationId = None,
      errorCodes = Some(Seq(error.code)))

    AuditEvent("SubmitAFinalDeclaration", "Submit-A-Final-Declaration", detail)
  }

  trait Test {
    val hc: HeaderCarrier = HeaderCarrier()

    val controller = new SubmitFinalDeclarationController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      parser = mockSubmitFinalDeclarationParser,
      service = mockSubmitFinalDeclarationService,
      cc = cc,
      auditService = mockAuditService,
      idGenerator = mockIdGenerator
    )

    MockedMtdIdLookupService.lookup(nino).returns(Future.successful(Right("test-mtd-id")))
    MockedEnrolmentsAuthService.authoriseUser()
    MockIdGenerator.getCorrelationId.returns(correlationId)
  }

  private val rawData     = SubmitFinalDeclarationRawData(nino, taxYear, calculationId)
  private val requestData = SubmitFinalDeclarationRequest(Nino(nino), TaxYear.fromMtd(taxYear), calculationId)

  "submit final declaration" should {
    "return a successful response from a consolidated request" when {
      "the request received is valid" in new Test {

        MockSubmitFinalDeclarationParser
          .parse(SubmitFinalDeclarationRawData(nino, taxYear, calculationId))
          .returns(Right(requestData))

        MockSubmitFinalDeclarationService
          .submitIntent(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, ()))))

        val result: Future[Result] =
          controller.submitFinalDeclaration(nino, taxYear, calculationId)(fakeRequest)
        status(result) shouldBe NO_CONTENT
        header("X-CorrelationId", result) shouldBe Some(correlationId)

        val auditValues = Map("nino" -> nino, "taxYear" -> taxYear, "calculationId" -> calculationId)
        val detail: GenericAuditDetail = GenericAuditDetail("Individual", None, auditValues, None, correlationId,
          versionNumber = "3.0", response = "success", httpStatusCode = 204, calculationId = None,
          errorCodes = None)

        val event: AuditEvent[GenericAuditDetail] =
          AuditEvent("SubmitAFinalDeclaration", "Submit-A-Final-Declaration", detail)
        MockedAuditService.verifyAuditEvent(event).once
      }
    }

    "return the error as per spec" when {
      "parser errors occur" should {
        def errorsFromParserTester(error: MtdError, expectedStatus: Int): Unit = {
          s"a ${error.code} error is returned from the parser" in new Test {

            MockSubmitFinalDeclarationParser
              .parse(rawData)
              .returns(Left(ErrorWrapper(correlationId, error, None)))

            val result: Future[Result] =
              controller.submitFinalDeclaration(nino, taxYear, calculationId)(fakeRequest)

            status(result) shouldBe expectedStatus
            contentAsJson(result) shouldBe Json.toJson(error)
            header("X-CorrelationId", result) shouldBe Some(correlationId)

            MockedAuditService.verifyAuditEvent(auditError(expectedStatus, error)).once
          }
        }

        val input = Seq(
          (BadRequestError, BAD_REQUEST),
          (NinoFormatError, BAD_REQUEST),
          (TaxYearFormatError, BAD_REQUEST),
          (RuleTaxYearRangeInvalidError, BAD_REQUEST),
          (RuleTaxYearNotSupportedError, BAD_REQUEST),
          (CalculationIdFormatError, BAD_REQUEST)
        )

        input.foreach(args => (errorsFromParserTester _).tupled(args))
      }

      "service errors occur" should {
        def serviceErrors(mtdError: MtdError, expectedStatus: Int): Unit = {
          s"a $mtdError error is returned from the service" in new Test {

            MockSubmitFinalDeclarationParser
              .parse(rawData)
              .returns(Right(requestData))

            MockSubmitFinalDeclarationService
              .submitIntent(requestData)
              .returns(Future.successful(Left(ErrorWrapper(correlationId, mtdError))))

            val result: Future[Result] = controller.submitFinalDeclaration(nino, taxYear, calculationId)(fakeRequest)

            status(result) shouldBe expectedStatus
            contentAsJson(result) shouldBe Json.toJson(mtdError)
            header("X-CorrelationId", result) shouldBe Some(correlationId)

            MockedAuditService.verifyAuditEvent(auditError(expectedStatus, mtdError)).once
          }
        }

        val input = Seq(
          (NinoFormatError, BAD_REQUEST),
          (TaxYearFormatError, BAD_REQUEST),
          (CalculationIdFormatError, BAD_REQUEST),
          (RuleIncomeSourcesChangedError, FORBIDDEN),
          (RuleRecentSubmissionsExistError, FORBIDDEN),
          (RuleResidencyChangedError, FORBIDDEN),
          (RuleFinalDeclarationReceivedError, FORBIDDEN),
          (RuleIncomeSourcesInvalidError, FORBIDDEN),
          (RuleNoIncomeSubmissionsExistError, FORBIDDEN),
          (NotFoundError, NOT_FOUND),
          (DownstreamError, INTERNAL_SERVER_ERROR),
          (RuleIncorrectGovTestScenarioError, BAD_REQUEST)
        )

        input.foreach(args => (serviceErrors _).tupled(args))
      }
    }

    "return a DownstreamError" when {
      object TestError
          extends MtdError(
            code = "TEST_ERROR",
            message = "This is a test error"
          )
      "the parser returns an unexpected error" in new Test {
        MockSubmitFinalDeclarationParser
          .parse(rawData)
          .returns(Left(ErrorWrapper(correlationId, TestError, None)))

        val result: Future[Result] = controller.submitFinalDeclaration(nino, taxYear, calculationId)(fakeRequest)

        status(result) shouldBe INTERNAL_SERVER_ERROR
        contentAsJson(result) shouldBe Json.toJson(DownstreamError)
        header("X-CorrelationId", result) shouldBe Some(correlationId)
      }

      "the service returns an unexpected error" in new Test {
        MockSubmitFinalDeclarationParser
          .parse(rawData)
          .returns(Right(requestData))

        MockSubmitFinalDeclarationService
          .submitIntent(requestData)
          .returns(Future.successful(Left(ErrorWrapper(correlationId, TestError))))

        val result: Future[Result] = controller.submitFinalDeclaration(nino, taxYear, calculationId)(fakeRequest)

        status(result) shouldBe INTERNAL_SERVER_ERROR
        contentAsJson(result) shouldBe Json.toJson(DownstreamError)
        header("X-CorrelationId", result) shouldBe Some(correlationId)
      }
    }
  }

}
