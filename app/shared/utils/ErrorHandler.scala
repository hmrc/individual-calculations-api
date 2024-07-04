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

package shared.utils

import play.api._
import play.api.http.Status._
import play.api.mvc.Results._
import play.api.mvc._
import shared.models.errors._
import shared.routing.Versions
import uk.gov.hmrc.auth.core.AuthorisationException
import uk.gov.hmrc.http._
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.bootstrap.backend.http.JsonErrorHandler
import uk.gov.hmrc.play.bootstrap.config.HttpAuditEvent
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import javax.inject._
import scala.concurrent._

@Singleton
class ErrorHandler @Inject() (
    config: Configuration,
    auditConnector: AuditConnector,
    httpAuditEvent: HttpAuditEvent
)(implicit ec: ExecutionContext)
    extends JsonErrorHandler(auditConnector, httpAuditEvent, config) {

  import httpAuditEvent.dataEvent

  private val logger: Logger = Logger(this.getClass)

  override def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] = {

    implicit val headerCarrier: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)

    logger.warn(
      message = s"[ErrorHandler][onClientError] error in version " +
        s"${versionIfSpecified(request)}, " +
        s"for (${request.method}) [${request.uri}] with status: " +
        s" $statusCode and message: $message")

    statusCode match {

      case BAD_REQUEST =>
        auditConnector.sendEvent(dataEvent("ServerValidationError", "Request bad format exception", request))
        Future.successful(BadRequest(BadRequestError.asJson))

      case NOT_FOUND =>
        auditConnector.sendEvent(dataEvent("ResourceNotFound", "Resource Endpoint Not Found", request))
        Future.successful(NotFound(NotFoundError.asJson))

      case _ =>
        val errorCode = statusCode match {
          case UNAUTHORIZED           => ClientOrAgentNotAuthorisedError.withStatus401
          case METHOD_NOT_ALLOWED     => InvalidHttpMethodError
          case UNSUPPORTED_MEDIA_TYPE => InvalidBodyTypeError
          case _                      => MtdError("INVALID_REQUEST", message, BAD_REQUEST)
        }

        auditConnector.sendEvent(
          dataEvent(
            eventType = "ClientError",
            transactionName = s"A client error occurred, status: ${errorCode.httpStatus}",
            request = request,
            detail = Map.empty
          )
        )

        Future.successful(Status(errorCode.httpStatus)(errorCode.asJson))
    }
  }

  private def versionIfSpecified(request: RequestHeader): String = Versions.getFromRequest(request).map(_.name).getOrElse("<unspecified>")

  override def onServerError(request: RequestHeader, ex: Throwable): Future[Result] = {
    implicit val headerCarrier: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)

    logger.warn(
      message = s"[ErrorHandler][onServerError] Internal server error in version " +
        s"${versionIfSpecified(request)}, " +
        s"for (${request.method}) [${request.uri}] -> ",
      ex
    )

    val (errorCode, eventType) = ex match {
      case _: NotFoundException      => (NotFoundError, "ResourceNotFound")
      case _: AuthorisationException => (ClientOrAgentNotAuthorisedError.withStatus401, "ClientError")
      case _: JsValidationException  => (BadRequestError, "ServerValidationError")
      case e: HttpException          => (BadRequestError, "ServerValidationError")
      case e: UpstreamErrorResponse if UpstreamErrorResponse.Upstream4xxResponse.unapply(e).isDefined =>
        (BadRequestError, "ServerValidationError")
      case e: UpstreamErrorResponse if UpstreamErrorResponse.Upstream5xxResponse.unapply(e).isDefined =>
        (InternalError, "ServerInternalError")
      case _ => (InternalError, "ServerInternalError")
    }

    auditConnector.sendEvent(
      dataEvent(
        eventType = eventType,
        transactionName = "Unexpected error",
        request = request,
        detail = Map("transactionFailureReason" -> ex.getMessage)
      )
    )

    Future.successful(Status(errorCode.httpStatus)(errorCode.asJson))
  }

}
