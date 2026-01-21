/*
 * Copyright 2026 HM Revenue & Customs
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

package v6.retrieveCalculation

import play.api.Configuration
import play.api.libs.json.JsValue
import play.api.mvc.Result
import shared.controllers.{ControllerBaseSpec, ControllerTestRunner}
import shared.models.audit.{AuditEvent, AuditResponse, GenericAuditDetail}
import shared.models.domain.{CalculationId, Nino, TaxYear}
import shared.models.errors.{ErrorWrapper, NinoFormatError, RuleTaxYearNotSupportedError}
import shared.models.outcomes.ResponseWrapper
import shared.services.{MockAuditService, MockEnrolmentsAuthService, MockMtdIdLookupService}
import shared.utils.MockIdGenerator
import v6.retrieveCalculation.def1.model.Def1_CalculationFixture
import v6.retrieveCalculation.models.request.{Def1_RetrieveCalculationRequestData, RetrieveCalculationRequestData}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RetrieveCalculationControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockRetrieveCalculationValidatorFactory
    with MockRetrieveCalculationService
    with MockAuditService
    with MockIdGenerator
    with Def1_CalculationFixture {

  private val calculationId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"

  "handleRequest" should {

    "return OK with the calculation" in new Test {
      willUseValidator(returningSuccess(requestData))

      MockRetrieveCalculationService
        .retrieveCalculation(requestData)
        .returns(Future.successful(Right(ResponseWrapper(correlationId, minimalCalculationResponse))))

      runOkTestWithAudit(
        expectedStatus = OK,
        maybeExpectedResponseBody = Some(minimumCalculationResponseR8BEnabledJson),
        maybeAuditResponseBody = Some(minimumCalculationResponseR8BEnabledJson)
      )
    }

    "return the error as per spec" when {
      "the parser validation fails" in new Test {
        willUseValidator(returning(NinoFormatError))
        runErrorTest(NinoFormatError)
      }

      "return RuleTaxYearNotSupportedError when tax year is outside supported range" in new Test {

        willUseValidator(returning(RuleTaxYearNotSupportedError))
        runErrorTest(RuleTaxYearNotSupportedError)
      }

      "return error when calculationId is invalid" in new Test {
        willUseValidator(
          returning(
            shared.models.errors.CalculationIdFormatError
          ))
        runErrorTest(shared.models.errors.CalculationIdFormatError)
      }

      "the service returns an error" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockRetrieveCalculationService
          .retrieveCalculation(requestData)
          .returns(Future.successful(Left(ErrorWrapper(correlationId, RuleTaxYearNotSupportedError))))

        runErrorTest(RuleTaxYearNotSupportedError)
      }
    }
  }

  trait Test extends ControllerTest with AuditEventChecking[GenericAuditDetail] {

    def taxYear: String = "2017-18"

    MockedAppConfig.featureSwitchConfig
      .returns(Configuration.empty)
      .anyNumberOfTimes()

    def requestData: RetrieveCalculationRequestData =
      Def1_RetrieveCalculationRequestData(Nino(validNino), TaxYear.fromMtd(taxYear), CalculationId(calculationId))

    val controller: RetrieveCalculationController = new RetrieveCalculationController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      validatorFactory = mockRetrieveCalculationValidatorFactory,
      service = mockRetrieveCalculationService,
      cc = cc,
      idGenerator = mockIdGenerator,
      auditService = mockAuditService
    )

    MockedAppConfig.endpointAllowsSupportingAgents(controller.endpointName).anyNumberOfTimes() returns false

    protected def callController(): Future[Result] =
      controller.retrieveCalculation(validNino, taxYear, calculationId)(fakeRequest)

    protected def event(auditResponse: AuditResponse, requestBody: Option[JsValue]): AuditEvent[GenericAuditDetail] =
      AuditEvent(
        auditType = "RetrieveATaxCalculation",
        transactionName = "retrieve-a-tax-calculation",
        detail = GenericAuditDetail(
          versionNumber = apiVersion.name,
          userType = "Individual",
          agentReferenceNumber = None,
          params = Map("nino" -> validNino, "calculationId" -> calculationId, "taxYear" -> taxYear),
          requestBody = requestBody,
          `X-CorrelationId` = correlationId,
          auditResponse = auditResponse
        )
      )

  }

}
