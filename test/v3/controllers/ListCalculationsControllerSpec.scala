/*
 * Copyright 2022 HM Revenue & Customs
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

import mocks.MockIdGenerator
import play.api.libs.json.Json
import play.api.mvc.Result
import uk.gov.hmrc.http.HeaderCarrier
import v3.fixtures.ListCalculationsFixture
import v3.mocks.hateoas.MockHateoasFactory
import v3.mocks.requestParsers.MockListCalculationsParser
import v3.mocks.services.{MockEnrolmentsAuthService, MockListCalculationsService, MockMtdIdLookupService}
import v3.models.domain.{Nino, TaxYear}
import v3.models.errors.{ErrorWrapper, MtdError, _}
import v3.models.hateoas.{HateoasWrapper, Link, Method, RelType}
import v3.models.outcomes.ResponseWrapper
import v3.models.request.{ListCalculationsRawData, ListCalculationsRequest}
import v3.models.response.listCalculations.{ListCalculationsHateoasData, ListCalculationsResponse}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ListCalculationsControllerSpec extends ControllerBaseSpec with ListCalculationsFixture {
  val defaultNino: String                     = "AA111111A"
  val defaultTaxYear: Option[String]          = Some("2018-19")
  val defaultRawData: ListCalculationsRawData = ListCalculationsRawData(defaultNino, defaultTaxYear)

  case class Test(rawData: ListCalculationsRawData = defaultRawData)
      extends MockEnrolmentsAuthService
      with MockMtdIdLookupService
      with MockListCalculationsParser
      with MockListCalculationsService
      with MockHateoasFactory
      with MockIdGenerator {

    val hc: HeaderCarrier     = HeaderCarrier()
    val correlationId: String = "a1e8057e-fbbc-47a8-a8b4-78d9f015c253"

    val nino: String            = rawData.nino
    val taxYear: Option[String] = rawData.taxYear

    lazy val request: ListCalculationsRequest = ListCalculationsRequest(
      nino = Nino(nino),
      taxYear = taxYear.fold(Option.empty[TaxYear])(taxYear => Some(TaxYear.fromMtd(taxYear)))
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

    MockedMtdIdLookupService.lookup(nino).returns(Future.successful(Right("test-mtd-id")))
    MockedEnrolmentsAuthService.authoriseUser()
    MockIdGenerator.getCorrelationId.returns(correlationId)
  }

  "ListCalculationsController" when {
    "a valid request is supplied" should {
      "return the expected result for a successful service response" in new Test {
        MockListCalculationsParser.parse(rawData).returns(Right(request))

        MockListCalculationsService
          .list(request)
          .returns(
            Future.successful(Right(ResponseWrapper(correlationId, listCalculationsResponseModel)))
          )

        MockHateoasFactory
          .wrapList(listCalculationsResponseModel, ListCalculationsHateoasData(nino, taxYear))
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
                  href = s"/individuals/calculations/$nino/self-assessment",
                  rel = RelType.SELF,
                  method = Method.GET
                )
              )
            )
          )

        val result: Future[Result] = controller.list(nino, taxYear)(fakeGetRequest)
        status(result) shouldBe OK
        contentAsJson(result) shouldBe listCalculationsMtdJsonWithHateoas(nino, taxYear.get)
        header("X-CorrelationId", result) shouldBe Some(correlationId)
      }

      "return the expected result for a service response with errors" should {
        def checkServiceError(mtdError: MtdError, expectedStatus: Int): Unit = {
          s"handle $mtdError correctly" in new Test {
            MockListCalculationsParser.parse(rawData).returns(Right(request))

            MockListCalculationsService
              .list(request)
              .returns(
                Future.successful(Left(ErrorWrapper(correlationId, mtdError)))
              )

            val result: Future[Result] = controller.list(nino, taxYear)(fakeGetRequest)
            status(result) shouldBe expectedStatus
            contentAsJson(result) shouldBe Json.toJson(mtdError)
            header("X-CorrelationId", result) shouldBe Some(correlationId)
          }
        }

        val serviceErrors = Map(
          NinoFormatError                   -> BAD_REQUEST,
          TaxYearFormatError                -> BAD_REQUEST,
          RuleTaxYearNotSupportedError      -> BAD_REQUEST,
          NotFoundError                     -> NOT_FOUND,
          InternalError                     -> INTERNAL_SERVER_ERROR,
          RuleIncorrectGovTestScenarioError -> BAD_REQUEST
        )

        serviceErrors.foreach(args => (checkServiceError _).tupled(args))
      }
    }

    "an invalid request is supplied" should {
      def checkParserError(rawRequestData: ListCalculationsRawData, mtdError: MtdError): Unit = {
        s"handle $mtdError correctly" in new Test(rawRequestData) {
          MockListCalculationsParser.parse(rawRequestData).returns(Left(ErrorWrapper(correlationId, mtdError)))

          val result: Future[Result] = controller.list(nino, taxYear)(fakeGetRequest)
          result shouldBe result
          status(result) shouldBe BAD_REQUEST
          contentAsJson(result) shouldBe Json.toJson(mtdError)
          header("X-CorrelationId", result) shouldBe Some(correlationId)
        }
      }

      val parserErrors = Map(
        ListCalculationsRawData("", None)                     -> NinoFormatError,
        ListCalculationsRawData("AA111111A", Some(""))        -> TaxYearFormatError,
        ListCalculationsRawData("AA111111A", Some("2018-20")) -> RuleTaxYearRangeInvalidError,
        ListCalculationsRawData("AA111111A", Some("2011-12")) -> RuleTaxYearNotSupportedError
      )

      parserErrors.foreach(args => (checkParserError _).tupled(args))
    }
  }

}
