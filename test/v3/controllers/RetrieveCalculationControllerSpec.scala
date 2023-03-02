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

import mocks.{MockAppConfig, MockIdGenerator}
import play.api.Configuration
import play.api.mvc.Result
import v3.mocks.hateoas.MockHateoasFactory
import v3.mocks.requestParsers.MockRetrieveCalculationParser
import v3.mocks.services.{MockAuditService, MockEnrolmentsAuthService, MockMtdIdLookupService, MockRetrieveCalculationService}
import v3.models.domain.{Nino, TaxYear}
import v3.models.errors._
import v3.models.hateoas.HateoasWrapper
import v3.models.outcomes.ResponseWrapper
import v3.models.request.{RetrieveCalculationRawData, RetrieveCalculationRequest}
import v3.models.response.retrieveCalculation.{CalculationFixture, RetrieveCalculationHateoasData}

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
    with MockAuditService
    with MockIdGenerator
    with MockAppConfig
    with CalculationFixture {

  private val taxYear       = "2017-18"
  private val calculationId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"

  private val response        = minimalCalculationResponse
  private val mtdResponseJson = minimumCalculationResponseMtdJson ++ hateoaslinksJson
  private val r8bMtdResponseJson = emptyCalculationResponseMtdJson ++ hateoaslinksJson

  val rawData: RetrieveCalculationRawData     = RetrieveCalculationRawData(nino, taxYear, calculationId)
  val requestData: RetrieveCalculationRequest = RetrieveCalculationRequest(Nino(nino), TaxYear.fromMtd(taxYear), calculationId)

  trait Test extends ControllerTest {

    val controller = new RetrieveCalculationController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      parser = mockRetrieveCalculationParser,
      service = mockService,
      cc = cc,
      hateoasFactory = mockHateoasFactory,
      idGenerator = mockIdGenerator,
      appConfig = mockAppConfig
    )

    protected def callController(): Future[Result] = controller.retrieveCalculation(nino, taxYear, calculationId)(fakeRequest)

  }

  "handleRequest" should {
    "return OK with the calculation" when {
      "happy path with r8b-api disabled" in new Test {
        MockRetrieveCalculationParser
          .parseRequest(rawData)
          .returns(Right(requestData))

        MockRetrieveCalculationService
          .retrieveCalculation(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        MockAppConfig.featureSwitches.returns(Configuration("r8b-api.enabled" -> false))

        MockHateoasFactory
          .wrap(response, RetrieveCalculationHateoasData(nino, TaxYear.fromMtd(taxYear), calculationId, emptyCalculationResponse))
          .returns(HateoasWrapper(emptyCalculationResponse, hateoaslinks))

        runOkTest(OK, Some(r8bMtdResponseJson))
      }

      "happy path with r8b-api disabled but no EOY data" in new Test {
        MockRetrieveCalculationParser
          .parseRequest(rawData)
          .returns(Right(requestData))

        MockRetrieveCalculationService
          .retrieveCalculation(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, noEOYCalculationResponse))))

        MockAppConfig.featureSwitches.returns(Configuration("r8b-api.enabled" -> false))

        MockHateoasFactory
          .wrap(noEOYCalculationResponse, RetrieveCalculationHateoasData(nino, TaxYear.fromMtd(taxYear), calculationId, noEOYCalculationResponse))
          .returns(HateoasWrapper(noEOYCalculationResponse, hateoaslinks))

        runOkTest(OK, Some(noEOYCalculationResponseMtdJson ++ hateoaslinksJson))
      }

      "happy path with r8b-api enabled" in new Test {
        MockRetrieveCalculationParser
          .parseRequest(rawData)
          .returns(Right(requestData))

        MockRetrieveCalculationService
          .retrieveCalculation(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        MockAppConfig.featureSwitches.returns(Configuration("r8b-api.enabled" -> true))
        MockHateoasFactory
          .wrap(response, RetrieveCalculationHateoasData(nino, TaxYear.fromMtd(taxYear), calculationId, response))
          .returns(HateoasWrapper(response, hateoaslinks))

        runOkTest(OK, Some(mtdResponseJson))
      }
    }

    "return the error as per spec" when {
      "the parser validation fails" in new Test {
        MockRetrieveCalculationParser
          .parseRequest(rawData)
          .returns(Left(ErrorWrapper(correlationId, NinoFormatError, None)))

        runErrorTest(NinoFormatError)
      }

      "the service returns an error" in new Test {
        MockRetrieveCalculationParser
          .parseRequest(rawData)
          .returns(Right(requestData))
        MockRetrieveCalculationService
          .retrieveCalculation(requestData)
          .returns(Future.successful(Left(ErrorWrapper(correlationId, RuleTaxYearNotSupportedError))))

        runErrorTest(RuleTaxYearNotSupportedError)
      }
    }
  }

}
