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

import api.controllers.{ControllerBaseSpec, ControllerTestRunner}
import api.hateoas
import api.hateoas.{HateoasWrapper, Method, MockHateoasFactory, RelType}
import api.mocks.MockIdGenerator
import api.models.domain.{Nino, TaxYear}
import api.models.errors.{ErrorWrapper, NinoFormatError, RuleTaxYearNotSupportedError}
import api.models.outcomes.ResponseWrapper
import api.services.{MockEnrolmentsAuthService, MockMtdIdLookupService}
import play.api.mvc.Result
import v4.controllers.validators.MockListCalculationsValidatorFactory
import v4.fixtures.ListCalculationsFixture
import v4.mocks.services.MockListCalculationsService
import v4.models.request.ListCalculationsRequestData
import v4.models.response.listCalculations.{ListCalculationsHateoasData, ListCalculationsResponse}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ListCalculationsControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockListCalculationsValidatorFactory
    with MockListCalculationsService
    with MockHateoasFactory
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

        MockHateoasFactory
          .wrapList(listCalculationsResponseModel, ListCalculationsHateoasData(nino, requestData.taxYear))
          .returns(
            HateoasWrapper(
              ListCalculationsResponse(Seq(HateoasWrapper(
                calculationModel,
                Seq(hateoas.Link(
                  href = s"/individuals/calculations/$nino/self-assessment/${taxYear.get}/${calculationModel.calculationId}",
                  rel = RelType.SELF,
                  method = Method.GET
                ))
              ))),
              Seq(
                hateoas.Link(
                  href = s"/individuals/calculations/$nino/self-assessment/${taxYear.get}",
                  rel = RelType.TRIGGER,
                  method = Method.POST
                ),
                hateoas.Link(
                  href = s"/individuals/calculations/$nino/self-assessment?taxYear=${taxYear.get}",
                  rel = RelType.SELF,
                  method = Method.GET
                )
              )
            )
          )

        runOkTest(OK, Some(listCalculationsMtdJsonWithHateoas(nino, taxYear.get)))
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
      mockHateoasFactory,
      cc,
      mockIdGenerator
    )

    override protected def callController(): Future[Result] = controller.list(nino, taxYear)(fakeRequest)
  }

}
