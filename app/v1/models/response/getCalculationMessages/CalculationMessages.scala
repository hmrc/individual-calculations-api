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

package v1.models.response.getCalculationMessages

import play.api.libs.functional.syntax._
import play.api.libs.json._
import utils.NestedJsonReads._

case class Message(id: String, text: String)

object Message {
  implicit val format: OFormat[Message] = Json.format[Message]
}

case class CalculationMessages(info: Option[Seq[Message]], warnings: Option[Seq[Message]], errors: Option[Seq[Message]]) {
  def hasMessages: Boolean = this match {
    case CalculationMessages(None,None,None) => false
    case _ => true
  }
}

object CalculationMessages {
  implicit val writes: Writes[CalculationMessages] = Json.writes[CalculationMessages]
  implicit val reads: Reads[CalculationMessages] = (
    (__ \ "messages" \ "info").readNestedNullable[Seq[Message]].map {
      case Some(info) if info.nonEmpty => Some(info)
      case _ => None}
      and
      (__ \ "messages" \ "warnings").readNestedNullable[Seq[Message]].map {
        case Some(warns) if warns.nonEmpty => Some(warns)
        case _ => None}
      and
      (__ \ "messages" \ "errors").readNestedNullable[Seq[Message]].map {
        case Some(errs) if errs.nonEmpty => Some(errs)
        case _ => None})(CalculationMessages.apply _)
}


