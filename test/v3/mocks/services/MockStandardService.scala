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

package v3.mocks.services

import org.scalamock.handlers.CallHandler
import org.scalamock.scalatest.MockFactory
import org.scalatest.matchers.should.Matchers
import uk.gov.hmrc.http.HeaderCarrier
import v3.controllers.EndpointLogContext
import v3.handler.{RequestDefn, RequestHandler}
import v3.models.errors.ErrorWrapper
import v3.models.outcomes.ResponseWrapper
import v3.services.StandardService

import scala.concurrent.{ExecutionContext, Future}

trait MockStandardService extends MockFactory {
  self: Matchers =>

  val mockStandardService: StandardService = mock[StandardService]

  object MockStandardService {

    def doService[Resp, _](requestDefn: RequestDefn, successStatus: Int): CallHandler[Future[Either[ErrorWrapper, ResponseWrapper[Resp]]]] = {

      val correctRequestHandler = argAssert { actualRequestHandler: RequestHandler[Resp, _] =>
        actualRequestHandler.requestDefn shouldBe requestDefn
        actualRequestHandler.successCode.status shouldBe successStatus
      }

      (mockStandardService
        .doService(_: RequestHandler[Resp, _])(_: EndpointLogContext, _: ExecutionContext, _: HeaderCarrier, _: String))
        .expects(correctRequestHandler, *, *, *, *)
    }

    def doServiceWithMappings[BackendResp, APIResp](
        mappingAssertion: RequestHandler[BackendResp, APIResp] => Unit): CallHandler[Future[Either[ErrorWrapper, ResponseWrapper[BackendResp]]]] = {
      (mockStandardService
        .doService(_: RequestHandler[BackendResp, APIResp])(_: EndpointLogContext, _: ExecutionContext, _: HeaderCarrier, _: String))
        .expects(argAssert(mappingAssertion), *, *, *, *)
    }
  }
}