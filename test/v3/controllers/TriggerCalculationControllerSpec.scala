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
import play.api.libs.json._
import play.api.mvc.Result
import uk.gov.hmrc.http.HeaderCarrier
import v3.mocks.hateoas.MockHateoasFactory
import v3.mocks.requestParsers.MockTriggerCalculationParser
import v3.mocks.services.{MockAuditService, MockEnrolmentsAuthService, MockMtdIdLookupService, MockTriggerCalculationService}
import v3.models.audit.{AuditEvent, GenericAuditDetail}
import v3.models.domain.{Nino, TaxYear}
import v3.models.errors._
import v3.models.hateoas.Method.GET
import v3.models.hateoas.{HateoasWrapper, Link}
import v3.models.outcomes.ResponseWrapper
import v3.models.request.{TriggerCalculationRawData, TriggerCalculationRequest}
import v3.models.response.triggerCalculation.{TriggerCalculationHateoasData, TriggerCalculationResponse}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TriggerCalculationControllerSpec
    extends ControllerBaseSpec
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockTriggerCalculationService
    with MockTriggerCalculationParser
    with MockHateoasFactory
    with MockAuditService
    with MockIdGenerator {

  private val nino                = "AA123456A"
  private val taxYear             = TaxYear.fromMtd("2017-18")
  private val rawTaxYear          = taxYear.asMtd
  private val rawFinalDeclaration = Some("true")
  private val calculationId       = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"
  private val correlationId       = "X-123"

  val response: TriggerCalculationResponse = TriggerCalculationResponse(calculationId)

  val json: JsValue = Json.parse(
    s"""
      |{
      |  "calculationId" : "$calculationId",
      |  "links" : [
      |      {
      |      "href":"/foo/bar",
      |      "method":"GET",
      |      "rel":"test-relationship"
      |      }
      |   ]
      |}
    """.stripMargin
  )

  val rawDataWithFinalDeclaration: TriggerCalculationRawData          = TriggerCalculationRawData(nino, rawTaxYear, finalDeclaration = Some("true"))
  val rawDataWithFinalDeclarationFalse: TriggerCalculationRawData     = TriggerCalculationRawData(nino, rawTaxYear, finalDeclaration = Some("false"))
  val rawDataWithFinalDeclarationUndefined: TriggerCalculationRawData = TriggerCalculationRawData(nino, rawTaxYear, None)

  val requestDataWithFinalDeclaration: TriggerCalculationRequest      = TriggerCalculationRequest(Nino(nino), taxYear, finalDeclaration = true)
  val requestDataWithFinalDeclarationFalse: TriggerCalculationRequest = TriggerCalculationRequest(Nino(nino), taxYear, finalDeclaration = false)

  val testHateoasLink: Link = Link(href = "/foo/bar", method = GET, rel = "test-relationship")

  private def uri = s"/$nino/self-assessment/$rawTaxYear/"

  private def auditError(responseStatus: Int, error: MtdError): AuditEvent[GenericAuditDetail] = {
    val auditValues = Map("nino" -> nino, "taxYear" -> rawTaxYear, "finalDeclaration" -> "true")
    val detail = GenericAuditDetail(
      "Individual",
      None,
      auditValues,
      None,
      correlationId,
      versionNumber = "3.0",
      response = "error",
      httpStatusCode = responseStatus,
      calculationId = None,
      errorCodes = Some(Seq(error.code))
    )

    AuditEvent("TriggerASelfAssessmentTaxCalculation", "trigger-a-self-assessment-tax-calculation", detail)
  }

  trait Test {
    val hc: HeaderCarrier = HeaderCarrier()

    val controller = new TriggerCalculationController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      parser = mockTriggerCalculationParser,
      service = mockService,
      cc = cc,
      auditService = mockAuditService,
      hateoasFactory = mockHateoasFactory,
      idGenerator = mockIdGenerator
    )

    MockedMtdIdLookupService.lookup(nino).returns(Future.successful(Right("test-mtd-id")))
    MockedEnrolmentsAuthService.authoriseUser()
    MockIdGenerator.getCorrelationId.returns(correlationId)
  }

  "handleRequest" should {
    "return ACCEPTED with list of calculations" when {

      def happyPath(rawData: TriggerCalculationRawData, requestData: TriggerCalculationRequest, controller: TriggerCalculationController): Unit = {

        MockTriggerCalculationParser
          .parse(rawData)
          .returns(Right(requestData))

        MockTriggerCalculationService
          .triggerCalculation(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        MockHateoasFactory
          .wrap(response, TriggerCalculationHateoasData(nino, TaxYear.fromMtd(rawTaxYear), finalDeclaration = requestData.finalDeclaration, calculationId))
          .returns(HateoasWrapper(response, Seq(testHateoasLink)))

        val result: Future[Result] =
          controller.triggerCalculation(nino, rawTaxYear, rawData.finalDeclaration)(fakePostRequest(uri))

        status(result) shouldBe ACCEPTED
        contentAsJson(result) shouldBe json
        header("X-CorrelationId", result) shouldBe Some(correlationId)

        val auditValues = Map("nino" -> nino, "taxYear" -> rawTaxYear, "finalDeclaration" -> s"${rawData.finalDeclaration.getOrElse(false)}")
        val detail: GenericAuditDetail = GenericAuditDetail(
          "Individual",
          None,
          auditValues,
          None,
          correlationId,
          versionNumber = "3.0",
          response = "success",
          httpStatusCode = 202,
          calculationId = Some(calculationId),
          errorCodes = None
        )

        val event: AuditEvent[GenericAuditDetail] =
          AuditEvent("TriggerASelfAssessmentTaxCalculation", "trigger-a-self-assessment-tax-calculation", detail)
        MockedAuditService.verifyAuditEvent(event).once
      }

      "happy path with final declaration" in new Test {
        happyPath(rawDataWithFinalDeclaration, requestDataWithFinalDeclaration, controller)
      }

      "happy path with final declaration as false" in new Test {
        happyPath(rawDataWithFinalDeclarationFalse, requestDataWithFinalDeclarationFalse, controller)
      }

      "happy path with final declaration undefined" in new Test {
        happyPath(rawDataWithFinalDeclarationUndefined, requestDataWithFinalDeclarationFalse, controller)
      }
    }
  }

  "return the error as per spec" when {
    "parser errors occur" should {
      def errorsFromParserTester(error: MtdError, expectedStatus: Int): Unit = {
        s"the parser returns error ${error.code}" in new Test {

          MockTriggerCalculationParser
            .parse(rawDataWithFinalDeclaration)
            .returns(Left(ErrorWrapper(correlationId, error, None)))

          val result: Future[Result] =
            controller.triggerCalculation(nino, rawTaxYear, rawFinalDeclaration)(fakePostRequest(uri))

          status(result) shouldBe expectedStatus
          contentAsJson(result) shouldBe Json.toJson(error)
          header("X-CorrelationId", result) shouldBe Some(correlationId)

          MockedAuditService.verifyAuditEvent(auditError(expectedStatus, error)).once
        }
      }

      val input = Seq(
        (NinoFormatError, BAD_REQUEST),
        (TaxYearFormatError, BAD_REQUEST),
        (FinalDeclarationFormatError, BAD_REQUEST),
        (RuleTaxYearNotSupportedError, BAD_REQUEST),
        (RuleTaxYearRangeInvalidError, BAD_REQUEST)
      )

      input.foreach(args => (errorsFromParserTester _).tupled(args))
    }

    "service errors occur" should {
      def serviceErrors(error: MtdError, expectedStatus: Int): Unit = {
        s"the service returns error ${error.code}" in new Test {

          MockTriggerCalculationParser
            .parse(rawDataWithFinalDeclaration)
            .returns(Right(requestDataWithFinalDeclaration))

          MockTriggerCalculationService
            .triggerCalculation(requestDataWithFinalDeclaration)
            .returns(Future.successful(Left(ErrorWrapper(correlationId, error))))

          val result: Future[Result] = controller.triggerCalculation(nino, rawTaxYear, rawFinalDeclaration)(fakePostRequest(uri))

          status(result) shouldBe expectedStatus
          contentAsJson(result) shouldBe Json.toJson(error)
          header("X-CorrelationId", result) shouldBe Some(correlationId)

          MockedAuditService.verifyAuditEvent(auditError(expectedStatus, error)).once
        }
      }

      val errors = List(
        (NinoFormatError, BAD_REQUEST),
        (TaxYearFormatError, BAD_REQUEST),
        (RuleNoIncomeSubmissionsExistError, FORBIDDEN),
        (RuleFinalDeclarationReceivedError, FORBIDDEN),
        (BadRequestError, BAD_REQUEST),
        (InternalError, INTERNAL_SERVER_ERROR),
        (RuleIncorrectGovTestScenarioError, BAD_REQUEST)
      )

      val extraTysErrors = List(
        (RuleIncomeSourcesChangedError, BAD_REQUEST),
        (RuleResidencyChangedError, BAD_REQUEST),
        (RuleTaxYearNotEndedError, BAD_REQUEST),
        (RuleRecentSubmissionsExistError, BAD_REQUEST),
        (RuleCalculationInProgressError, BAD_REQUEST),
        (RuleBusinessValidationFailureError, BAD_REQUEST)
      )

      (errors ++ extraTysErrors).foreach(args => (serviceErrors _).tupled(args))
    }
  }

}
