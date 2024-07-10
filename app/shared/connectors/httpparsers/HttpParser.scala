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

package shared.connectors.httpparsers

import play.api.libs.json._
import shared.models.errors._
import shared.utils.Logging
import uk.gov.hmrc.http.HttpResponse

import scala.util.{Success, Try}

trait HttpParser extends Logging {

  implicit class KnownJsonResponse(response: HttpResponse) {

    def validateJson[T](implicit reads: Reads[T]): Option[T] = {
      Try(response.json) match {
        case Success(json: JsValue) =>
          parseResult(json)

        // $COVERAGE-OFF$
        case _ =>
          logger.warn("[KnownJsonResponse][validateJson] No JSON was returned")
          None
        // $COVERAGE-ON$
      }
    }

    def parseResult[T](json: JsValue)(implicit reads: Reads[T]): Option[T] = json.validate[T] match {
      case JsSuccess(value, _) => Some(value)
      case JsError(error) =>
        logger.warn(s"[KnownJsonResponse][validateJson] Unable to parse JSON: $error")
        None
    }

  }

  def retrieveCorrelationId(response: HttpResponse): String = response.header("CorrelationId").getOrElse("")

  private val multipleErrorReads: Reads[Seq[DownstreamErrorCode]] = (__ \ "failures").read[Seq[DownstreamErrorCode]]

  private val bvrErrorReads: Reads[Seq[DownstreamErrorCode]] = {
    implicit val errorIdReads: Reads[DownstreamErrorCode] = (__ \ "id").read[String].map(DownstreamErrorCode(_))
    (__ \ "bvrfailureResponseElement" \ "validationRuleFailures").read[Seq[DownstreamErrorCode]]
  }

  def parseErrors(response: HttpResponse): DownstreamError = {
    val singleError         = response.validateJson[DownstreamErrorCode].map(err => DownstreamErrors(List(err)))
    lazy val multipleErrors = response.validateJson(multipleErrorReads).map(errs => DownstreamErrors(errs))
    lazy val bvrErrors      = response.validateJson(bvrErrorReads).map(errs => OutboundError(BVRError, Some(errs.map(_.toMtd(BVRError.httpStatus)))))

    lazy val unableToParseJsonError = {
      logger.warn(s"unable to parse errors from response: ${response.body}")
      OutboundError(InternalError)
    }

    singleError orElse multipleErrors orElse bvrErrors getOrElse unableToParseJsonError
  }

}
