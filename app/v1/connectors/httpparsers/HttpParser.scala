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

package v1.connectors.httpparsers

import play.api.Logger
import play.api.http.Status._
import play.api.libs.functional.syntax._
import play.api.libs.json._
import uk.gov.hmrc.http.HttpResponse
import v1.models.errors._

import scala.util.{ Success, Try }

trait HttpParser {

  implicit class KnownJsonResponse(response: HttpResponse) {

    def validateJson[T](implicit reads: Reads[T]): Option[T] = {
      Try(response.json) match {
        case Success(json: JsValue) => parseResult(json)
        case _ =>
          Logger.warn("[KnownJsonResponse][validateJson] No JSON was returned")
          None
      }
    }

    private def parseResult[T](json: JsValue)(implicit reads: Reads[T]): Option[T] = json.validate[T] match {

      case JsSuccess(value, _) => Some(value)
      case JsError(error) =>
        Logger.warn(s"[KnownJsonResponse][validateJson] Unable to parse JSON: $error")
        None
    }
  }

  def retrieveCorrelationId(response: HttpResponse): String = response.header("CorrelationId").getOrElse("")

  def parseErrors(response: HttpResponse): BackendError = {

    implicit val reads: Reads[BackendError] = (
      ((__).read[BackendErrorCode] and
        (__ \ "errors").readNullable[List[BackendErrorCode]]) { (singleError, errors) =>
        BackendErrors(response.status, singleError :: errors.getOrElse(Nil))
      }
    )

    response
      .validateJson[BackendError]
      .getOrElse {
        Logger.warn(s"unable to parse errors from response: ${response.body}")
        OutboundError(INTERNAL_SERVER_ERROR, DownstreamError)
      }
  }

}
