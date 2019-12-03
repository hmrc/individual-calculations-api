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

package v1.models.errors

import play.api.libs.json.{JsValue, Json, Writes}
import v1.models.audit.AuditError

case class MtdErrors(statusCode: Int, error: MtdError, errors: Option[Seq[MtdError]] = None) {

  def auditErrors: Seq[AuditError] =
    allErrors.map(error => AuditError(error.code))

  private def allErrors: Seq[MtdError] = errors match {
    case Some(seq) => seq
    case None => Seq(error)
  }
}

object MtdErrors {
  implicit val writes: Writes[MtdErrors] = new Writes[MtdErrors] {
    override def writes(errorResponse: MtdErrors): JsValue = {

      val json = Json.obj(
        "code" -> errorResponse.error.code,
        "message" -> errorResponse.error.message
      )

      errorResponse.errors match {
        case Some(errors) if errors.nonEmpty => json + ("errors" -> Json.toJson(errors))
        case _ => json
      }

    }
  }
}
