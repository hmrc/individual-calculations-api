/*
 * Copyright 2020 HM Revenue & Customs
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

package v1.controllers

import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.http.HeaderCarrier
import utils.Logging
import v1.handler.{RequestDefn, RequestHandler}
import v1.mocks.hateoas.MockHateoasFactory
import v1.mocks.requestParsers.MockGetCalculationParser
import v1.mocks.services.{MockAuditService, MockEnrolmentsAuthService, MockMtdIdLookupService, MockStandardService}
import v1.models.audit.{AuditError, AuditEvent, AuditResponse, GenericAuditDetail}
import v1.models.errors._
import v1.models.hateoas.Method.GET
import v1.models.hateoas.{HateoasWrapper, Link}
import v1.models.outcomes.ResponseWrapper
import v1.models.request.{GetCalculationRawData, GetCalculationRequest}
import v1.models.response.common.{CalculationReason, CalculationRequestor, CalculationType}
import v1.models.response.getMetadata.{MetadataResponse, MetadataHateoasData}
import v1.support.BackendResponseMappingSupport

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class GetMetadataControllerSpec
    extends ControllerBaseSpec
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockGetCalculationParser
    with MockStandardService
    with MockHateoasFactory
    with MockAuditService{

  trait Test {
    val hc = HeaderCarrier()

    val controller = new GetMetadataController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      parser = mockGetCalculationParser,
      service = mockStandardService,
      hateoasFactory = mockHateoasFactory,
      auditService = mockAuditService,
      cc = cc
    )

    MockedMtdIdLookupService.lookup(nino).returns(Future.successful(Right("test-mtd-id")))
    MockedEnrolmentsAuthService.authoriseUser()
  }

  private val nino          = "AA123456A"
  private val calcId        = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"
  private val correlationId = "X-123"

  val responseBody: JsValue = Json.parse("""
      |{
      |    "id": "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
      |    "taxYear": "2018-19",
      |    "requestedBy": "customer",
      |    "calculationReason": "customerRequest",
      |    "calculationTimestamp": "2019-11-15T09:35:15.094Z",
      |    "calculationType": "crystallisation",
      |    "intentToCrystallise": true,
      |    "crystallised": false,
      |    "links": [
      |      {
      |       "href": "/foo/bar",
      |       "method": "GET",
      |       "rel": "test-relationship"
      |      }
      |    ]
      |}""".stripMargin)

  val response = MetadataResponse(
    id = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
    taxYear = "2018-19",
    requestedBy = CalculationRequestor.customer,
    calculationReason = CalculationReason.customerRequest,
    calculationTimestamp = Some("2019-11-15T09:35:15.094Z"),
    calculationType = CalculationType.crystallisation,
    intentToCrystallise = true,
    crystallised = false,
    totalIncomeTaxAndNicsDue = None,
    calculationErrorCount = None
  )

  val error: ErrorWrapper = ErrorWrapper(Some(correlationId), MtdErrors(NOT_FOUND, NotFoundError))

  private val rawData     = GetCalculationRawData(nino, calcId)
  private val requestData = GetCalculationRequest(Nino(nino), calcId)

  private def uri = s"/$nino/self-assessment/$calcId"
  private def queryUri = "/input/uri"

  val testHateoasLink = Link(href = "/foo/bar", method = GET, rel = "test-relationship")

  "handleRequest" should {
    "return OK the calculation metadata" when {
      "happy path" in new Test {
        MockGetCalculationParser
          .parse(rawData)
          .returns(Right(requestData))

        MockStandardService
          .doService(RequestDefn.Get(uri), OK)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        MockHateoasFactory
          .wrap(response, MetadataHateoasData(nino, calcId, None))
          .returns(HateoasWrapper(response, Seq(testHateoasLink)))

        val result: Future[Result] = controller.getMetadata(nino, calcId)(fakeGetRequest(queryUri))

        status(result) shouldBe OK
        contentAsJson(result) shouldBe responseBody
        header("X-CorrelationId", result) shouldBe Some(correlationId)

        val detail = GenericAuditDetail(
          "Individual", None, Map("nino" -> nino, "calculationId" -> calcId), None, correlationId,
          AuditResponse(OK, None, Some(responseBody)))
        val event = AuditEvent("retrieveSelfAssessmentTaxCalculationMetadata", "retrieve-self-assessment-tax-calculation-metadata", detail)
        MockedAuditService.verifyAuditEvent(event).once
      }
    }

    "return NOT_FOUND with the correct error message" when {
      "matching resource not found" in new Test {
        MockGetCalculationParser
          .parse(rawData)
          .returns(Right(requestData))

        MockStandardService
          .doService(RequestDefn.Get(uri), OK)
          .returns(Future.successful(Left(error)))

        val result: Future[Result] = controller.getMetadata(nino, calcId)(fakeGetRequest(queryUri))

        status(result) shouldBe NOT_FOUND
        contentAsJson(result) shouldBe Json.toJson(NotFoundError)
        header("X-CorrelationId", result) shouldBe Some(correlationId)

        val detail = GenericAuditDetail(
          "Individual", None, Map("nino" -> nino, "calculationId" -> calcId), None, correlationId,
          AuditResponse(NOT_FOUND, Some(List(AuditError(NotFoundError.code))), None))
        val event = AuditEvent("retrieveSelfAssessmentTaxCalculationMetadata", "retrieve-self-assessment-tax-calculation-metadata", detail)
        MockedAuditService.verifyAuditEvent(event).once
      }
    }

    "map service error mapping according to spec" in new Test with BackendResponseMappingSupport with Logging {
      MockGetCalculationParser
        .parse(rawData)
        .returns(Right(requestData))

      import controller.endpointLogContext

      val mappingChecks: RequestHandler[MetadataResponse, MetadataResponse] => Unit = allChecks[MetadataResponse, MetadataResponse](
        ("FORMAT_NINO", BAD_REQUEST, NinoFormatError, BAD_REQUEST),
        ("FORMAT_CALC_ID", BAD_REQUEST, CalculationIdFormatError, BAD_REQUEST),
        ("MATCHING_RESOURCE_NOT_FOUND", NOT_FOUND, NotFoundError, NOT_FOUND),
        ("INTERNAL_SERVER_ERROR", INTERNAL_SERVER_ERROR, DownstreamError, INTERNAL_SERVER_ERROR)
      )

      MockStandardService
        .doServiceWithMappings(mappingChecks)
        .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

      MockHateoasFactory
        .wrap(response, MetadataHateoasData(nino, calcId, None))
        .returns(HateoasWrapper(response, Seq(testHateoasLink)))

      val result: Future[Result] = controller.getMetadata(nino, calcId)(fakeGetRequest(queryUri))

      header("X-CorrelationId", result) shouldBe Some(correlationId)
    }
  }
}
