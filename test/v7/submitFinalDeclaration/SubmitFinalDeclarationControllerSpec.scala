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

package v7.submitFinalDeclaration

import api.nrs.{MockNrsProxyConnector, StubNrsProxyService}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.Eventually
import play.api.Configuration
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import shared.config.MockAppConfig
import shared.controllers.{ControllerBaseSpec, ControllerTestRunner}
import shared.models.audit.GenericAuditDetailFixture.nino
import shared.models.audit.{AuditEvent, AuditResponse, GenericAuditDetail}
import shared.models.domain.{CalculationId, Nino, TaxYear}
import shared.models.errors.{ErrorWrapper, InternalError, NinoFormatError, RuleTaxYearNotSupportedError}
import shared.models.outcomes.ResponseWrapper
import shared.services.{MockAuditService, MockEnrolmentsAuthService, MockMtdIdLookupService}
import shared.utils.MockIdGenerator
import v7.common.model.domain.{CalculationType, `final-declaration`}
import v7.retrieveCalculation.MockRetrieveCalculationService
import v7.retrieveCalculation.def1.model.Def1_CalculationFixture
import v7.retrieveCalculation.models.request.Def1_RetrieveCalculationRequestData
import v7.submitFinalDeclaration.model.request.Def1_SubmitFinalDeclarationRequestData

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
    with MockSubmitFinalDeclarationValidatorFactory
    with MockAuditService
    with MockIdGenerator
    with MockNrsProxyConnector
    with MockRetrieveCalculationService
    with StubNrsProxyService
    with MockAppConfig
    with Def1_CalculationFixture {

  private val taxYear                  = "2020-21"
  private val calculationId            = "4557ecb5-fd32-48cc-81f5-e6acd1099f3c"
  val calculationType: CalculationType = `final-declaration`

  implicit override val patienceConfig: PatienceConfig =
    PatienceConfig(timeout = scaled(5.seconds), interval = scaled(25.milliseconds))

  override protected def beforeEach(): Unit = resetNrsProxyService()
  private val requestData = Def1_SubmitFinalDeclarationRequestData(Nino(nino), TaxYear.fromMtd(taxYear), CalculationId(calculationId), calculationType)
  private val retrieveDetailsRequestData = Def1_RetrieveCalculationRequestData(Nino(nino), TaxYear.fromMtd(taxYear), CalculationId(calculationId))

  private val retrieveDetailsResponseData = minimalCalculationR8bResponse

  "SubmitFinalDeclarationController" should {
    "return a successful response" when {
      "the request received is valid" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockSubmitFinalDeclarationService
          .submitFinalDeclaration(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, ()))))

        MockRetrieveCalculationService
          .retrieveCalculation(retrieveDetailsRequestData)
          .returns(Future.successful(Right(ResponseWrapper("correlationId", retrieveDetailsResponseData))))

        runOkTestWithAudit(expectedStatus = NO_CONTENT)

        eventually {
          verifyNrsProxyService(NrsProxyCall(validNino, "itsa-crystallisation", Json.toJson(retrieveDetailsResponseData)))
        }
      }

      "the request is valid but the Details lookup for NRS logging fails" in new Test {
        willUseValidator(returningSuccess(requestData))

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
          verifyNrsProxyService(NrsProxyCall(validNino, "itsa-crystallisation", fallbackNrsPayload))
        }
      }
    }

    "return the error as per spec" when {
      "the parser validation fails" in new Test {
        willUseValidator(returning(NinoFormatError))

        runErrorTestWithAudit(NinoFormatError)
      }

      "the service returns an error" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockSubmitFinalDeclarationService
          .submitFinalDeclaration(requestData)
          .returns(Future.successful(Left(ErrorWrapper(correlationId, RuleTaxYearNotSupportedError))))

        MockRetrieveCalculationService
          .retrieveCalculation(retrieveDetailsRequestData)
          .returns(Future.successful(Right(ResponseWrapper("correlationId", retrieveDetailsResponseData))))

        runErrorTestWithAudit(RuleTaxYearNotSupportedError)

        eventually {
          verifyNrsProxyService(NrsProxyCall(validNino, "itsa-crystallisation", Json.toJson(retrieveDetailsResponseData)))
        }
      }
    }

  }

  private trait Test extends ControllerTest with AuditEventChecking[GenericAuditDetail] {

    lazy val controller = new SubmitFinalDeclarationController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      validatorFactory = mockSubmitFinalDeclarationValidatorFactory,
      service = mockSubmitFinalDeclarationService,
      retrieveService = mockRetrieveCalculationService,
      cc = cc,
      nrsProxyService = stubNrsProxyService,
      auditService = mockAuditService,
      idGenerator = mockIdGenerator
    )

    MockedAppConfig.featureSwitchConfig.anyNumberOfTimes() returns Configuration(
      "supporting-agents-access-control.enabled" -> true
    )

    MockedAppConfig.endpointAllowsSupportingAgents(controller.endpointName).anyNumberOfTimes() returns false

    protected def callController(): Future[Result] =
      controller.submitFinalDeclaration(validNino, taxYear, calculationId, "final-declaration")(fakeRequest)

    override protected def event(auditResponse: AuditResponse, maybeRequestBody: Option[JsValue]): AuditEvent[GenericAuditDetail] =
      AuditEvent(
        "SubmitAFinalDeclaration",
        "submit-a-final-declaration",
        GenericAuditDetail(
          userType = "Individual",
          agentReferenceNumber = None,
          params = Map("nino" -> validNino, "taxYear" -> taxYear, "calculationId" -> calculationId),
          requestBody = None,
          `X-CorrelationId` = correlationId,
          versionNumber = apiVersion.name,
          auditResponse = auditResponse
        )
      )

  }

}
