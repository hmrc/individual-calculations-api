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
import api.mocks.services.{MockEnrolmentsAuthService, MockMtdIdLookupService}
import api.models.domain.{Nino, TaxYear}
import api.models.errors.{ErrorWrapper, NinoFormatError, RuleTaxYearNotSupportedError}
import api.models.hateoas.{HateoasWrapper, Link, Method, RelType}
import api.models.outcomes.ResponseWrapper
import config.AppConfig
import play.api.http.HeaderNames
import play.api.mvc.{AnyContentAsEmpty, Result}
import play.api.test.FakeRequest
import v3.fixtures.ListCalculationsFixture
import v3.mocks.requestParsers.MockListCalculationsParser
import v3.mocks.services.MockListCalculationsService
import v3.models.request.{ListCalculationsRawData, ListCalculationsRequest}
import v3.models.response.listCalculations.{ListCalculationsHateoasData, ListCalculationsResponse}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ListCalculationsControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockListCalculationsParser
    with MockListCalculationsService
    with MockHateoasFactory
    with MockIdGenerator
    with ListCalculationsFixture {
  val taxYear: Option[String]          = Some("2020-21")
  val rawData: ListCalculationsRawData = ListCalculationsRawData(nino, taxYear)

  override implicit lazy val fakeRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest().withHeaders(HeaderNames.ACCEPT -> s"application/vnd.hmrc.3.0+json")

  implicit val appConfig: AppConfig = mockAppConfig

  class Test extends ControllerTest {

    val taxYear: Option[String] = rawData.taxYear

    lazy val request: ListCalculationsRequest = ListCalculationsRequest(
      nino = Nino(nino),
      taxYear = taxYear.map(TaxYear.fromMtd).getOrElse(TaxYear.now())
    )

    val controller: ListCalculationsController = new ListCalculationsController(
      mockEnrolmentsAuthService,
      mockMtdIdLookupService,
      mockListCalculationsParser,
      mockListCalculationsService,
      mockHateoasFactory,
      cc,
      mockIdGenerator
    )

    override protected def callController(): Future[Result] = controller.list(nino, taxYear)(fakeRequest)

    MockAppConfig
      .apiDocumentationUrl()
      .returns("")
      .anyNumberOfTimes()

  }

  "ListCalculationsController" when {
    "a valid request is supplied" should {
      "return the expected result for a successful service response" in new Test {
        MockListCalculationsParser.parseRequest(rawData).returns(Right(request))

        MockListCalculationsService
          .list(request)
          .returns(
            Future.successful(Right(ResponseWrapper(correlationId, listCalculationsResponseModel)))
          )

        MockHateoasFactory
          .wrapList(listCalculationsResponseModel, ListCalculationsHateoasData(nino, request.taxYear))
          .returns(
            HateoasWrapper(
              ListCalculationsResponse(Seq(HateoasWrapper(
                calculationModel,
                Seq(Link(
                  href = s"/individuals/calculations/$nino/self-assessment/${taxYear.get}/${calculationModel.calculationId}",
                  rel = RelType.SELF,
                  method = Method.GET
                ))
              ))),
              Seq(
                Link(
                  href = s"/individuals/calculations/$nino/self-assessment/${taxYear.get}",
                  rel = RelType.TRIGGER,
                  method = Method.POST
                ),
                Link(
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
        MockListCalculationsParser
          .parseRequest(rawData)
          .returns(Left(ErrorWrapper(correlationId, NinoFormatError, None)))

        runErrorTest(NinoFormatError)
      }

      "the service returns an error" in new Test {
        MockListCalculationsParser
          .parseRequest(rawData)
          .returns(Right(request))

        MockListCalculationsService
          .list(request)
          .returns(Future.successful(Left(ErrorWrapper(correlationId, RuleTaxYearNotSupportedError))))

        runErrorTest(RuleTaxYearNotSupportedError)
      }
    }
  }

}
