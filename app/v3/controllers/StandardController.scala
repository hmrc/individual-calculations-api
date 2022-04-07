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

package v3.controllers

import cats.data.EitherT
import cats.implicits._
import play.api.http.MimeTypes
import play.api.libs.json.{Json, Reads, Writes}
import play.api.mvc.{ControllerComponents, Request, Result}
import uk.gov.hmrc.play.audit.http.connector.AuditResult
import utils.{IdGenerator, Logging}
import v3.connectors.httpparsers.StandardHttpParser.SuccessCode
import v3.controllers.requestParsers.RequestParser
import v3.handler.{AuditHandler, RequestHandler}
import v3.models.audit.AuditResponse
import v3.models.request.RawData
import v3.services._
import v3.support.BackendResponseMappingSupport

import scala.concurrent.{ExecutionContext, Future}

abstract class StandardController[Raw <: RawData, Req, BackendResp: Reads, APIResp: Writes, A](
    val authService: EnrolmentsAuthService,
    val lookupService: MtdIdLookupService,
    parser: RequestParser[Raw, Req],
    service: StandardService,
    auditService: AuditService,
    cc: ControllerComponents,
    idGenerator: IdGenerator
)(implicit ec: ExecutionContext)
    extends AuthorisedController(cc)
    with BaseController
    with BackendResponseMappingSupport
    with Logging {

  implicit val endpointLogContext: EndpointLogContext

  def requestHandlerFor(playRequest: Request[A], req: Req): RequestHandler[BackendResp, APIResp]

  val successCode: SuccessCode

  def doHandleRequest(rawData: Raw, auditHandler: Option[AuditHandler[_]] = None)(implicit request: Request[A]): Future[Result] = {

    implicit val correlationId: String = idGenerator.getCorrelationId
    logger.info(
      s"[${endpointLogContext.controllerName}][${endpointLogContext.endpointName}] " +
        s"with CorrelationId: $correlationId")

    val result =
      for {
        parsedRequest <- EitherT.fromEither[Future](parser.parseRequest(rawData))
        requestHandler = requestHandlerFor(request, parsedRequest)
        backendResponse <- EitherT(service.doService(requestHandler))
        response        <- EitherT.fromEither[Future](requestHandler.successMapping(backendResponse))
      } yield {
        logger.info(
          s"[${endpointLogContext.controllerName}][${endpointLogContext.endpointName}] - " +
            s"Success response received with CorrelationId: ${response.correlationId}"
        )

        val status       = successCode.status
        val responseBody = Json.toJson(response.responseData)

        auditHandler.foreach { auditHandler =>
          def doAudit[D](auditHandler: AuditHandler[D]): Future[AuditResult] = {
            implicit val writes: Writes[D] = auditHandler.writes
            auditService.auditEvent(auditHandler.event(response.correlationId, AuditResponse(successCode.status, Right(Some(responseBody)))))
          }
          doAudit(auditHandler)
        }

        Status(status)(responseBody)
          .withApiHeaders(response.correlationId)
          .as(MimeTypes.JSON)
      }

    result.leftMap { errorWrapper =>
      val resCorrelationId = errorWrapper.correlationId
      val status           = errorWrapper.statusCode
      logger.warn(
        s"[${endpointLogContext.controllerName}][${endpointLogContext.endpointName}] - " +
          s"Error response received with CorrelationId: $resCorrelationId")

      auditHandler.foreach { auditHandler =>
        def doAudit[D](auditHandler: AuditHandler[D]): Future[AuditResult] = {
          implicit val writes: Writes[D] = auditHandler.writes
          auditService.auditEvent(auditHandler.event(resCorrelationId, AuditResponse(status, Left(errorWrapper.auditErrors))))
        }
        doAudit(auditHandler)
      }

      Status(status)(Json.toJson(errorWrapper)).withApiHeaders(resCorrelationId)
    }.merge
  }

}
