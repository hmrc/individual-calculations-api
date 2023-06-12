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

import api.mocks.MockIdGenerator
import cats.implicits._
import org.scalamock.handlers.CallHandler
import play.api.libs.json._
import play.api.mvc.{Action, AnyContent, Request, Result}
import uk.gov.hmrc.http.HeaderCarrier
import v4.connectors.httpparsers.StandardHttpParser
import v4.connectors.httpparsers.StandardHttpParser.SuccessCode
import v4.controllers.requestParsers.RequestParser
import v4.handler.RequestDefn.Get
import v4.handler.{AuditHandler, RequestHandler}
import v4.mocks.services.{MockAuditService, MockEnrolmentsAuthService, MockMtdIdLookupService, MockStandardService}
import v4.models.audit.{AuditError, AuditEvent, AuditResponse, GenericAuditDetail}
import v4.models.errors._
import v4.models.outcomes.ResponseWrapper
import v4.models.request.RawData

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class StandardControllerSpec
    extends ControllerBaseSpec
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockStandardService
    with MockAuditService
    with MockIdGenerator {

  case class Raw(data: String) extends RawData
  case class RequestData(data: String)
  case class BackendResp(data: String)

  object BackendResp {
    implicit val reads: Reads[BackendResp] = Json.reads[BackendResp]
  }

  case class APIResp(data: String)

  object APIResp {
    implicit val writes: Writes[APIResp] = Json.writes[APIResp]
  }

  val mockParser: RequestParser[Raw, RequestData] = mock[RequestParser[Raw, RequestData]]

  object MockParser {

    def parse(data: Raw): CallHandler[Either[ErrorWrapper, RequestData]] = {
      (mockParser.parseRequest(_: Raw)(_: String)).expects(data, *)
    }

  }

  trait Test extends MockEnrolmentsAuthService with MockMtdIdLookupService {
    def uri: String              = "/input/uri"
    val correlationId            = "X-123"
    val nino: String             = "nino"
    val rawData: Raw             = Raw("data")
    val requestData: RequestData = RequestData("data")

    val response: BackendResp   = BackendResp("data")
    val mappedResponse: APIResp = APIResp("dataMapped")
    val requestDefn: Get        = Get("url")

    val hc: HeaderCarrier = HeaderCarrier()

    MockedMtdIdLookupService
      .lookup(nino)
      .returns(Future.successful(Right("mtdId")))

    MockedEnrolmentsAuthService.authoriseUser()

    MockIdGenerator.generateCorrelationId.returns(correlationId)

    class TestController
        extends StandardController[Raw, RequestData, BackendResp, APIResp, AnyContent](
          authService = mockEnrolmentsAuthService,
          lookupService = mockMtdIdLookupService,
          parser = mockParser,
          service = mockStandardService,
          auditService = mockAuditService,
          cc = cc,
          idGenerator = mockIdGenerator
        ) {
      override implicit val endpointLogContext: EndpointLogContext = EndpointLogContext("standard", "standard")

      override def requestHandlerFor(playRequest: Request[AnyContent], req: RequestData): RequestHandler.Impl[BackendResp, APIResp] = {
        RequestHandler[BackendResp](requestDefn)
          .mapSuccess(_.map(_ => mappedResponse).asRight)
      }

      override val successCode: StandardHttpParser.SuccessCode = SuccessCode(OK)

      def handleRequest(nino: String): Action[AnyContent] =
        authorisedAction(nino).async { implicit request =>
          val rawData = Raw(nino)
          doHandleRequest(rawData)(request)
        }

    }

    val controller = new TestController

    val controllerWithAudit: TestController = new TestController {

      override def handleRequest(nino: String): Action[AnyContent] =
        authorisedAction(nino).async { implicit request =>
          val rawData = Raw(nino)

          val auditHandling = AuditHandler(
            "auditType",
            "txName",
            detailFactory = (correlationId: String, auditResponse: AuditResponse) =>
              GenericAuditDetail(request.userDetails, Map(), None, correlationId, auditResponse)
          )

          doHandleRequest(rawData, Some(auditHandling))(request)
        }

    }

  }

  "handleRequest" when {
    "success" should {
      "return success result" in new Test {
        MockParser
          .parse(Raw(nino))
          .returns(Right(requestData))

        MockStandardService
          .doService(requestDefn, OK)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        val result: Future[Result] = controller.handleRequest(nino)(fakeGetRequest(uri))

        status(result) shouldBe OK
        contentAsJson(result) shouldBe Json.toJson(mappedResponse)
        header("X-CorrelationId", result) shouldBe Some(correlationId)
      }

      "perform auditing if required" in new Test {
        MockParser
          .parse(Raw(nino))
          .returns(Right(requestData))

        MockStandardService
          .doService(requestDefn, OK)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        val result: Future[Result] = controllerWithAudit.handleRequest(nino)(fakeGetRequest(uri))

        status(result) shouldBe OK
        val responseBody: JsValue = Json.toJson(mappedResponse)
        contentAsJson(result) shouldBe responseBody

        val detail: GenericAuditDetail =
          GenericAuditDetail("Individual", None, Map(), None, "X-123", AuditResponse(200, None, Some(Json.parse("""{"data":"dataMapped"}"""))))

        val event: AuditEvent[GenericAuditDetail] = AuditEvent("auditType", "txName", detail)
        MockedAuditService.verifyAuditEvent(event).once()
      }
    }

    "parser errors occurs" should {
      "return the error as per spec" in new Test {
        // WLOG
        val statusCode: Int = BAD_REQUEST
        val error: MtdError = NinoFormatError

        MockParser
          .parse(Raw(nino))
          .returns(Left(ErrorWrapper(correlationId, error, None, statusCode)))

        val result: Future[Result] = controller.handleRequest(nino)(fakeGetRequest(uri))

        status(result) shouldBe statusCode
        contentAsJson(result) shouldBe Json.toJson(error)
        header("X-CorrelationId", result) shouldBe Some(correlationId)
      }
    }

    "service errors occur" when {
      "return the errors" in new Test {
        // WLOG
        val statusCode: Int = NOT_FOUND

        MockParser
          .parse(Raw(nino))
          .returns(Right(requestData))

        MockStandardService
          .doService(requestDefn, OK)
          .returns(Future.successful(Left(ErrorWrapper(correlationId, NotFoundError, None, statusCode))))

        val result: Future[Result] = controller.handleRequest(nino)(fakeGetRequest(uri))

        status(result) shouldBe statusCode
        contentAsJson(result) shouldBe Json.toJson(NotFoundError)
        header("X-CorrelationId", result) shouldBe Some(correlationId)
      }

      "perform auditing if required" in new Test {
        // WLOG
        val statusCode: Int = NOT_FOUND

        MockParser
          .parse(Raw(nino))
          .returns(Right(requestData))

        MockStandardService
          .doService(requestDefn, OK)
          .returns(Future.successful(Left(ErrorWrapper(correlationId, NotFoundError, None, statusCode))))

        val result: Future[Result] = controllerWithAudit.handleRequest(nino)(fakeGetRequest(uri))

        status(result) shouldBe statusCode
        contentAsJson(result) shouldBe Json.toJson(NotFoundError)
        header("X-CorrelationId", result) shouldBe Some(correlationId)

        val detail: GenericAuditDetail = GenericAuditDetail(
          "Individual",
          None,
          Map(),
          None,
          "X-123",
          AuditResponse(404, Some(List(AuditError("MATCHING_RESOURCE_NOT_FOUND"))), None))

        val event: AuditEvent[GenericAuditDetail] = AuditEvent("auditType", "txName", detail)
        MockedAuditService.verifyAuditEvent(event).once()
      }
    }
  }

}
