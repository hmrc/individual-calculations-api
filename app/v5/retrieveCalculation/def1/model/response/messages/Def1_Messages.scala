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

package v5.retrieveCalculation.def1.model.response.messages

import play.api.libs.json.{Json, OFormat}

case class Def1_Message(id: String, text: String)

case class Def1_Messages(info: Option[Seq[Def1_Message]], warnings: Option[Seq[Def1_Message]], errors: Option[Seq[Def1_Message]])

object Def1_Messages {
  implicit val messageFormat: OFormat[Def1_Message]   = Json.format[Def1_Message]
  implicit val messagesFormat: OFormat[Def1_Messages] = Json.format[Def1_Messages]
}
