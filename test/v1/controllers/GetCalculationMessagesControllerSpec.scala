/*
 * Copyright 2019 HM Revenue & Customs
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

import play.api.libs.json.JsValue
import play.api.mvc.Result
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.http.HeaderCarrier
import utils.Logging
import v1.fixtures.Fixtures._
import v1.handling.{RequestDefn, RequestHandling}
import v1.mocks.requestParsers.MockGetCalculationQueryParser
import v1.mocks.services.{MockEnrolmentsAuthService, MockMtdIdLookupService, MockStandardService}
import v1.models.errors._
import v1.models.outcomes.ResponseWrapper
import v1.models.request.{GetCalculationMessagesRawData, GetCalculationMessagesRequest, MessageType}
import v1.models.response.getCalculationMessages.CalculationMessages
import v1.support.BackendResponseMappingSupport

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class GetCalculationMessagesControllerSpec
    extends ControllerBaseSpec
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockGetCalculationQueryParser
    with MockStandardService {

  trait Test {
    val hc = HeaderCarrier()

    val controller = new GetCalculationMessagesController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      parser = mockGetCalculationQueryParser,
      service = mockStandardService,
      cc = cc
    )

    MockedMtdIdLookupService.lookup(nino).returns(Future.successful(Right("test-mtd-id")))
    MockedEnrolmentsAuthService.authoriseUser()
  }

  private val nino          = "AA123456A"
  private val calcId        = "someCalcId"
  private val correlationId = "X-123"

  def messagesResponse(info: Boolean, warn: Boolean, error: Boolean): CalculationMessages =
    CalculationMessages(if (info) Some(Seq(info1,info2)) else None, if (warn) Some(Seq(warn1,warn2)) else None, if (error) Some(Seq(err1,err2)) else None)

  val responseBody: JsValue = outputMessagesJson
  val response: CalculationMessages = messagesResponse(info = true,warn = true,error = true)

  private val rawData     = GetCalculationMessagesRawData(nino, calcId, Seq("info","warning","error"))
  private val typeQueries = Seq(MessageType.toTypeClass("info"), MessageType.toTypeClass("error"), MessageType.toTypeClass("warning"))
  private val requestData = GetCalculationMessagesRequest(Nino(nino), calcId, typeQueries)

  private def uri = "/input/uri"
  private def queryUri = "/input/uri?type=info&type=warning&type=error"

  "handleRequest" should {
    "return OK the calculation messages" when {
      "happy path" in new Test {
        MockGetCalculationQueryParser
          .parse(rawData)
          .returns(Right(requestData))

        MockStandardService
          .doService(RequestDefn.Get(uri), OK)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        val result: Future[Result] = controller.getMessages(nino, calcId)(fakeGetRequest(queryUri))

        status(result) shouldBe OK
        contentAsJson(result) shouldBe responseBody
        header("X-CorrelationId", result) shouldBe Some(correlationId)
      }
    }

    "map service error mapping according to spec" in new Test with BackendResponseMappingSupport with Logging {
      MockGetCalculationQueryParser
        .parse(rawData)
        .returns(Right(requestData))

      import controller.endpointLogContext

      val mappingChecks: RequestHandling[CalculationMessages, CalculationMessages] => Unit = allChecks[CalculationMessages, CalculationMessages](
        ("FORMAT_NINO", BAD_REQUEST, NinoFormatError, BAD_REQUEST),
        ("FORMAT_CALC_ID", BAD_REQUEST, CalculationIdFormatError, BAD_REQUEST),
        ("MATCHING_RESOURCE_NOT_FOUND", NOT_FOUND, NotFoundError, NOT_FOUND),
        ("INTERNAL_SERVER_ERROR", INTERNAL_SERVER_ERROR, DownstreamError, INTERNAL_SERVER_ERROR)
      )

      MockStandardService
        .doServiceWithMappings(mappingChecks)
        .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

      val result: Future[Result] = controller.getMessages(nino, calcId)(fakeGetRequest(queryUri))

      header("X-CorrelationId", result) shouldBe Some(correlationId)
    }
  }
}
