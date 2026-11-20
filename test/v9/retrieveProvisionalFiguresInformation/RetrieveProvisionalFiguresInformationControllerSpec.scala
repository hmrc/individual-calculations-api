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

package v9.retrieveProvisionalFiguresInformation

import api.controllers.{ControllerBaseSpec, ControllerTestRunner}
import api.models.domain.TaxYear
import api.models.errors.*
import api.models.outcomes.ResponseWrapper
import play.api.Configuration
import play.api.mvc.Result
import v9.retrieveProvisionalFiguresInformation.RetrieveProvisionalFiguresInformationFixtures.{responseModel, responseMtdJson}
import v9.retrieveProvisionalFiguresInformation.model.request.RetrieveProvisionalFiguresInformationRequestData

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RetrieveProvisionalFiguresInformationControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockRetrieveProvisionalFiguresInformationService
    with MockRetrieveProvisionalFiguresInformationValidatorFactory {

  private val taxYear = "2026-27"

  private val requestData: RetrieveProvisionalFiguresInformationRequestData =
    RetrieveProvisionalFiguresInformationRequestData(parsedNino, TaxYear.fromMtd(taxYear))

  "RetrieveProvisionalFiguresInformationController" should {
    "return OK status" when {
      "the request received is valid" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockRetrieveProvisionalFiguresInformationService
          .retrieveProvisionalFiguresInformation(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, responseModel))))

        runOkTest(expectedStatus = OK, maybeExpectedResponseBody = Some(responseMtdJson))
      }
    }

    "return the error as per spec" when {
      "the parser validation fails" in new Test {
        willUseValidator(returning(NinoFormatError))

        runErrorTest(NinoFormatError)
      }

      "the service returns an error" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockRetrieveProvisionalFiguresInformationService
          .retrieveProvisionalFiguresInformation(requestData)
          .returns(Future.successful(Left(ErrorWrapper(correlationId, TaxYearFormatError))))

        runErrorTest(TaxYearFormatError)
      }
    }
  }

  private trait Test extends ControllerTest {

    val controller: RetrieveProvisionalFiguresInformationController = new RetrieveProvisionalFiguresInformationController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      service = mockRetrieveProvisionalFiguresInformationService,
      validatorFactory = mockRetrieveProvisionalFiguresInformationValidatorFactory,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    MockedAppConfig.featureSwitchConfig.anyNumberOfTimes() returns Configuration(
      "supporting-agents-access-control.enabled" -> true
    )

    MockedAppConfig.endpointAllowsSupportingAgents(controller.endpointName).anyNumberOfTimes() returns false

    protected def callController(): Future[Result] = controller.retrieveProvisionalFiguresInformation(validNino, taxYear)(fakeRequest)

  }

}
