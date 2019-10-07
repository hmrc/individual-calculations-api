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

import cats.data.EitherT
import cats.implicits._
import play.api.http.MimeTypes
import play.api.libs.json.{Json, Reads, Writes}
import play.api.mvc.{ControllerComponents, Request, Result}
import utils.Logging
import v1.connectors.httpparsers.StandardHttpParser.SuccessCode
import v1.controllers.requestParsers.RequestParser
import v1.handling.RequestDefinition
import v1.handling.RequestDefinition.SuccessHandler
import v1.models.errors.ErrorWrapper
import v1.models.outcomes.ResponseWrapper
import v1.models.request.RawData
import v1.services._
import v1.support.BackendResponseMappingSupport

import scala.concurrent.{ExecutionContext, Future}

abstract class StandardController[Raw <: RawData, Req, BackendResp: Reads, APIResp: Writes, A](
                                                                                                val authService: EnrolmentsAuthService,
                                                                                                val lookupService: MtdIdLookupService,
                                                                                                parser: RequestParser[Raw, Req],
                                                                                                service: StandardService,
                                                                                                cc: ControllerComponents
                                                                                              )(implicit ec: ExecutionContext)
  extends AuthorisedController(cc)
    with BaseController
    with BackendResponseMappingSupport
    with Logging {

  implicit val endpointLogContext: EndpointLogContext

  def requestDefinitionFor(playRequest: Request[A], req: Req): RequestDefinition[BackendResp, APIResp]

  val successCode: SuccessCode

  def doHandleRequest(rawData: Raw)(implicit request: Request[A]): Future[Result] = {
    val result =
      for {
        parsedRequest <- EitherT.fromEither[Future](parser.parseRequest(rawData))
        requestHandling = requestDefinitionFor(request, parsedRequest)
        backendResponse <- EitherT(service.doService(requestHandling))
        response <- EitherT.fromEither[Future](requestHandling.successHandler(backendResponse))
      } yield {
        logger.info(
          s"[${endpointLogContext.controllerName}][${endpointLogContext.endpointName}] - " +
            s"Success response received with CorrelationId: ${response.correlationId}"
        )

        Status(successCode.status)(Json.toJson(response.responseData))
          .withApiHeaders(response.correlationId)
          .as(MimeTypes.JSON)
      }

    result.leftMap { errorWrapper =>
      val correlationId = getCorrelationId(errorWrapper)
      val errorBody = errorWrapper.errors

      Status(errorWrapper.errors.statusCode)(Json.toJson(errorBody)).withApiHeaders(correlationId)
    }.merge
  }
}
