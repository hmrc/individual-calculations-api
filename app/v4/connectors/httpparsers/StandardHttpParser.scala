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

package v4.connectors.httpparsers

import play.api.http.Status._
import play.api.libs.json.Reads
import uk.gov.hmrc.http.{HttpReads, HttpResponse}
import v4.connectors.BackendOutcome
import v4.models.errors.{DownstreamError, OutboundError}
import v4.models.outcomes.ResponseWrapper
import v4.models.response.common.{DownstreamResponse, DownstreamUnit}

object StandardHttpParser extends HttpParser {

  case class SuccessCode(status: Int) extends AnyVal

  // Return Right[BackendOutcome[Unit]] as success response has no body - no need to assign it a value
  implicit def readsEmpty(implicit successCode: SuccessCode = SuccessCode(NO_CONTENT)): HttpReads[BackendOutcome[Unit]] =
    (_: String, url: String, response: HttpResponse) =>
      doRead(url, response) { correlationId =>
        Right(ResponseWrapper(correlationId, ()))
      }

  implicit def downstreamReadsEmpty(implicit successCode: SuccessCode = SuccessCode(NO_CONTENT)): HttpReads[BackendOutcome[DownstreamUnit]] =
    (_: String, url: String, response: HttpResponse) =>
      doDownstreamRead(url, response) { correlationId =>
        Right(ResponseWrapper(correlationId, DownstreamUnit))
      }

  implicit def reads[A: Reads](implicit successCode: SuccessCode = SuccessCode(OK)): HttpReads[BackendOutcome[A]] =
    (_: String, url: String, response: HttpResponse) =>
      doRead(url, response) { correlationId =>
        response.validateJson[A] match {
          case Some(ref) => Right(ResponseWrapper(correlationId, ref))
          case None      => Left(ResponseWrapper(correlationId, OutboundError(INTERNAL_SERVER_ERROR, DownstreamError)))
        }
      }

  implicit def downstreamReads[A <: DownstreamResponse](implicit
      successCode: SuccessCode = SuccessCode(OK),
      reads: Reads[A]): HttpReads[BackendOutcome[A]] =
    (_: String, url: String, response: HttpResponse) =>
      doDownstreamRead(url, response) { correlationId =>
        response.validateJson[A] match {
          case Some(ref) => Right(ResponseWrapper(correlationId, ref))
          case None      => Left(ResponseWrapper(correlationId, OutboundError(INTERNAL_SERVER_ERROR, DownstreamError)))
        }
      }

  private def doRead[A](url: String, response: HttpResponse)(successOutcomeFactory: String => BackendOutcome[A])(implicit
      successCode: SuccessCode): BackendOutcome[A] = {

    val correlationId = retrieveCorrelationId(response)

    if (response.status != successCode.status) {
      logger.warn(
        "[StandardHttpParser][read] - " +
          s"Error response received from backend with status: ${response.status} and body\n" +
          s"${response.body} and correlationId: $correlationId when calling $url")
    }

    response.status match {
      case successCode.status =>
        logger.info(
          "[StandardHttpParser][read] - " +
            s"Success response received from backend with correlationId: $correlationId when calling $url")
        successOutcomeFactory(correlationId)
      case BAD_REQUEST | NOT_FOUND | FORBIDDEN | CONFLICT => Left(ResponseWrapper(correlationId, parseErrors(response)))
      case _ => Left(ResponseWrapper(correlationId, OutboundError(INTERNAL_SERVER_ERROR, DownstreamError)))
    }
  }

  private def doDownstreamRead[A](url: String, response: HttpResponse)(successOutcomeFactory: String => BackendOutcome[A])(implicit
      successCode: SuccessCode): BackendOutcome[A] = {

    val correlationId = retrieveDownstreamCorrelationId(response)

    if (response.status != successCode.status) {
      logger.warn(
        "[StandardHttpParser][read] - " +
          s"Error response received from backend with status: ${response.status} and body\n" +
          s"${response.body} and correlationId: $correlationId when calling $url")
    }

    response.status match {
      case successCode.status =>
        logger.info(
          "[StandardHttpParser][read] - " +
            s"Success response received from backend with correlationId: $correlationId when calling $url")
        successOutcomeFactory(correlationId)
      case BAD_REQUEST | NOT_FOUND | FORBIDDEN | CONFLICT | UNPROCESSABLE_ENTITY =>
        Left(ResponseWrapper(correlationId, parseDownstreamErrors(response)))
      case _ => Left(ResponseWrapper(correlationId, OutboundError(INTERNAL_SERVER_ERROR, DownstreamError)))
    }
  }

}