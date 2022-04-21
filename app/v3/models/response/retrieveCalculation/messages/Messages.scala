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

package v3.models.response.retrieveCalculation.messages

import play.api.libs.json.{Json, OFormat}

case class Message(id: String, text: String)

case class Messages(info: Option[Seq[Message]], warnings: Option[Seq[Message]], errors: Option[Seq[Message]])

object Messages {
  implicit val messageFormat: OFormat[Message]   = Json.format[Message]
  implicit val messagesFormat: OFormat[Messages] = Json.format[Messages]
}
