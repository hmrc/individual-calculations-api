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

package v3.connectors.httpparsers

import play.api.http.Status._
import play.api.libs.functional.syntax._
import play.api.libs.json._
import uk.gov.hmrc.http.HttpResponse
import utils.Logging
import v3.models.errors._

import scala.util.{Success, Try}

trait HttpParser extends Logging {

  implicit class KnownJsonResponse(response: HttpResponse) {

    def validateJson[T](implicit reads: Reads[T]): Option[T] = {
      Try(response.json) match {
        case Success(json: JsValue) => parseResult(json)
        case _ =>
          logger.warn("[KnownJsonResponse][validateJson] No JSON was returned")
          None
      }
    }

    private def parseResult[T](json: JsValue)(implicit reads: Reads[T]): Option[T] = json.validate[T] match {

      case JsSuccess(value, _) => Some(value)
      case JsError(error) =>
        logger.warn(s"[KnownJsonResponse][validateJson] Unable to parse JSON: $error")
        None
    }
  }

  def retrieveCorrelationId(response: HttpResponse): String = response.header("X-CorrelationId").getOrElse("")

  def retrieveDesCorrelationId(response: HttpResponse): String = response.header("CorrelationId").getOrElse("")

  def parseErrors(response: HttpResponse): BackendError = {

    implicit val reads: Reads[BackendError] =
      (
        __.read[BackendErrorCode] and
          (__ \ "errors").readNullable[List[BackendErrorCode]]
        ) { (singleError, errors) =>
        BackendErrors(response.status, singleError :: errors.getOrElse(Nil))
      }

    response
      .validateJson[BackendError]
      .getOrElse {
        logger.warn(s"unable to parse errors from response: ${response.body}")
        OutboundError(INTERNAL_SERVER_ERROR, DownstreamError)
      }
  }

  private val multipleErrorReads: Reads[List[BackendErrorCode]] = (__ \ "failures").read[List[BackendErrorCode]]

  private val bvrErrorReads: Reads[Seq[BackendErrorCode]] = {
    implicit val errorIdReads: Reads[BackendErrorCode] = (__ \ "id").read[String].map(BackendErrorCode(_))
    (__ \ "bvrfailureResponseElement" \ "validationRuleFailures").read[Seq[BackendErrorCode]]
  }

  def parseDesErrors(response: HttpResponse): BackendError = {
    val singleError         = response.validateJson[BackendErrorCode].map(err => BackendErrors(response.status, List(err)))
    lazy val multipleErrors = response.validateJson(multipleErrorReads).map(errs => BackendErrors(response.status, errs))
    lazy val bvrErrors      = response.validateJson(bvrErrorReads).map(errs => OutboundError(response.status, BVRError, Some(errs.map(_.fromDes))))
    lazy val unableToParseJsonError = {
      logger.warn(s"unable to parse errors from response: ${response.body}")
      OutboundError(INTERNAL_SERVER_ERROR, DownstreamError)
    }

    singleError orElse multipleErrors orElse bvrErrors getOrElse unableToParseJsonError
  }
}