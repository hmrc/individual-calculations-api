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

package v5.controllers

import api.controllers.{ControllerBaseSpec, ControllerTestRunner}
import api.hateoas.{HateoasWrapper, MockHateoasFactory}
import api.mocks.MockIdGenerator
import api.models.audit.{AuditEvent, AuditResponse, GenericAuditDetail}
import api.models.domain.{CalculationId, Nino, TaxYear}
import api.models.errors.{ErrorWrapper, NinoFormatError, RuleTaxYearNotSupportedError}
import api.models.outcomes.ResponseWrapper
import api.services.{MockAuditService, MockEnrolmentsAuthService, MockMtdIdLookupService}
import play.api.Configuration
import play.api.libs.json.{JsObject, JsValue}
import play.api.mvc.Result
import v5.controllers.validators.MockRetrieveCalculationValidatorFactory
import v5.mocks.services.MockRetrieveCalculationService
import v5.models.request.RetrieveCalculationRequestData
import v5.models.response.retrieveCalculation.{CalculationFixture, RetrieveCalculationHateoasData, RetrieveCalculationResponse}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RetrieveCalculationControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockRetrieveCalculationValidatorFactory
    with MockHateoasFactory
    with MockRetrieveCalculationService
    with MockAuditService
    with MockIdGenerator
    with CalculationFixture {

  private val calculationId                                 = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"
  private val responseWithR8b                               = minimalCalculationR8bResponse
  private val responseWithAdditionalFields                  = minimalCalculationAdditionalFieldsResponse
  private val responseWithCl290Enabled                      = minimalCalculationCl290EnabledResponse
  private val responseWithBasicRateDivergenceEnabled        = minimalCalculationBasicRateDivergenceEnabledResponse
  private val mtdResponseWithR8BJson                        = minimumCalculationResponseR8BEnabledJson ++ hateoaslinksJson
  private val mtdResponseWithAdditionalFieldsJson           = responseAdditionalFieldsEnabledJson ++ hateoaslinksJson
  private val mtdResponseWithCl290EnabledJson               = minimumResponseCl290EnabledJson ++ hateoaslinksJson
  private val mtdResponseWithBasicRateDivergenceEnabledJson = minimumCalculationResponseBasicRateDivergenceEnabledJson ++ hateoaslinksJson

  "handleRequest" should {
    "return OK with the calculation" when {
      "happy path with R8B feature switch enabled" in new NonTysTest {
        willUseValidator(returningSuccess(requestData))

        MockRetrieveCalculationService
          .retrieveCalculation(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, responseWithR8b))))

        MockAppConfig.featureSwitches
          .returns(
            Configuration(
              "r8b-api.enabled"                    -> true,
              "retrieveSAAdditionalFields.enabled" -> false,
              "cl290.enabled"                      -> false,
              "basicRateDivergence.enabled"        -> false))
          .anyNumberOfTimes()

        MockHateoasFactory
          .wrap(responseWithR8b, RetrieveCalculationHateoasData(nino, TaxYear.fromMtd(taxYear), calculationId, responseWithR8b))
          .returns(HateoasWrapper(responseWithR8b, hateoaslinks))


        runOkTestWithAudit(
          expectedStatus = OK,
          maybeAuditRequestBody = Some(mtdResponseWithR8BJson),
          maybeExpectedResponseBody = Some(mtdResponseWithR8BJson),
          maybeAuditResponseBody = None
        )
      }

      "happy path with Additional Fields feature switch enabled" in new NonTysTest {
        willUseValidator(returningSuccess(requestData))

        MockRetrieveCalculationService
          .retrieveCalculation(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, responseWithAdditionalFields))))

        MockAppConfig.featureSwitches
          .returns(
            Configuration(
              "r8b-api.enabled"                    -> false,
              "retrieveSAAdditionalFields.enabled" -> true,
              "cl290.enabled"                      -> false,
              "basicRateDivergence.enabled"        -> false))
          .anyNumberOfTimes()

        MockHateoasFactory
          .wrap(
            responseWithAdditionalFields,
            RetrieveCalculationHateoasData(nino, TaxYear.fromMtd(taxYear), calculationId, responseWithAdditionalFields))
          .returns(HateoasWrapper(responseWithAdditionalFields, hateoaslinks))


        runOkTestWithAudit(
          expectedStatus = OK,
          maybeAuditRequestBody = Some(mtdResponseWithAdditionalFieldsJson),
          maybeExpectedResponseBody = Some(mtdResponseWithAdditionalFieldsJson),
          maybeAuditResponseBody = None
        )
      }

      "happy path with cl290 feature switch enabled" in new NonTysTest {
        willUseValidator(returningSuccess(requestData))

        MockRetrieveCalculationService
          .retrieveCalculation(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, responseWithCl290Enabled))))

        MockAppConfig.featureSwitches
          .returns(
            Configuration(
              "r8b-api.enabled"                    -> false,
              "retrieveSAAdditionalFields.enabled" -> false,
              "cl290.enabled"                      -> true,
              "basicRateDivergence.enabled"        -> false))
          .anyNumberOfTimes()

        MockHateoasFactory
          .wrap(responseWithCl290Enabled, RetrieveCalculationHateoasData(nino, TaxYear.fromMtd(taxYear), calculationId, responseWithCl290Enabled))
          .returns(HateoasWrapper(responseWithCl290Enabled, hateoaslinks))


        runOkTestWithAudit(
          expectedStatus = OK,
          maybeAuditRequestBody = Some(mtdResponseWithCl290EnabledJson),
          maybeExpectedResponseBody = Some(mtdResponseWithCl290EnabledJson),
          maybeAuditResponseBody = None
        )
      }

      "happy path with BasicRateDivergence feature switch enabled and TYS (2025)" in new TysTest {
        willUseValidator(returningSuccess(requestData))

        MockRetrieveCalculationService
          .retrieveCalculation(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, responseWithBasicRateDivergenceEnabled))))

        MockAppConfig.featureSwitches
          .returns(
            Configuration(
              "r8b-api.enabled"                    -> false,
              "retrieveSAAdditionalFields.enabled" -> false,
              "cl290.enabled"                      -> false,
              "basicRateDivergence.enabled"        -> true))
          .anyNumberOfTimes()

        MockHateoasFactory
          .wrap(
            responseWithBasicRateDivergenceEnabled,
            RetrieveCalculationHateoasData(nino, TaxYear.fromMtd(taxYear), calculationId, responseWithBasicRateDivergenceEnabled)
          )
          .returns(HateoasWrapper(responseWithBasicRateDivergenceEnabled, hateoaslinks))


        runOkTestWithAudit(
          expectedStatus = OK,
          maybeAuditRequestBody = Some(mtdResponseWithBasicRateDivergenceEnabledJson),
          maybeExpectedResponseBody = Some(mtdResponseWithBasicRateDivergenceEnabledJson),
          maybeAuditResponseBody = None
        )
      }

      "happy path with R8B; additional fields; cl290 and basicRateDivergence feature switches disabled" in new NonTysTest {
        val updatedResponse: RetrieveCalculationResponse = responseWithR8b.copy(calculation = Some(calculationWithR8BDisabled))
        val updatedMtdResponse: JsObject                 = minimumCalculationResponseWithSwitchesDisabledJson ++ hateoaslinksJson
        willUseValidator(returningSuccess(requestData))

        MockRetrieveCalculationService
          .retrieveCalculation(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, responseWithR8b))))

        MockAppConfig.featureSwitches
          .returns(
            Configuration(
              "r8b-api.enabled"                    -> false,
              "retrieveSAAdditionalFields.enabled" -> false,
              "cl290.enabled"                      -> false,
              "basicRateDivergence.enabled"        -> false))
          .anyNumberOfTimes()

        MockHateoasFactory
          .wrap(updatedResponse, RetrieveCalculationHateoasData(nino, TaxYear.fromMtd(taxYear), calculationId, updatedResponse))
          .returns(HateoasWrapper(updatedResponse, hateoaslinks))


        runOkTestWithAudit(
          expectedStatus = OK,
          maybeAuditRequestBody = Some(updatedMtdResponse),
          maybeExpectedResponseBody = Some(updatedMtdResponse),
          maybeAuditResponseBody = None
        )
      }

    }

    "return the error as per spec" when {
      "the parser validation fails" in new NonTysTest {

        willUseValidator(returning(NinoFormatError))

        MockAppConfig.featureSwitches
          .returns(Configuration("r8b-api.enabled" -> true))
          .anyNumberOfTimes()

        runErrorTest(NinoFormatError)
      }

      "the service returns an error" in new NonTysTest {

        willUseValidator(returningSuccess(requestData))

        MockAppConfig.featureSwitches
          .returns(Configuration("r8b-api.enabled" -> true))
          .anyNumberOfTimes()

        MockRetrieveCalculationService
          .retrieveCalculation(requestData)
          .returns(Future.successful(Left(ErrorWrapper(correlationId, RuleTaxYearNotSupportedError))))

        runErrorTest(RuleTaxYearNotSupportedError)
      }
    }
  }

  trait Test extends ControllerTest with AuditEventChecking {
    def taxYear: String

    def requestData: RetrieveCalculationRequestData =
      RetrieveCalculationRequestData(Nino(nino), TaxYear.fromMtd(taxYear), CalculationId(calculationId))

    lazy val controller = new RetrieveCalculationController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      validatorFactory = mockRetrieveCalculationValidatorFactory,
      service = mockRetrieveCalculationService,
      cc = cc,
      hateoasFactory = mockHateoasFactory,
      idGenerator = mockIdGenerator,
      auditService = mockAuditService
    )

    protected def callController(): Future[Result] =
      controller.retrieveCalculation(nino, taxYear, calculationId)(fakeRequest)

    protected def event(auditResponse: AuditResponse, requestBody: Option[JsValue]): AuditEvent[GenericAuditDetail] =
      AuditEvent(
        auditType = "RetrieveATaxCalculation",
        transactionName = "retrieve-a-tax-calculation",
        detail = GenericAuditDetail(
          versionNumber = "5.0",
          userType = "Individual",
          agentReferenceNumber = None,
          params = Map("nino" -> nino, "calculationId" -> calculationId, "taxYear" -> taxYear),
          requestBody = None,
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
