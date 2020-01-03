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

package v1.support

import cats.implicits._
import utils.Logging
import v1.connectors.BackendOutcome
import v1.controllers.EndpointLogContext
import v1.models.errors._
import v1.models.outcomes.ResponseWrapper
import play.api.http.Status._

trait BackendResponseMappingSupport {
  self: Logging =>

  final def mapBackendErrors[D](passThroughErrors: Seq[MtdError], errorCodeMap: PartialFunction[String, (Int, MtdError)])(
      backendResponseWrapper: ResponseWrapper[BackendError])(implicit logContext: EndpointLogContext): ErrorWrapper = {

    val defaultErrorCodeMapping: String => (Int, MtdError) = { code =>
      logger.info(s"[${logContext.controllerName}] [${logContext.endpointName}] - No mapping found for error code $code")
      (INTERNAL_SERVER_ERROR, DownstreamError)
    }

    val passThroughMapping: PartialFunction[String, MtdError] =
      passThroughErrors.map(err => err.code -> err).toMap

    def map(backendStatus: Int, backendErrorCode: BackendErrorCode): (Int, MtdError) = {
      (errorCodeMap orElse passThroughMapping.andThen(mtdError => (backendStatus, mtdError)))
        .applyOrElse(backendErrorCode.code, defaultErrorCodeMapping)
    }

    backendResponseWrapper match {
      case ResponseWrapper(correlationId, BackendErrors(backendStatusCode, backendErrorCode :: Nil)) =>
        val (statusCode, mtdError) = map(backendStatusCode, backendErrorCode)
        ErrorWrapper(Some(correlationId), MtdErrors(statusCode, mtdError, None))

      case ResponseWrapper(correlationId, BackendErrors(backendStatusCode, backendErrorCodes)) =>
        val mtdErrors = backendErrorCodes.map(errorCode => map(backendStatusCode, errorCode)).map(_._2)

        if (mtdErrors.contains(DownstreamError)) {
          logger.info(
            s"[${logContext.controllerName}] [${logContext.endpointName}] [CorrelationId - $correlationId]" +
              s" - downstream returned ${backendErrorCodes.map(_.code).mkString(",")}. Revert to ISE")
          ErrorWrapper(Some(correlationId), MtdErrors(INTERNAL_SERVER_ERROR, DownstreamError, None))
        } else {
          ErrorWrapper(Some(correlationId), MtdErrors(BAD_REQUEST, BadRequestError, Some(mtdErrors)))
        }

      case ResponseWrapper(correlationId, OutboundError(statusCode, error, errors)) =>
        ErrorWrapper(Some(correlationId), MtdErrors(statusCode, error, errors))
    }
  }

  def directMap[D](passThroughErrors: Seq[MtdError], errorCodeMap: PartialFunction[String, (Int, MtdError)])(outcome: BackendOutcome[D])(
      implicit logContext: EndpointLogContext): Either[ErrorWrapper, ResponseWrapper[D]] = {
    outcome.leftMap(mapBackendErrors(passThroughErrors, errorCodeMap))
  }
}
