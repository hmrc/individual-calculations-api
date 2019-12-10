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

package v1.models.response.getMessages

import config.AppConfig
import play.api.libs.functional.syntax._
import play.api.libs.json._
import utils.NestedJsonReads._
import v1.hateoas.{HateoasLinks, HateoasLinksFactory}
import v1.models.hateoas.{HateoasData, Link}

case class Message(id: String, text: String)

object Message {
  implicit val format: OFormat[Message] = Json.format[Message]
}

case class MessagesResponse(info: Option[Seq[Message]],
                            warnings: Option[Seq[Message]],
                            errors: Option[Seq[Message]]) {

  val hasMessages: Boolean = if (this == MessagesResponse.empty) false else true
}

object MessagesResponse extends HateoasLinks {

  val empty: MessagesResponse = MessagesResponse(None, None, None)

  implicit val writes: OWrites[MessagesResponse] = Json.writes[MessagesResponse]
  implicit val reads: Reads[MessagesResponse] = (
    (__ \ "messages" \ "info").readNestedNullable[Seq[Message]] and
      (__ \ "messages" \ "warnings").readNestedNullable[Seq[Message]] and
      (__ \ "messages" \ "errors").readNestedNullable[Seq[Message]]
    ) (MessagesResponse.apply _)

  implicit object LinksFactory extends HateoasLinksFactory[MessagesResponse, CalculationMessagesHateoasData] {
    override def links(appConfig: AppConfig, data: CalculationMessagesHateoasData): Seq[Link] = {
      import data.{id, nino}
      Seq(
        getMetadata(appConfig, nino, id, isSelf = false),
        getMessages(appConfig, nino, id, isSelf = true)
      )
    }
  }

}

case class CalculationMessagesHateoasData(nino: String, id: String) extends HateoasData