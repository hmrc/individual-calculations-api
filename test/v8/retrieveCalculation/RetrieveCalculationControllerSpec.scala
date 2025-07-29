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

package v8.retrieveCalculation

import play.api.Configuration
import play.api.libs.json.{JsObject, JsValue}
import play.api.mvc.Result
import shared.config.MockAppConfig
import shared.controllers.{ControllerBaseSpec, ControllerTestRunner}
import shared.models.audit.{AuditEvent, AuditResponse, GenericAuditDetail}
import shared.models.domain.{CalculationId, Nino, TaxYear}
import shared.models.errors.{ErrorWrapper, NinoFormatError, RuleTaxYearNotSupportedError}
import shared.models.outcomes.ResponseWrapper
import shared.services.{MockAuditService, MockEnrolmentsAuthService, MockMtdIdLookupService}
import shared.utils.MockIdGenerator
import v8.retrieveCalculation.def1.model.Def1_CalculationFixture
import v8.retrieveCalculation.models.request.{Def1_RetrieveCalculationRequestData, RetrieveCalculationRequestData}

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
    with MockAppConfig
    with Def1_CalculationFixture {

  private val calculationId                                 = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"
  private val responseWithR8b                               = minimalCalculationR8bResponse
  private val responseWithAdditionalFields                  = minimalCalculationAdditionalFieldsResponse
  private val responseWithCl290Enabled                      = minimalCalculationCl290EnabledResponse
  private val responseWithBasicRateDivergenceEnabled        = minimalCalculationBasicRateDivergenceEnabledResponse
  private val mtdResponseWithR8BJson                        = minimumCalculationResponseR8BEnabledJson
  private val mtdResponseWithAdditionalFieldsJson           = responseAdditionalFieldsEnabledJson
  private val mtdResponseWithCl290EnabledJson               = minimumResponseCl290EnabledJson
  private val mtdResponseWithBasicRateDivergenceEnabledJson = minimumCalculationResponseBasicRateDivergenceEnabledJson

  "handleRequest" should {
    "return OK with the calculation" when {
      "happy path with R8B feature switch enabled" in new NonTysTest {
        willUseValidator(returningSuccess(requestData))

        MockRetrieveCalculationService
          .retrieveCalculation(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, responseWithR8b))))

        MockedAppConfig.featureSwitchConfig
          .returns(
            Configuration(
              "r8b-api.enabled"                    -> true,
              "retrieveSAAdditionalFields.enabled" -> false,
              "cl290.enabled"                      -> false,
              "basicRateDivergence.enabled"        -> false
            ))
          .anyNumberOfTimes()

        runOkTestWithAudit(
          expectedStatus = OK,
          maybeExpectedResponseBody = Some(mtdResponseWithR8BJson),
          maybeAuditResponseBody = Some(mtdResponseWithR8BJson)
        )
      }

      "happy path with Additional Fields feature switch enabled" in new NonTysTest {
        willUseValidator(returningSuccess(requestData))

        MockRetrieveCalculationService
          .retrieveCalculation(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, responseWithAdditionalFields))))

        MockedAppConfig.featureSwitchConfig
          .returns(
            Configuration(
              "r8b-api.enabled"                    -> false,
              "retrieveSAAdditionalFields.enabled" -> true,
              "cl290.enabled"                      -> false,
              "basicRateDivergence.enabled"        -> false))
          .anyNumberOfTimes()

        runOkTestWithAudit(
          expectedStatus = OK,
          maybeExpectedResponseBody = Some(mtdResponseWithAdditionalFieldsJson),
          maybeAuditResponseBody = Some(mtdResponseWithAdditionalFieldsJson)
        )
      }

      "happy path with cl290 feature switch enabled" in new NonTysTest {
        willUseValidator(returningSuccess(requestData))

        MockRetrieveCalculationService
          .retrieveCalculation(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, responseWithCl290Enabled))))

        MockedAppConfig.featureSwitchConfig
          .returns(
            Configuration(
              "r8b-api.enabled"                    -> false,
              "retrieveSAAdditionalFields.enabled" -> false,
              "cl290.enabled"                      -> true,
              "basicRateDivergence.enabled"        -> false))
          .anyNumberOfTimes()

        runOkTestWithAudit(
          expectedStatus = OK,
          maybeExpectedResponseBody = Some(mtdResponseWithCl290EnabledJson),
          maybeAuditResponseBody = Some(mtdResponseWithCl290EnabledJson)
        )
      }

      "happy path with BasicRateDivergence feature switch enabled and TYS (2025)" in new TysTest {
        willUseValidator(returningSuccess(requestData))

        MockRetrieveCalculationService
          .retrieveCalculation(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, responseWithBasicRateDivergenceEnabled))))

        MockedAppConfig.featureSwitchConfig
          .returns(
            Configuration(
              "r8b-api.enabled"                    -> false,
              "retrieveSAAdditionalFields.enabled" -> false,
              "cl290.enabled"                      -> false,
              "basicRateDivergence.enabled"        -> true))
          .anyNumberOfTimes()

        runOkTestWithAudit(
          expectedStatus = OK,
          maybeExpectedResponseBody = Some(mtdResponseWithBasicRateDivergenceEnabledJson),
          maybeAuditResponseBody = Some(mtdResponseWithBasicRateDivergenceEnabledJson)
        )
      }

      "happy path with R8B; additional fields; cl290 and basicRateDivergence feature switches disabled" in new NonTysTest {
        val updatedMtdResponse: JsObject = minimumCalculationResponseWithSwitchesDisabledJson
        willUseValidator(returningSuccess(requestData))

        MockRetrieveCalculationService
          .retrieveCalculation(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, responseWithR8b))))

        MockedAppConfig.featureSwitchConfig
          .returns(
            Configuration(
              "r8b-api.enabled"                    -> false,
              "retrieveSAAdditionalFields.enabled" -> false,
              "cl290.enabled"                      -> false,
              "basicRateDivergence.enabled"        -> false))
          .anyNumberOfTimes()

        runOkTestWithAudit(
          expectedStatus = OK,
          maybeExpectedResponseBody = Some(updatedMtdResponse),
          maybeAuditResponseBody = Some(updatedMtdResponse)
        )
      }

    }

    "return the error as per spec" when {
      "the parser validation fails" in new NonTysTest {

        willUseValidator(returning(NinoFormatError))

        MockedAppConfig.featureSwitchConfig
          .returns(Configuration("r8b-api.enabled" -> true))
          .anyNumberOfTimes()

        runErrorTest(NinoFormatError)
      }

      "the service returns an error" in new NonTysTest {

        willUseValidator(returningSuccess(requestData))

        MockedAppConfig.featureSwitchConfig
          .returns(Configuration("r8b-api.enabled" -> true))
          .anyNumberOfTimes()

        MockRetrieveCalculationService
          .retrieveCalculation(requestData)
          .returns(Future.successful(Left(ErrorWrapper(correlationId, RuleTaxYearNotSupportedError))))

        runErrorTest(RuleTaxYearNotSupportedError)
      }
    }
  }

  trait Test extends ControllerTest with AuditEventChecking[GenericAuditDetail] {
    def taxYear: String

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

  trait NonTysTest extends Test {
    def taxYear: String = "2017-18"
  }

  trait TysTest extends Test {
    def taxYear: String = "2024-25"
  }

}
