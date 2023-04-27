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

package v2.controllers

import api.mocks.MockIdGenerator
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import uk.gov.hmrc.http.HeaderCarrier
import utils.Logging
import v2.handler.RequestDefn
import v2.mocks.hateoas.MockHateoasFactory
import v2.mocks.requestParsers.MockListCalculationsParser
import v2.mocks.services.{MockAuditService, MockEnrolmentsAuthService, MockMtdIdLookupService, MockStandardService}
import v2.models.domain.Nino
import v2.models.errors._
import v2.models.hateoas.Method.{GET, POST}
import v2.models.hateoas.{HateoasWrapper, Link}
import v2.models.outcomes.ResponseWrapper
import v2.models.request.{ListCalculationsRawData, ListCalculationsRequest}
import v2.models.response.common.{CalculationRequestor, CalculationType}
import v2.models.response.listCalculations.{CalculationListItem, ListCalculationsHateoasData, ListCalculationsResponse}
import v2.support.BackendResponseMappingSupport

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ListCalculationsControllerSpec
    extends ControllerBaseSpec
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockListCalculationsParser
    with MockHateoasFactory
    with MockStandardService
    with MockAuditService
    with MockIdGenerator {

  private val nino          = "AA123456A"
  private val taxYear       = "2017-18"
  private val correlationId = "X-123"

  val responseBody: JsValue = Json.parse(
    """
      |{
      |  "calculations": [
      |    {
      |      "id": "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
      |      "calculationTimestamp": "2019-03-17T09:22:59Z",
      |      "type": "inYear",
      |      "requestedBy": "hmrc",
      |      "links" : [
      |         {
      |           "href": "/foo/bar",
      |           "method": "GET",
      |           "rel": "test-item-relationship"
      |         }
      |      ]
      |    }
      |  ],
      |  "links" : [
      |     {
      |       "href": "/foo/bar",
      |       "method": "POST",
      |       "rel": "test-relationship"
      |     }
      |  ]
      |}
    """.stripMargin
  )

  val response: ListCalculationsResponse[CalculationListItem] = ListCalculationsResponse(
    Seq(
      CalculationListItem(
        id = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
        calculationTimestamp = "2019-03-17T09:22:59Z",
        `type` = CalculationType.inYear,
        requestedBy = Some(CalculationRequestor.hmrc)
      )
    ))

  val testItemHateoasLink: Link = Link(href = "/foo/bar", method = GET, rel = "test-item-relationship")
  val testHateoasLink: Link     = Link(href = "/foo/bar", method = POST, rel = "test-relationship")

  val hateoasResponse: ListCalculationsResponse[HateoasWrapper[CalculationListItem]] = ListCalculationsResponse(
    Seq(
      HateoasWrapper(
        CalculationListItem(
          id = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
          calculationTimestamp = "2019-03-17T09:22:59Z",
          `type` = CalculationType.inYear,
          requestedBy = Some(CalculationRequestor.hmrc)
        ),
        Seq(testItemHateoasLink)
      )))

  private val rawData     = ListCalculationsRawData(nino, Some(taxYear))
  private val requestData = ListCalculationsRequest(Nino(nino), taxYear)

  private def uri = "/input/uri"

  trait Test {
    val hc: HeaderCarrier = HeaderCarrier()

    val controller = new ListCalculationsController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      listCalculationsParser = mockListCalculationsParser,
      service = mockStandardService,
      hateoasFactory = mockHateoasFactory,
      auditService = mockAuditService,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    MockedMtdIdLookupService.lookup(nino).returns(Future.successful(Right("test-mtd-id")))
    MockedEnrolmentsAuthService.authoriseUser()
    MockIdGenerator.generateCorrelationId.returns(correlationId)
  }

  "handleRequest" should {
    "return OK with list of calculations" when {
      "happy path" in new Test {
        MockListCalculationsParser
          .parse(rawData)
          .returns(Right(requestData))

        MockStandardService
          .doService(RequestDefn.Get(uri).withParams("taxYear" -> taxYear), OK)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        MockHateoasFactory
          .wrapList(response, ListCalculationsHateoasData(nino))
          .returns(HateoasWrapper(hateoasResponse, Seq(testHateoasLink)))

        val result: Future[Result] = controller.listCalculations(nino, Some(taxYear))(fakeGetRequest(uri))

        status(result) shouldBe OK
        contentAsJson(result) shouldBe responseBody
        header("X-CorrelationId", result) shouldBe Some(correlationId)
      }
    }

    "return 404 NotFoundError" when {
      "an empty is is returned from the back end" in new Test {
        MockListCalculationsParser
          .parse(rawData)
          .returns(Right(requestData))

        MockStandardService
          .doService(RequestDefn.Get(uri).withParams("taxYear" -> taxYear), OK)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, ListCalculationsResponse(Nil)))))

        val result: Future[Result] = controller.listCalculations(nino, Some(taxYear))(fakeGetRequest(uri))

        status(result) shouldBe NOT_FOUND
        contentAsJson(result) shouldBe Json.toJson(NotFoundError)
        header("X-CorrelationId", result) shouldBe Some(correlationId)
      }
    }

    "map service error mapping according to spec" in new Test with BackendResponseMappingSupport with Logging {
      MockListCalculationsParser
        .parse(rawData)
        .returns(Right(requestData))

      import controller.endpointLogContext

      private val mappingChecks = allChecks[ListCalculationsResponse[CalculationListItem], ListCalculationsResponse[CalculationListItem]](
        ("FORMAT_NINO", BAD_REQUEST, NinoFormatError, BAD_REQUEST),
        ("FORMAT_TAX_YEAR", BAD_REQUEST, TaxYearFormatError, BAD_REQUEST),
        ("RULE_TAX_YEAR_NOT_SUPPORTED", BAD_REQUEST, RuleTaxYearNotSupportedError, BAD_REQUEST),
        ("RULE_TAX_YEAR_RANGE_INVALID", BAD_REQUEST, RuleTaxYearRangeInvalidError, BAD_REQUEST),
        ("MATCHING_RESOURCE_NOT_FOUND", NOT_FOUND, NotFoundError, NOT_FOUND),
        ("INTERNAL_SERVER_ERROR", INTERNAL_SERVER_ERROR, DownstreamError, INTERNAL_SERVER_ERROR),
        ("RULE_INCORRECT_GOV_TEST_SCENARIO", BAD_REQUEST, RuleIncorrectGovTestScenarioError, BAD_REQUEST)
      )

      MockStandardService
        .doServiceWithMappings(mappingChecks)
        .returns(Future.successful(Right(ResponseWrapper(correlationId, ListCalculationsResponse(Nil)))))

      val result: Future[Result] = controller.listCalculations(nino, Some(taxYear))(fakeGetRequest(uri))

      header("X-CorrelationId", result) shouldBe Some(correlationId)
    }
  }

}
