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

package v7.triggerCalculation

import play.api.Configuration
import play.api.libs.json._
import play.api.mvc.Result
import shared.controllers.{ControllerBaseSpec, ControllerTestRunner}
import shared.models.audit.GenericAuditDetailFixture.nino
import shared.models.audit.{AuditEvent, AuditResponse, GenericAuditDetail}
import shared.models.domain.{Nino, TaxYear}
import shared.models.errors.{ErrorWrapper, NinoFormatError, RuleTaxYearNotSupportedError}
import shared.models.outcomes.ResponseWrapper
import shared.services.{MockAuditService, MockEnrolmentsAuthService, MockMtdIdLookupService}
import shared.utils.MockIdGenerator
import v5.triggerCalculation.model.request.{Def1_TriggerCalculationRequestData, TriggerCalculationRequestData}
import v5.triggerCalculation.model.response.{Def1_TriggerCalculationResponse, TriggerCalculationResponse}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TriggerCalculationControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockTriggerCalculationService
    with MockTriggerCalculationValidatorFactory
    with MockAuditService
    with MockIdGenerator {

  private val taxYear    = TaxYear.fromMtd("2017-18")
  private val rawTaxYear = taxYear.asMtd

  val requestDataWithFinalDeclaration: TriggerCalculationRequestData =
    Def1_TriggerCalculationRequestData(Nino(nino), taxYear, finalDeclaration = true)

  val requestDataWithFinalDeclarationFalse: TriggerCalculationRequestData =
    Def1_TriggerCalculationRequestData(Nino(nino), taxYear, finalDeclaration = false)

  private val calculationId                        = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"
  private val response: TriggerCalculationResponse = Def1_TriggerCalculationResponse(calculationId)

  private val responseJson: JsValue = Json
    .parse(
      s"""
         |{
         |  "calculationId" : "$calculationId"
         |}
    """.stripMargin
    )

  private val mtdResponseJson: JsValue = responseJson.as[JsObject]

  "handleRequest" should {
    "return ACCEPTED with a calculationId" when {

      "happy path with final declaration" in new Test {
        willUseValidator(returningSuccess(requestDataWithFinalDeclaration))

        MockTriggerCalculationService
          .triggerCalculation(requestDataWithFinalDeclaration)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        runOkTestWithAudit(
          expectedStatus = ACCEPTED,
          maybeExpectedResponseBody = Some(mtdResponseJson),
          maybeAuditResponseBody = Some(responseJson)
        )
      }

      "happy path with final declaration undefined" in new Test {
        willUseValidator(returningSuccess(requestDataWithFinalDeclarationFalse))

        MockTriggerCalculationService
          .triggerCalculation(requestDataWithFinalDeclarationFalse)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        runOkTestWithAudit(
          expectedStatus = ACCEPTED,
          maybeExpectedResponseBody = Some(mtdResponseJson),
          maybeAuditResponseBody = Some(responseJson)
        )
      }
    }

    "return the error as per spec" when {
      "the parser validation fails" in new Test {
        willUseValidator(returning(NinoFormatError))

        runErrorTestWithAudit(NinoFormatError)
      }

      "the service returns an error" in new Test {
        willUseValidator(returningSuccess(requestDataWithFinalDeclarationFalse))

        MockTriggerCalculationService
          .triggerCalculation(requestDataWithFinalDeclarationFalse)
          .returns(Future.successful(Left(ErrorWrapper(correlationId, RuleTaxYearNotSupportedError))))

        runErrorTestWithAudit(RuleTaxYearNotSupportedError)
      }
    }

  }

  private trait Test extends ControllerTest with AuditEventChecking[GenericAuditDetail] {

    val finalDeclaration: Option[String] = None

    lazy val controller = new TriggerCalculationController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      validatorFactory = mockTriggerCalculationValidatorFactory,
      service = mockService,
      cc = cc,
      auditService = mockAuditService,
      idGenerator = mockIdGenerator
    )

    MockedAppConfig.featureSwitchConfig.anyNumberOfTimes() returns Configuration(
      "supporting-agents-access-control.enabled" -> true
    )

    MockedAppConfig.endpointAllowsSupportingAgents(controller.endpointName).anyNumberOfTimes() returns false

    protected def callController(): Future[Result] = controller.triggerCalculation(validNino, rawTaxYear, finalDeclaration)(fakeRequest)

    override protected def event(auditResponse: AuditResponse, maybeRequestBody: Option[JsValue]): AuditEvent[GenericAuditDetail] =
      AuditEvent(
        "TriggerASelfAssessmentTaxCalculation",
        "trigger-a-self-assessment-tax-calculation",
        GenericAuditDetail(
          userType = "Individual",
          agentReferenceNumber = None,
          params = Map("nino" -> validNino, "taxYear" -> rawTaxYear, "finalDeclaration" -> s"${finalDeclaration.getOrElse(false)}"),
          requestBody = None,
          `X-CorrelationId` = correlationId,
          versionNumber = apiVersion.name,
          auditResponse = auditResponse
        )
      )

  }

}
