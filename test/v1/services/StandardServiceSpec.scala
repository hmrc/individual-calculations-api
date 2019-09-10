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

package v1.services

import play.api.libs.json.{Json, OFormat, Reads}
import uk.gov.hmrc.http.HeaderCarrier
import v1.connectors.httpparsers.StandardHttpParser.SuccessCode
import v1.connectors.{BackendOutcome, StandardConnector}
import v1.handling.RequestDefinition
import v1.models.errors._
import v1.models.outcomes.ResponseWrapper

import scala.concurrent.{ExecutionContext, Future}

class StandardServiceSpec extends ServiceSpec {
  test =>

  val requestDefn: RequestDefinition[Response, Response] = RequestDefinition.Get[Response, Response]("url")

  case class Response(data: String)

  implicit val format: OFormat[Response] = Json.format[Response]

  object PassedThroughError extends MtdError("PASSED_THROUGH", "msg1")

  object MappedError extends MtdError("MAPPED", "msg2")

  class Test(response: Future[BackendOutcome[Response]]) {
    val mockConnector: StandardConnector = mock[StandardConnector]

    (mockConnector
      .doRequest[Response](_: RequestDefinition[Response, Response])(_: Reads[Response], _: HeaderCarrier, _: ExecutionContext, _: SuccessCode))
      .expects(*, *, *, *, *)
      .returns(response)

    val service = new StandardService(mockConnector)
  }

  "StandardService" should {

    val expected =
      Right(ResponseWrapper("correlationId", Response("someData")))

    val requestHandling: RequestDefinition[Response, Response] = new RequestDefinition.Get[Response, Response](
      uri = "url",
      expectedSuccessCode = 123,
      passThroughErrors = List(PassedThroughError),
      customErrorHandler = Map(("BACKEND_MAPPED", (BAD_REQUEST, MappedError))),
      successHandler = RequestDefinition.noMapping
    )

    "use the connector with the RequestDefn" in new Test(Future.successful(expected)) {
      await(service.doService(requestHandling)) shouldBe expected
    }

    "map the errors according to the passthrough" in
      new Test(Future.successful(Left(ResponseWrapper("correlationId", BackendErrors.single(BAD_REQUEST, BackendErrorCode("PASSED_THROUGH")))))) {
        await(service.doService(requestHandling)) shouldBe Left(ErrorWrapper(Some("correlationId"), MtdErrors(BAD_REQUEST, PassedThroughError)))
      }

    "map the errors according to the mappings" in {
      new Test(Future.successful(Left(ResponseWrapper("correlationId", BackendErrors.single(BAD_REQUEST, BackendErrorCode("BACKEND_MAPPED")))))) {
        await(service.doService(requestHandling)) shouldBe Left(ErrorWrapper(Some("correlationId"), MtdErrors(BAD_REQUEST, MappedError)))
      }
    }
  }
}
