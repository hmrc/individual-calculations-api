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

package v4.controllers

import shared.controllers.{ControllerBaseSpec, ControllerTestRunner}
import shared.utils.MockIdGenerator
import shared.models.audit.{AuditEvent, AuditResponse, GenericAuditDetail}
import shared.models.domain.{CalculationId, Nino, TaxYear}
import shared.models.errors.{ErrorWrapper, NinoFormatError, RuleTaxYearNotSupportedError}
import shared.models.outcomes.ResponseWrapper
import shared.services.{MockAuditService, MockEnrolmentsAuthService, MockMtdIdLookupService}
import play.api.Configuration
import play.api.libs.json.{JsObject, JsValue}
import play.api.mvc.Result
import shared.models.audit.GenericAuditDetailFixture.nino
import v4.controllers.validators.MockRetrieveCalculationValidatorFactory
import v4.mocks.services.MockRetrieveCalculationService
import v4.models.request.RetrieveCalculationRequestData
import v4.models.response.retrieveCalculation.CalculationFixture

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
    with CalculationFixture {

  private val calculationId                       = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"
  private val taxYear                             = "2017-18"
  private val responseWithR8b                     = minimalCalculationR8bResponse
  private val responseWithAdditionalFields        = minimalCalculationAdditionalFieldsResponse
  private val responseWithCl290Enabled            = minimalCalculationCl290EnabledResponse
  private val mtdResponseWithR8BJson              = minimumCalculationResponseR8BEnabledJson
  private val mtdResponseWithAdditionalFieldsJson = responseAdditionalFieldsEnabledJson
  private val mtdResponseWithCl290EnabledJson     = minimumResponseCl290EnabledJson

  private val requestData: RetrieveCalculationRequestData =
    RetrieveCalculationRequestData(Nino(nino), TaxYear.fromMtd(taxYear), CalculationId(calculationId))

  "handleRequest" should {
    "return OK with the calculation" when {
      "happy path with R8B feature switch enabled" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockRetrieveCalculationService
          .retrieveCalculation(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, responseWithR8b))))

        MockAppConfig.featureSwitchConfig
          .returns(Configuration("r8b-api.enabled" -> true, "retrieveSAAdditionalFields.enabled" -> false, "cl290.enabled" -> false))
          .anyNumberOfTimes()

        runOkTestWithAudit(
          expectedStatus = OK,
          maybeExpectedResponseBody = Some(mtdResponseWithR8BJson),
          maybeAuditResponseBody = Some(mtdResponseWithR8BJson)
        )
      }

      "happy path with Additional Fields feature switch enabled" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockRetrieveCalculationService
          .retrieveCalculation(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, responseWithAdditionalFields))))

        MockAppConfig.featureSwitchConfig
          .returns(Configuration("r8b-api.enabled" -> false, "retrieveSAAdditionalFields.enabled" -> true, "cl290.enabled" -> false))
          .anyNumberOfTimes()

        runOkTestWithAudit(
          expectedStatus = OK,
          maybeExpectedResponseBody = Some(mtdResponseWithAdditionalFieldsJson),
          maybeAuditResponseBody = Some(mtdResponseWithAdditionalFieldsJson)
        )
      }

      "happy path with cl290 feature switch enabled" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockRetrieveCalculationService
          .retrieveCalculation(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, responseWithCl290Enabled))))

        MockAppConfig.featureSwitchConfig
          .returns(Configuration("r8b-api.enabled" -> false, "retrieveSAAdditionalFields.enabled" -> false, "cl290.enabled" -> true))
          .anyNumberOfTimes()


        runOkTestWithAudit(
          expectedStatus = OK,
          maybeExpectedResponseBody = Some(mtdResponseWithCl290EnabledJson),
          maybeAuditResponseBody = Some(mtdResponseWithCl290EnabledJson)
        )
      }

      "happy path with R8B, additional fields, and cl290 feature switches disabled" in new Test {
        val updatedMtdResponse: JsObject                 = minimumCalculationResponseWithSwitchesDisabledJson
        willUseValidator(returningSuccess(requestData))

        MockRetrieveCalculationService
          .retrieveCalculation(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, responseWithR8b))))

        MockAppConfig.featureSwitchConfig
          .returns(Configuration("r8b-api.enabled" -> false, "retrieveSAAdditionalFields.enabled" -> false, "cl290.enabled" -> false))
          .anyNumberOfTimes()


        runOkTestWithAudit(
          expectedStatus = OK,
          maybeExpectedResponseBody = Some(updatedMtdResponse),
          maybeAuditResponseBody = Some(updatedMtdResponse)
        )
      }

    }

    "return the error as per spec" when {
      "the parser validation fails" in new Test {

        willUseValidator(returning(NinoFormatError))

        MockAppConfig.featureSwitchConfig
          .returns(Configuration("r8b-api.enabled" -> true))
          .anyNumberOfTimes()

        runErrorTest(NinoFormatError)
      }

      "the service returns an error" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockAppConfig.featureSwitchConfig
          .returns(Configuration("r8b-api.enabled" -> true))
          .anyNumberOfTimes()

        MockRetrieveCalculationService
          .retrieveCalculation(requestData)
          .returns(Future.successful(Left(ErrorWrapper(correlationId, RuleTaxYearNotSupportedError))))

        runErrorTest(RuleTaxYearNotSupportedError)
      }
    }
  }

  private trait Test extends ControllerTest with AuditEventChecking {

    lazy val controller = new RetrieveCalculationController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      validatorFactory = mockRetrieveCalculationValidatorFactory,
      service = mockRetrieveCalculationService,
      cc = cc,
      idGenerator = mockIdGenerator,
      auditService = mockAuditService
    )

    protected def callController(): Future[Result] =
      controller.retrieveCalculation(nino, taxYear, calculationId)(fakeRequest)

    protected def event(auditResponse: AuditResponse, requestBody: Option[JsValue] = None): AuditEvent[GenericAuditDetail] =
      AuditEvent(
        auditType = "RetrieveATaxCalculation",
        transactionName = "retrieve-a-tax-calculation",
        detail = GenericAuditDetail(
          versionNumber = apiVersion.name,
          userType = "Individual",
          agentReferenceNumber = None,
          params = Map("nino" -> nino, "calculationId" -> calculationId, "taxYear" -> taxYear),
          requestBody = requestBody,
          `X-CorrelationId` = correlationId,
          auditResponse = auditResponse
        )
      )

  }

}
