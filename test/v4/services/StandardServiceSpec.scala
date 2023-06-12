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

package v4.services

import play.api.libs.json.{Json, Reads}
import uk.gov.hmrc.http.HeaderCarrier
import v4.connectors.httpparsers.StandardHttpParser
import v4.connectors.httpparsers.StandardHttpParser.SuccessCode
import v4.connectors.{BackendOutcome, StandardConnector}
import v4.handler.RequestHandler.SuccessMapping
import v4.handler.{RequestDefn, RequestHandler}
import v4.models.errors._
import v4.models.outcomes.ResponseWrapper

import scala.concurrent.{ExecutionContext, Future}

class StandardServiceSpec extends ServiceSpec {
  test =>

  val requestDefn: RequestDefn.Get = RequestDefn.Get("url")
  case class Response(data: String)

  class Test(response: Future[BackendOutcome[Response]]) {
    val mockConnector: StandardConnector = mock[StandardConnector]

    (mockConnector
      .doRequest[Response](_: RequestDefn)(_: Reads[Response], _: HeaderCarrier, _: ExecutionContext, _: SuccessCode, _: String))
      .expects(requestDefn, *, *, *, *, *)
      .returns(response)

    val service = new StandardService(mockConnector)
  }

  object PassedThroughError extends MtdError("PASSED_THROUGH", "msg1")

  object MappedError extends MtdError("MAPPED", "msg2")

  "StandardService" should {

    val expected =
      Right(ResponseWrapper("correlationId", Response("someData")))

    val requestHandling: RequestHandler[Response, Response] = new RequestHandler[Response, Response] {
      override def requestDefn: RequestDefn.Get = test.requestDefn

      override def passThroughErrors = List(PassedThroughError)

      override def customErrorMapping: Map[String, (Int, MtdError)] = Map(("BACKEND_MAPPED", (BAD_REQUEST, MappedError)))

      override implicit val successCode: StandardHttpParser.SuccessCode = SuccessCode(123)
      override def successMapping: SuccessMapping[Response, Response]   = RequestHandler.noMapping

      override implicit val reads: Reads[Response] = Json.reads[Response]
    }

    "use the connector with the RequestDefn" in new Test(Future.successful(expected)) {
      await(service.doService(requestHandling)) shouldBe expected
    }

    "map the errors according to the passthrough" in
      new Test(Future.successful(Left(ResponseWrapper("correlationId", BackendErrors.single(BAD_REQUEST, BackendErrorCode("PASSED_THROUGH")))))) {
        await(service.doService(requestHandling)) shouldBe Left(ErrorWrapper("correlationId", PassedThroughError, None, BAD_REQUEST))
      }

    "map the errors according to the mappings" in {
      new Test(Future.successful(Left(ResponseWrapper("correlationId", BackendErrors.single(BAD_REQUEST, BackendErrorCode("BACKEND_MAPPED")))))) {
        await(service.doService(requestHandling)) shouldBe Left(ErrorWrapper("correlationId", MappedError, None, BAD_REQUEST))
      }
    }
  }

}
