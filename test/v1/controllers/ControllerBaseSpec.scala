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

import play.api.http.{ HeaderNames, MimeTypes, Status }
import play.api.mvc.{ AnyContentAsEmpty, ControllerComponents }
import play.api.test.Helpers.stubControllerComponents
import play.api.test.{ FakeRequest, ResultExtractors }
import support.UnitSpec
import utils.Logging
import v1.handler.RequestHandler
import v1.models.errors._
import v1.models.outcomes.ResponseWrapper
import v1.support.BackendResponseMappingSupport

class ControllerBaseSpec
    extends UnitSpec
    with Status
    with MimeTypes
    with HeaderNames
    with ResultExtractors
    with BackendResponseMappingSupport
    with Logging {

  implicit lazy val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  lazy val cc: ControllerComponents = stubControllerComponents()

  lazy val fakeGetRequest: FakeRequest[AnyContentAsEmpty.type] = fakeRequest.withHeaders(
    HeaderNames.AUTHORIZATION -> "Bearer Token"
  )

  def fakeGetRequest(url: String): FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", url).withHeaders(
    HeaderNames.AUTHORIZATION -> "Bearer Token"
  )

  def fakePostRequest[T](body: T): FakeRequest[T] = fakeRequest.withBody(body)

  def errorMappingCheck[BackendResp, APIResp](backendCode: String, backendStatus: Int, mtdError: MtdError, status: Int)(
      implicit endpointLogContext: EndpointLogContext): RequestHandler[BackendResp, APIResp] => Unit =
    (requestHandling: RequestHandler[BackendResp, APIResp]) => {

      val inputResponse = ResponseWrapper("ignoredCorrelationId", BackendErrors.single(backendStatus, BackendErrorCode(backendCode)))
      val requiredError = ErrorWrapper("ignoredCorrelationId", mtdError, None, status)

      mapBackendErrors(requestHandling.passThroughErrors, requestHandling.customErrorMapping)(inputResponse) shouldBe
        requiredError
    }

  def allChecks[BackendResp, APIResp](params: (String, Int, MtdError, Int)*)(
      implicit endpointLogContext: EndpointLogContext): RequestHandler[BackendResp, APIResp] => Unit = { requestHandling =>
    params
      .map {
        case (backendCode, backendStatus, mtdError, status) => errorMappingCheck[BackendResp, APIResp](backendCode, backendStatus, mtdError, status)
      }
      .foreach(check => check(requestHandling))
  }
}
