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
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import uk.gov.hmrc.http.HeaderCarrier
import v3.mocks.hateoas.MockHateoasFactory
import v3.mocks.requestParsers.MockRetrieveCalculationParser
import v3.mocks.services.{MockAuditService, MockEnrolmentsAuthService, MockMtdIdLookupService, MockRetrieveCalculationService}
import v3.models.domain.{Nino, TaxYear}
import v3.models.errors._
import v3.models.hateoas.Method.GET
import v3.models.hateoas.{HateoasWrapper, Link}
import v3.models.outcomes.ResponseWrapper
import v3.models.request.{RetrieveCalculationRawData, RetrieveCalculationRequest}
import v3.models.response.common.CalculationType.`inYear`
import v3.models.response.retrieveCalculation.calculation.Calculation
import v3.models.response.retrieveCalculation.inputs.{IncomeSources, Inputs, PersonalInformation}
import v3.models.response.retrieveCalculation.messages.Messages
import v3.models.response.retrieveCalculation.metadata.Metadata
import v3.models.response.retrieveCalculation.{RetrieveCalculationHateoasData, RetrieveCalculationResponse}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RetrieveCalculationControllerSpec
    extends ControllerBaseSpec
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockRetrieveCalculationParser
    with MockHateoasFactory
    with MockRetrieveCalculationService
    with MockAuditService
    with MockIdGenerator {

  private val nino          = "AA123456A"
  private val taxYear       = "2017-18"
  private val calculationId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"
  private val correlationId = "X-123"

  // This is a filler model to make the branch compile
  val inputsModel: Inputs = Inputs(
    PersonalInformation("", None, "UK", None, None, None, None, None),
    IncomeSources(None, None),
    None,
    None,
    None,
    None,
    None,
    None,
    None
  )

  val response: RetrieveCalculationResponse = RetrieveCalculationResponse(
    metadata = Metadata(
      calculationId = "",
      taxYear = TaxYear.fromDownstream("2018"),
      requestedBy = "",
      requestedTimestamp = None,
      calculationReason = "",
      calculationTimestamp = None,
      calculationType = `inYear`,
      intentToSubmitFinalDeclaration = false,
      finalDeclaration = false,
      finalDeclarationTimestamp = None,
      periodFrom = "",
      periodTo = ""
    ),
    inputs = inputsModel,
    calculation = Some(
      Calculation(
        None,
        None,
        None,
        None,
        None,
        None,
        None,
        None,
        None,
        None,
        None,
        None,
        None,
        None,
        None,
        None,
        None,
        None,
        None,
        None,
        None,
        None,
        None,
        None,
        None,
        None,
        None,
        None
      )),
    messages = Some(Messages(None, None, None))
  )

  val json: JsValue = Json.parse(
    """
      |{
      |  "metadata" : {
      |    "calculationId": "",
      |    "taxYear": "2017-18",
      |    "requestedBy": "",
      |    "calculationReason": "",
      |    "calculationType": "inYear",
      |    "intentToSubmitFinalDeclaration": false,
      |    "finalDeclaration": false,
      |    "periodFrom": "",
      |    "periodTo": ""
      |  },
      |  "inputs" : {
      |    "personalInformation": {
      |       "identifier": "",
      |       "taxRegime": "UK"
      |    },
      |    "incomeSources": {}
      |  },
      |  "calculation" : {},
      |  "messages" : {
      |  },
      |  "links" : [
      |    {
      |      "href":"/foo/bar",
      |      "method":"GET",
      |      "rel":"test-relationship"
      |    }
      |  ]
      |}
    """.stripMargin
  )

  val rawData: RetrieveCalculationRawData     = RetrieveCalculationRawData(nino, taxYear, calculationId)
  val requestData: RetrieveCalculationRequest = RetrieveCalculationRequest(Nino(nino), taxYear, calculationId)
  val error: ErrorWrapper                     = ErrorWrapper(correlationId, RuleNoIncomeSubmissionsExistError, None)

  val testHateoasLink: Link = Link(href = "/foo/bar", method = GET, rel = "test-relationship")

  trait Test {
    val hc: HeaderCarrier = HeaderCarrier()

    val controller = new RetrieveCalculationController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      retrieveCalculationParser = mockRetrieveCalculationParser,
      service = mockService,
      cc = cc,
      hateoasFactory = mockHateoasFactory,
      idGenerator = mockIdGenerator
    )

    MockedMtdIdLookupService.lookup(nino).returns(Future.successful(Right("test-mtd-id")))
    MockedEnrolmentsAuthService.authoriseUser()
    MockIdGenerator.getCorrelationId.returns(correlationId)
  }

  "handleRequest" should {
    "return OK with the calculation" when {
      "happy path" in new Test {
        MockRetrieveCalculationParser
          .parse(rawData)
          .returns(Right(requestData))

        MockRetrieveCalculationService
          .retrieveCalculation(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        MockHateoasFactory
          .wrap(response, RetrieveCalculationHateoasData(nino, taxYear, calculationId, response))
          .returns(HateoasWrapper(response, Seq(testHateoasLink)))

        val result: Future[Result] = controller.retrieveCalculation(nino, taxYear, calculationId)(fakeGetRequest)

        status(result) shouldBe OK
        contentAsJson(result) shouldBe json
        header("X-CorrelationId", result) shouldBe Some(correlationId)
      }
    }

    "return the error as per spec" when {
      "parser errors occur" must {
        def errorsFromParserTester(error: MtdError, expectedStatus: Int): Unit = {
          s"a ${error.code} error is returned from the parser" in new Test {
            MockRetrieveCalculationParser.parse(rawData) returns Left(ErrorWrapper(correlationId, error, None))

            val result: Future[Result] = controller.retrieveCalculation(nino, taxYear, calculationId)(fakeGetRequest)

            contentAsJson(result) shouldBe Json.toJson(error)
            status(result) shouldBe expectedStatus
            header("X-CorrelationId", result) shouldBe Some(correlationId)
          }
        }

        val input = Seq(
          (BadRequestError, BAD_REQUEST),
          (NinoFormatError, BAD_REQUEST),
          (CalculationIdFormatError, BAD_REQUEST),
          (TaxYearFormatError, BAD_REQUEST),
          (RuleTaxYearNotSupportedError, BAD_REQUEST),
          (RuleTaxYearRangeInvalidError, BAD_REQUEST)
        )

        input.foreach(args => (errorsFromParserTester _).tupled(args))
      }

      "service errors occur" must {
        def serviceErrors(mtdError: MtdError, expectedStatus: Int): Unit = {
          s"a $mtdError error is returned from the service" in new Test {
            MockRetrieveCalculationParser.parse(rawData) returns Right(requestData)

            MockRetrieveCalculationService.retrieveCalculation(requestData) returns Future.successful(Left(ErrorWrapper(correlationId, mtdError)))

            val result: Future[Result] = controller.retrieveCalculation(nino, taxYear, calculationId)(fakeGetRequest)

            contentAsJson(result) shouldBe Json.toJson(mtdError)
            status(result) shouldBe expectedStatus
            header("X-CorrelationId", result) shouldBe Some(correlationId)
          }
        }

        val input = Seq(
          (NinoFormatError, BAD_REQUEST),
          (CalculationIdFormatError, BAD_REQUEST),
          (NotFoundError, NOT_FOUND),
          (DownstreamError, INTERNAL_SERVER_ERROR),
          (RuleIncorrectGovTestScenarioError, BAD_REQUEST)
        )

        input.foreach(args => (serviceErrors _).tupled(args))
      }

      "return a DownstreamError" when {
        object TestError
            extends MtdError(
              code = "TEST_ERROR",
              message = "This is a test error"
            )
        "the parser returns an unexpected error" in new Test {
          MockRetrieveCalculationParser
            .parse(rawData)
            .returns(Left(ErrorWrapper(correlationId, TestError, None)))

          val result: Future[Result] = controller.retrieveCalculation(nino, taxYear, calculationId)(fakeRequest)

          status(result) shouldBe INTERNAL_SERVER_ERROR
          contentAsJson(result) shouldBe Json.toJson(DownstreamError)
          header("X-CorrelationId", result) shouldBe Some(correlationId)
        }

        "the service returns an unexpected error" in new Test {
          MockRetrieveCalculationParser
            .parse(rawData)
            .returns(Right(requestData))

          MockRetrieveCalculationService
            .retrieveCalculation(requestData)
            .returns(Future.successful(Left(ErrorWrapper(correlationId, TestError))))

          val result: Future[Result] = controller.retrieveCalculation(nino, taxYear, calculationId)(fakeRequest)

          status(result) shouldBe INTERNAL_SERVER_ERROR
          contentAsJson(result) shouldBe Json.toJson(DownstreamError)
          header("X-CorrelationId", result) shouldBe Some(correlationId)
        }
      }
    }
  }

}
