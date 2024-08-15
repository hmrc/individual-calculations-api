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

import play.api.Configuration
import play.api.mvc.Result
import shared.controllers.{ControllerBaseSpec, ControllerTestRunner}
import shared.models.audit.GenericAuditDetailFixture.nino
import shared.models.domain.{Nino, TaxYear}
import shared.models.errors.{ErrorWrapper, NinoFormatError, RuleTaxYearNotSupportedError}
import shared.models.outcomes.ResponseWrapper
import shared.services.{MockEnrolmentsAuthService, MockMtdIdLookupService}
import shared.utils.MockIdGenerator
import v4.controllers.validators.MockListCalculationsValidatorFactory
import v4.fixtures.ListCalculationsFixture
import v4.mocks.services.MockListCalculationsService
import v4.models.request.ListCalculationsRequestData

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ListCalculationsControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockListCalculationsValidatorFactory
    with MockListCalculationsService
    with MockIdGenerator
    with ListCalculationsFixture {

  val taxYear: Option[String] = Some("2020-21")

  private val requestData: ListCalculationsRequestData = ListCalculationsRequestData(
    nino = Nino(nino),
    taxYear = taxYear.map(TaxYear.fromMtd).getOrElse(TaxYear.now())
  )

  "ListCalculationsController" when {
    "a valid request is supplied" should {
      "return the expected result for a successful service response" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockListCalculationsService
          .list(requestData)
          .returns(
            Future.successful(Right(ResponseWrapper(correlationId, listCalculationsResponseModel)))
          )

        runOkTest(OK, Some(listCalculationsMtdJson))
      }
    }

    "return the error as per spec" when {
      "the parser validation fails" in new Test {
        willUseValidator(returning(NinoFormatError))

        runErrorTest(NinoFormatError)
      }

      "the service returns an error" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockListCalculationsService
          .list(requestData)
          .returns(Future.successful(Left(ErrorWrapper(correlationId, RuleTaxYearNotSupportedError))))

        runErrorTest(RuleTaxYearNotSupportedError)
      }
    }
  }

  private trait Test extends ControllerTest {

    val controller: ListCalculationsController = new ListCalculationsController(
      mockEnrolmentsAuthService,
      mockMtdIdLookupService,
      mockListCalculationsFactory,
      mockListCalculationsService,
      cc,
      mockIdGenerator
    )

    MockedAppConfig.featureSwitchConfig.anyNumberOfTimes() returns Configuration(
      "supporting-agents-access-control.enabled" -> true
    )

    MockedAppConfig.endpointAllowsSupportingAgents(controller.endpointName).anyNumberOfTimes() returns false

    override protected def callController(): Future[Result] = controller.list(validNino, taxYear)(fakeRequest)
  }

}
