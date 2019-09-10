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

import cats.implicits._
import org.scalamock.handlers.CallHandler
import play.api.libs.json.{Format, Json, Reads, Writes}
import play.api.mvc.{AnyContent, Request, Result}
import uk.gov.hmrc.http.HeaderCarrier
import v1.connectors.httpparsers.StandardHttpParser
import v1.connectors.httpparsers.StandardHttpParser.SuccessCode
import v1.controllers.requestParsers.RequestParser
import v1.handling.RequestDefinition
import v1.mocks.services.{MockEnrolmentsAuthService, MockMtdIdLookupService, MockStandardService}
import v1.models.errors._
import v1.models.outcomes.ResponseWrapper
import v1.models.request.RawData

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class StandardControllerSpec extends ControllerBaseSpec with MockEnrolmentsAuthService with MockMtdIdLookupService with MockStandardService {

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

  val mockParser = mock[RequestParser[Raw, RequestData]]

  object MockParser {

    def parse(data: Raw): CallHandler[Either[ErrorWrapper, RequestData]] = {
      (mockParser.parseRequest(_: Raw)).expects(data)
    }
  }

  trait Test {
    def uri           = "/input/uri"
    val correlationId = "X-123"
    val rawData       = Raw("data")
    val requestData   = RequestData("data")

    val response       = BackendResp("data")
    val mappedResponse = APIResp("dataMapped")
    val requestDefn    = RequestDefinition.Get[BackendResp, BackendResp]("url")

    val hc = HeaderCarrier()

    val controller: StandardController[Raw, RequestData, BackendResp, APIResp, AnyContent] =
      new StandardController[Raw, RequestData, BackendResp, APIResp, AnyContent](
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      parser = mockParser,
      service = mockStandardService,
      cc = cc
    ) {
      override implicit val endpointLogContext: EndpointLogContext = EndpointLogContext("standard", "standard")

      override def requestHandlingFor(playRequest: Request[AnyContent], req: RequestData) = {
        RequestDefinition.Get[BackendResp, APIResp](
          uri = "url",
          successHandler = x=> Right(x.map(_ => mappedResponse))
        )
      }

      override val successCode: StandardHttpParser.SuccessCode = SuccessCode(OK)
    }
  }

  "handleRequest" should {
    "return success result" when {
      "happy path" in new Test {
        MockParser
          .parse(rawData)
          .returns(Right(requestData))

        MockStandardService
          .doService(requestDefn, OK)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        val result: Future[Result] = controller.doHandleRequest(rawData)(fakeGetRequest(uri))

        status(result) shouldBe OK
        contentAsJson(result) shouldBe Json.toJson(mappedResponse)
        header("X-CorrelationId", result) shouldBe Some(correlationId)
      }
    }

    "return the error as per spec" when {
      "parser errors occurs" in new Test {
        // WLOG
        val statusCode = BAD_REQUEST
        val error      = NinoFormatError

        MockParser
          .parse(rawData)
          .returns(Left(ErrorWrapper(Some(correlationId), MtdErrors(statusCode, error))))

        val result: Future[Result] = controller.doHandleRequest(rawData)(fakeGetRequest(uri))

        status(result) shouldBe statusCode
        contentAsJson(result) shouldBe Json.toJson(error)
        header("X-CorrelationId", result) shouldBe Some(correlationId)
      }
    }

    "service errors occur" must {
      "return the errors" in new Test {
        // WLOG
        val statusCode = NOT_FOUND
        val errors     = MtdErrors(statusCode, NotFoundError)

        MockParser
          .parse(rawData)
          .returns(Right(requestData))

        MockStandardService
          .doService(requestDefn, OK)
          .returns(Future.successful(Left(ErrorWrapper(Some(correlationId), errors))))

        val result: Future[Result] = controller.doHandleRequest(rawData)(fakeGetRequest(uri))

        status(result) shouldBe statusCode
        contentAsJson(result) shouldBe Json.toJson(errors)
        header("X-CorrelationId", result) shouldBe Some(correlationId)
      }
    }
  }
}
