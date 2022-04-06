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
import utils.Logging
import v3.handler.{RequestDefn, RequestHandler}
import v3.mocks.hateoas.MockHateoasFactory
import v3.mocks.requestParsers.MockRetrieveCalculationParser
import v3.mocks.services.{MockAuditService, MockEnrolmentsAuthService, MockMtdIdLookupService, MockStandardService}
import v3.models.domain.Nino
import v3.models.errors._
import v3.models.hateoas.Method.GET
import v3.models.hateoas.{HateoasWrapper, Link}
import v3.models.outcomes.ResponseWrapper
import v3.models.request.{RetrieveCalculationRawData, RetrieveCalculationRequest}
import v3.models.response.retrieveCalculation.calculation.Calculation
import v3.models.response.retrieveCalculation.inputs.{IncomeSources, Inputs, PersonalInformation}
import v3.models.response.retrieveCalculation.messages.Messages
import v3.models.response.retrieveCalculation.metadata.Metadata
import v3.models.response.retrieveCalculation.{RetrieveCalculationHateoasData, RetrieveCalculationResponse}
import v3.support.BackendResponseMappingSupport

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RetrieveCalculationControllerSpec
    extends ControllerBaseSpec
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockRetrieveCalculationParser
    with MockHateoasFactory
    with MockStandardService
    with MockAuditService
    with MockIdGenerator {

  private val nino          = "AA123456A"
  private val taxYear       = "2017-18"
  private val calculationId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"
  private val correlationId = "X-123"

  // This is a filler model to make the branch compile
  val inputsModel: Inputs = Inputs(
    PersonalInformation("", None, "", None, None, None, None, None),
    IncomeSources(None, None),
    None, None, None, None, None, None, None
  )

  val response: RetrieveCalculationResponse =
    RetrieveCalculationResponse(metadata = Metadata(""), inputs = inputsModel, calculation = Some(Calculation("")), messages = Some(Messages("")))

  val json: JsValue = Json.parse(
    """
      |{
      |  "metadata" : {
      |    "field": ""
      |  },
      |  "inputs" : {
      |    "field": ""
      |  },
      |  "calculation" : {
      |    "field": ""
      |  },
      |  "messages" : {
      |    "field": ""
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
  val error: ErrorWrapper                     = ErrorWrapper(correlationId, RuleNoIncomeSubmissionsExistError, None, FORBIDDEN)

  val testHateoasLink: Link = Link(href = "/foo/bar", method = GET, rel = "test-relationship")

  private def uri = s"/income-tax/view/calculations/liability/$nino/$calculationId"

  trait Test {
    val hc: HeaderCarrier = HeaderCarrier()

    val controller = new RetrieveCalculationController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      retrieveCalculationParser = mockRetrieveCalculationParser,
      service = mockStandardService,
      cc = cc,
      auditService = mockAuditService,
      hateoasFactory = mockHateoasFactory,
      idGenerator = mockIdGenerator
    )

    MockedMtdIdLookupService.lookup(nino).returns(Future.successful(Right("test-mtd-id")))
    MockedEnrolmentsAuthService.authoriseUser()
    MockIdGenerator.getCorrelationId.returns(correlationId)
  }

  "handleRequest" should {
    "return ACCEPTED with list of calculations" when {
      "happy path" in new Test {
        MockRetrieveCalculationParser
          .parse(rawData)
          .returns(Right(requestData))

        MockStandardService
          .doService(RequestDefn.Get(uri), OK)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        MockHateoasFactory
          .wrap(response, RetrieveCalculationHateoasData(nino, taxYear, calculationId))
          .returns(HateoasWrapper(response, Seq(testHateoasLink)))

        val result: Future[Result] = controller.retrieveCalculation(nino, taxYear, calculationId)(fakeGetRequest(""))

        status(result) shouldBe OK
        contentAsJson(result) shouldBe json
        header("X-CorrelationId", result) shouldBe Some(correlationId)
      }
    }

    "return FORBIDDEN with the correct error message" when {
      "no income submissions exist" in new Test {
        MockRetrieveCalculationParser
          .parse(rawData)
          .returns(Right(requestData))

        MockStandardService
          .doService(RequestDefn.Get(uri), OK)
          .returns(Future.successful(Left(error)))

        val result: Future[Result] = controller.retrieveCalculation(nino, taxYear, calculationId)(fakeGetRequest(""))

        status(result) shouldBe FORBIDDEN
        contentAsJson(result) shouldBe Json.toJson(RuleNoIncomeSubmissionsExistError)
        header("X-CorrelationId", result) shouldBe Some(correlationId)
      }
    }

    "map service error mapping according to spec" in new Test with BackendResponseMappingSupport with Logging {
      MockRetrieveCalculationParser
        .parse(rawData)
        .returns(Right(requestData))

      import controller.endpointLogContext

      val mappingChecks: RequestHandler[RetrieveCalculationResponse, RetrieveCalculationResponse] => Unit =
        allChecks[RetrieveCalculationResponse, RetrieveCalculationResponse](
          ("INVALID_TAXABLE_ENTITY_ID", BAD_REQUEST, NinoFormatError, BAD_REQUEST),
          ("INVALID_CALCULATION_ID", BAD_REQUEST, CalculationIdFormatError, BAD_REQUEST),
          ("INVALID_CORRELATIONID", BAD_REQUEST, DownstreamError, INTERNAL_SERVER_ERROR),
          ("INVALID_CONSUMERID", BAD_REQUEST, DownstreamError, INTERNAL_SERVER_ERROR),
          ("NO_DATA_FOUND", NOT_FOUND, NotFoundError, NOT_FOUND),
          ("SERVER_ERROR", INTERNAL_SERVER_ERROR, DownstreamError, INTERNAL_SERVER_ERROR),
          ("SERVICE_UNAVAILABLE", SERVICE_UNAVAILABLE, DownstreamError, INTERNAL_SERVER_ERROR)
        )

      MockStandardService
        .doServiceWithMappings(mappingChecks)
        .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

      MockHateoasFactory
        .wrap(response, RetrieveCalculationHateoasData(nino, taxYear, calculationId))
        .returns(HateoasWrapper(response, Seq(testHateoasLink)))

      val result: Future[Result] = controller.retrieveCalculation(nino, taxYear, calculationId)(fakeGetRequest(""))

      header("X-CorrelationId", result) shouldBe Some(correlationId)
    }
  }

}
