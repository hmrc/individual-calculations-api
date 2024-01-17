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
import v3.mocks.requestParsers.MockRetrieveCalculationParser
import v3.mocks.services.MockRetrieveCalculationService
import v3.models.request.{RetrieveCalculationRawData, RetrieveCalculationRequest}
import v3.models.response.retrieveCalculation.{CalculationFixture, RetrieveCalculationHateoasData, RetrieveCalculationResponse}

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

  private val calculationId                           = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"
  private val taxYear                                 = "2017-18"
  private val response                                = minimalCalculationResponse
  private val mtdResponseJson                         = minimumCalculationResponseR8BEnabledJson ++ hateoaslinksJson
  private val rawData: RetrieveCalculationRawData     = RetrieveCalculationRawData(nino, taxYear, calculationId)
  private val requestData: RetrieveCalculationRequest = RetrieveCalculationRequest(Nino(nino), TaxYear.fromMtd(taxYear), CalculationId(calculationId))

  trait Test extends ControllerTest {

    implicit val appConfig: AppConfig = mockAppConfig
    implicit val apiVersion: Version  = Version3

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

  "handleRequest" should {
    "return OK with the calculation" when {
      "happy path with R8B feature switch enabled" in new Test {
        MockRetrieveCalculationParser
          .parseRequest(rawData)
          .returns(Right(requestData))

        MockRetrieveCalculationService
          .retrieveCalculation(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        MockAppConfig.featureSwitches
          .returns(Configuration("r8b-api.enabled" -> true))
          .anyNumberOfTimes()

        MockHateoasFactory
          .wrap(response, RetrieveCalculationHateoasData(nino, TaxYear.fromMtd(taxYear), calculationId, response))
          .returns(HateoasWrapper(response, hateoaslinks))

        runOkTest(OK, Some(mtdResponseJson))
      }

      "happy path with R8B feature switch disabled" in new Test {
        val updatedRawData: RetrieveCalculationRawData   = rawData
        val updatedResponse: RetrieveCalculationResponse = response.copy(calculation = Some(calculationWithR8BDisabled))
        val updatedMtdResponse: JsObject                 = minimumCalculationResponseWithR8BDisabledJson ++ hateoaslinksJson
        MockRetrieveCalculationParser
          .parseRequest(updatedRawData)
          .returns(Right(requestData))

        MockRetrieveCalculationService
          .retrieveCalculation(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        MockAppConfig.featureSwitches
          .returns(Configuration("r8b-api.enabled" -> false))
          .anyNumberOfTimes()

        MockHateoasFactory
          .wrap(updatedResponse, RetrieveCalculationHateoasData(nino, TaxYear.fromMtd(taxYear), calculationId, updatedResponse))
          .returns(HateoasWrapper(updatedResponse, hateoaslinks))

        runOkTest(OK, Some(updatedMtdResponse))
      }
    }

    "return the error as per spec" when {
      "the parser validation fails" in new Test {
        MockRetrieveCalculationParser
          .parseRequest(rawData)
          .returns(Left(ErrorWrapper(correlationId, NinoFormatError, None)))

        MockAppConfig.featureSwitches
          .returns(Configuration("r8b-api.enabled" -> true))
          .anyNumberOfTimes()

        runErrorTest(NinoFormatError)
      }

      "the service returns an error" in new Test {
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
