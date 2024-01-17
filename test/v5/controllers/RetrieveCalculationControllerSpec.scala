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
import api.mocks.MockIdGenerator
import api.mocks.hateoas.MockHateoasFactory
import api.mocks.services.{MockAuditService, MockEnrolmentsAuthService, MockMtdIdLookupService}
import api.models.domain.{CalculationId, Nino, TaxYear}
import api.models.errors.{ErrorWrapper, NinoFormatError, RuleTaxYearNotSupportedError}
import api.models.hateoas.HateoasWrapper
import api.models.outcomes.ResponseWrapper
import config.AppConfig
import mocks.MockAppConfig
import play.api.Configuration
import play.api.libs.json.JsObject
import play.api.mvc.Result
import routing.{Version, Version3}
import v5.mocks.requestParsers.MockRetrieveCalculationParser
import v5.mocks.services.MockRetrieveCalculationService
import v5.models.request.{RetrieveCalculationRawData, RetrieveCalculationRequest}
import v5.models.response.retrieveCalculation.{CalculationFixture, RetrieveCalculationHateoasData, RetrieveCalculationResponse}

import java.time.LocalDateTime
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RetrieveCalculationControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockRetrieveCalculationParser
    with MockHateoasFactory
    with MockRetrieveCalculationService
    with MockAppConfig
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

  implicit val appConfig: AppConfig = mockAppConfig
  implicit val apiVersion: Version  = Version3

  trait Test extends ControllerTest {
    def taxYear: String
    def rawData: RetrieveCalculationRawData     = RetrieveCalculationRawData(nino, taxYear, calculationId)
    def requestData: RetrieveCalculationRequest = RetrieveCalculationRequest(Nino(nino), TaxYear.fromMtd(taxYear), CalculationId(calculationId))

    lazy val controller = new RetrieveCalculationController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      parser = mockRetrieveCalculationParser,
      service = mockRetrieveCalculationService,
      cc = cc,
      hateoasFactory = mockHateoasFactory,
      idGenerator = mockIdGenerator
    )

    protected def callController(): Future[Result] = controller.retrieveCalculation(nino, taxYear, calculationId)(fakeRequest)

    MockAppConfig
      .isApiDeprecated(apiVersion)
      .returns(false)
      .anyNumberOfTimes()

    MockAppConfig
      .sunsetDate(apiVersion)
      .returns(Some(LocalDateTime.of(2023, 1, 17, 12, 0)))
      .anyNumberOfTimes()

  }

  trait NonTysTest extends Test {
    def taxYear: String = "2017-18"
  }

  trait TysTest extends Test {
    def taxYear: String = "2024-25"
  }

  "handleRequest" should {
    "return OK with the calculation" when {
      "happy path with R8B feature switch enabled" in new NonTysTest {
        MockRetrieveCalculationParser
          .parseRequest(rawData)
          .returns(Right(requestData))

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

        runOkTest(OK, Some(mtdResponseWithR8BJson))
      }

      "happy path with Additional Fields feature switch enabled" in new NonTysTest {
        MockRetrieveCalculationParser
          .parseRequest(rawData)
          .returns(Right(requestData))

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

        runOkTest(OK, Some(mtdResponseWithAdditionalFieldsJson))
      }

      "happy path with cl290 feature switch enabled" in new NonTysTest {
        MockRetrieveCalculationParser
          .parseRequest(rawData)
          .returns(Right(requestData))

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

        runOkTest(OK, Some(mtdResponseWithCl290EnabledJson))
      }

      "happy path with BasicRateDivergence feature switch enabled and TYS (2025)" in new TysTest {
        MockRetrieveCalculationParser
          .parseRequest(rawData)
          .returns(Right(requestData))

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

        runOkTest(OK, Some(mtdResponseWithBasicRateDivergenceEnabledJson))
      }

      "happy path with R8B; additional fields; cl290 and basicRateDivergence feature switches disabled" in new NonTysTest {
        val updatedRawData: RetrieveCalculationRawData   = rawData
        val updatedResponse: RetrieveCalculationResponse = responseWithR8b.copy(calculation = Some(calculationWithR8BDisabled))
        val updatedMtdResponse: JsObject                 = minimumCalculationResponseWithSwitchesDisabledJson ++ hateoaslinksJson
        MockRetrieveCalculationParser
          .parseRequest(updatedRawData)
          .returns(Right(requestData))

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

        runOkTest(OK, Some(updatedMtdResponse))
      }

    }

    "return the error as per spec" when {
      "the parser validation fails" in new NonTysTest {
        MockRetrieveCalculationParser
          .parseRequest(rawData)
          .returns(Left(ErrorWrapper(correlationId, NinoFormatError, None)))
        MockAppConfig.featureSwitches
          .returns(Configuration("r8b-api.enabled" -> true))
          .anyNumberOfTimes()

        runErrorTest(NinoFormatError)
      }

      "the service returns an error" in new NonTysTest {
        MockRetrieveCalculationParser
          .parseRequest(rawData)
          .returns(Right(requestData))
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

}
