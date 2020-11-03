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

import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.mvc.Result
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.http.HeaderCarrier
import utils.Logging
import v1.fixtures.getMessages.MessagesResponseFixture._
import v1.handler.{RequestDefn, RequestHandler}
import v1.mocks.MockIdGenerator
import v1.mocks.hateoas.MockHateoasFactory
import v1.mocks.requestParsers.MockGetCalculationQueryParser
import v1.mocks.services.{MockAuditService, MockEnrolmentsAuthService, MockMtdIdLookupService, MockStandardService}
import v1.models.audit.{AuditError, AuditEvent, AuditResponse, GenericAuditDetail}
import v1.models.domain.MessageType
import v1.models.errors._
import v1.models.hateoas.Method.GET
import v1.models.hateoas.{HateoasWrapper, Link}
import v1.models.outcomes.ResponseWrapper
import v1.models.request.{GetMessagesRawData, GetMessagesRequest}
import v1.models.response.getMessages.{MessagesHateoasData, MessagesResponse}
import v1.support.BackendResponseMappingSupport

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class GetMessagesControllerSpec
    extends ControllerBaseSpec
      with MockEnrolmentsAuthService
      with MockMtdIdLookupService
      with MockGetCalculationQueryParser
      with MockStandardService
      with MockHateoasFactory
      with MockAuditService
      with MockIdGenerator {

  trait Test {
    val hc: HeaderCarrier = HeaderCarrier()

    val controller = new GetMessagesController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      parser = mockGetCalculationQueryParser,
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

  private val nino          = "AA123456A"
  private val correlationId = "X-123"

  def messagesResponse(info: Boolean, warn: Boolean, error: Boolean): MessagesResponse =
    MessagesResponse(if (info) Some(Seq(info1, info2)) else None,
                        if (warn) Some(Seq(warn1, warn2)) else None,
                        if (error) Some(Seq(err1, err2)) else None, calcId)

  val hateoasLinks: JsValue = Json.parse("""{
      |      "links":[
      |        {
      |          "href":"/foo/bar",
      |          "method":"GET",
      |          "rel":"test-relationship"
      |         }
      |      ]
      |}""".stripMargin)

  val responseBody: JsValue         = messagesResponseJson.as[JsObject].deepMerge(hateoasLinks.as[JsObject])

  private val rawData     = GetMessagesRawData(nino, calcId, Seq("info", "warning", "error"))
  private val typeQueries = Seq(MessageType.toTypeClass("info"), MessageType.toTypeClass("error"), MessageType.toTypeClass("warning"))
  private val requestData = GetMessagesRequest(Nino(nino), calcId, typeQueries)
  val testHateoasLink     = Link(href = "/foo/bar", method = GET, rel = "test-relationship")

  private def uri      = s"/$nino/self-assessment/$calcId"
  private def queryUri = "/input/uri?type=info&type=warning&type=error"

  "handleRequest" should {
    "return OK the calculation messages" when {
      "happy path" in new Test {
        MockGetCalculationQueryParser
          .parse(rawData)
          .returns(Right(requestData))

        val response: MessagesResponse = messagesResponse(info = true, warn = true, error = true)

        MockStandardService
          .doService(RequestDefn.Get(uri), OK)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        MockHateoasFactory
          .wrap(response, MessagesHateoasData(nino, calcId))
          .returns(HateoasWrapper(response, Seq(testHateoasLink)))

        val result: Future[Result] = controller.getMessages(nino, calcId)(fakeGetRequest(queryUri))

        status(result) shouldBe OK
        contentAsJson(result) shouldBe responseBody
        header("X-CorrelationId", result) shouldBe Some(correlationId)

        val detail = GenericAuditDetail(
          "Individual", None, Map("nino" -> nino, "calculationId" -> calcId), None, correlationId,
          AuditResponse(OK, None, Some(responseBody)))
        val event = AuditEvent("retrieveSelfAssessmentTaxCalculationMessages", "retrieve-self-assessment-tax-calculation-messages", detail)
        MockedAuditService.verifyAuditEvent(event).once
      }
    }

    "return NOT_FOUND (NO_MESSAGES_PRESENT)" when {
      "there are no messages" in new Test {
        MockGetCalculationQueryParser
          .parse(rawData)
          .returns(Right(requestData))

        val response: MessagesResponse = messagesResponse(info = false, warn = false, error = false)

        MockStandardService
          .doService(RequestDefn.Get(uri), OK)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        val result: Future[Result] = controller.getMessages(nino, calcId)(fakeGetRequest(queryUri))

        status(result) shouldBe NOT_FOUND
        contentAsJson(result) shouldBe Json.toJson(NoMessagesExistError)
        header("X-CorrelationId", result) shouldBe Some(correlationId)

        val detail = GenericAuditDetail(
          "Individual", None, Map("nino" -> nino, "calculationId" -> calcId), None, correlationId,
          AuditResponse(NOT_FOUND, Some(Seq(AuditError(NoMessagesExistError.code))), None))
        val event = AuditEvent("retrieveSelfAssessmentTaxCalculationMessages", "retrieve-self-assessment-tax-calculation-messages", detail)
        MockedAuditService.verifyAuditEvent(event).once
      }
    }

    "map service error mapping according to spec" in new Test with BackendResponseMappingSupport with Logging {
      MockGetCalculationQueryParser
        .parse(rawData)
        .returns(Right(requestData))

      import controller.endpointLogContext

      val mappingChecks: RequestHandler[MessagesResponse, MessagesResponse] => Unit = allChecks[MessagesResponse, MessagesResponse](
        ("FORMAT_NINO", BAD_REQUEST, NinoFormatError, BAD_REQUEST),
        ("FORMAT_CALC_ID", BAD_REQUEST, CalculationIdFormatError, BAD_REQUEST),
        ("MATCHING_RESOURCE_NOT_FOUND", NOT_FOUND, NotFoundError, NOT_FOUND),
        ("INTERNAL_SERVER_ERROR", INTERNAL_SERVER_ERROR, DownstreamError, INTERNAL_SERVER_ERROR)
      )

      val response: MessagesResponse = messagesResponse(info = true, warn = true, error = true)

      MockStandardService
        .doServiceWithMappings(mappingChecks)
        .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

      MockHateoasFactory
        .wrap(response, MessagesHateoasData(nino, calcId))
        .returns(HateoasWrapper(response, Seq(testHateoasLink)))

      val result: Future[Result] = controller.getMessages(nino, calcId)(fakeGetRequest(queryUri))

      header("X-CorrelationId", result) shouldBe Some(correlationId)
    }
  }
}
