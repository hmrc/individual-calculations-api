/*
 * Copyright 2023 HM Revenue & Customs
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

import api.controllers.{ControllerBaseSpec, ControllerTestRunner}
import api.mocks.MockIdGenerator
import api.mocks.services.{MockAuditService, MockEnrolmentsAuthService, MockMtdIdLookupService}
import api.models.audit.{AuditEvent, AuditResponse, GenericAuditDetail}
import api.models.domain.{CalculationId, Nino, TaxYear}
import api.models.errors.{ErrorWrapper, InternalError, NinoFormatError, RuleTaxYearNotSupportedError}
import api.models.outcomes.ResponseWrapper
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.Eventually
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import v3.mocks.connectors.MockNrsProxyConnector
import v3.mocks.requestParsers.MockSubmitFinalDeclarationParser
import v3.mocks.services._
import v3.models.request._
import v3.models.response.retrieveCalculation.CalculationFixture
import v3.services.StubNrsProxyService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

class SubmitFinalDeclarationControllerSpec
    extends ControllerBaseSpec
    with Eventually
    with BeforeAndAfterEach
    with ControllerTestRunner
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockSubmitFinalDeclarationService
    with MockSubmitFinalDeclarationParser
    with MockRetrieveCalculationService
    with MockAuditService
    with MockNrsProxyConnector
    with StubNrsProxyService
    with MockIdGenerator
    with CalculationFixture {

  private val taxYear       = "2020-21"
  private val calculationId = "4557ecb5-fd32-48cc-81f5-e6acd1099f3c"

  implicit override val patienceConfig: PatienceConfig =
    PatienceConfig(timeout = scaled(5.seconds), interval = scaled(25.milliseconds))

  trait Test extends ControllerTest with AuditEventChecking {

    val controller = new SubmitFinalDeclarationController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      parser = mockSubmitFinalDeclarationParser,
      service = mockSubmitFinalDeclarationService,
      retrieveService = mockRetrieveCalculationService,
      cc = cc,
      nrsProxyService = stubNrsProxyService,
      auditService = mockAuditService,
      idGenerator = mockIdGenerator
    )

    protected def callController(): Future[Result] = controller.submitFinalDeclaration(nino, taxYear, calculationId)(fakeRequest)

    override protected def event(auditResponse: AuditResponse, maybeRequestBody: Option[JsValue]): AuditEvent[GenericAuditDetail] =
      AuditEvent(
        "SubmitAFinalDeclaration",
        "submit-a-final-declaration",
        GenericAuditDetail(
          userType = "Individual",
          agentReferenceNumber = None,
          params = Map("nino" -> nino, "taxYear" -> taxYear, "calculationId" -> calculationId),
          requestBody = None,
          `X-CorrelationId` = correlationId,
          versionNumber = "3.0",
          auditResponse = auditResponse
        )
      )

  }

  override protected def beforeEach(): Unit = resetNrsProxyService()

  private val rawData                    = SubmitFinalDeclarationRawData(nino, taxYear, calculationId)
  private val requestData                = SubmitFinalDeclarationRequest(Nino(nino), TaxYear.fromMtd(taxYear), CalculationId(calculationId))
  private val retrieveDetailsRequestData = RetrieveCalculationRequest(Nino(nino), TaxYear.fromMtd(taxYear), CalculationId(calculationId))

  private val retrieveDetailsResponseData = minimalCalculationResponse

  "SubmitFinalDeclarationController" should {
    "return a successful response" when {
      "the request received is valid" in new Test {
        MockSubmitFinalDeclarationParser
          .parseRequest(SubmitFinalDeclarationRawData(nino, taxYear, calculationId))
          .returns(Right(requestData))

        MockSubmitFinalDeclarationService
          .submitFinalDeclaration(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, ()))))

        MockRetrieveCalculationService
          .retrieveCalculation(retrieveDetailsRequestData)
          .returns(Future.successful(Right(ResponseWrapper("correlationId", retrieveDetailsResponseData))))

        runOkTestWithAudit(expectedStatus = NO_CONTENT)

        eventually {
          verifyNrsProxyService(NrsProxyCall(nino, "itsa-crystallisation", Json.toJson(retrieveDetailsResponseData)))
        }
      }

      "the request is valid but the Details lookup for NRS logging fails" in new Test {
        MockSubmitFinalDeclarationParser
          .parseRequest(SubmitFinalDeclarationRawData(nino, taxYear, calculationId))
          .returns(Right(requestData))

        MockSubmitFinalDeclarationService
          .submitFinalDeclaration(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, ()))))

        MockRetrieveCalculationService
          .retrieveCalculation(retrieveDetailsRequestData)
          .returns(Future.successful(Left(ErrorWrapper(correlationId, InternalError))))
          .anyNumberOfTimes()

        runOkTestWithAudit(expectedStatus = NO_CONTENT)

        private val fallbackNrsPayload = Json.parse(s"""
            |{
            |  "calculationId": "$calculationId"
            |}
            |""".stripMargin)

        eventually {
          verifyNrsProxyService(NrsProxyCall(nino, "itsa-crystallisation", fallbackNrsPayload))
        }
      }
    }

    "return the error as per spec" when {
      "the parser validation fails" in new Test {
        MockSubmitFinalDeclarationParser
          .parseRequest(rawData)
          .returns(Left(ErrorWrapper(correlationId, NinoFormatError, None)))

        runErrorTestWithAudit(NinoFormatError)
      }

      "the service returns an error" in new Test {
        MockSubmitFinalDeclarationParser
          .parseRequest(rawData)
          .returns(Right(requestData))

        MockSubmitFinalDeclarationService
          .submitFinalDeclaration(requestData)
          .returns(Future.successful(Left(ErrorWrapper(correlationId, RuleTaxYearNotSupportedError))))

        MockRetrieveCalculationService
          .retrieveCalculation(retrieveDetailsRequestData)
          .returns(Future.successful(Right(ResponseWrapper("correlationId", retrieveDetailsResponseData))))

        runErrorTestWithAudit(RuleTaxYearNotSupportedError)

        eventually {
          verifyNrsProxyService(NrsProxyCall(nino, "itsa-crystallisation", Json.toJson(retrieveDetailsResponseData)))
        }
      }
    }

  }

}
