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

package v2.models.response.getMessages

import config.AppConfig
import play.api.libs.functional.syntax._
import play.api.libs.json._
import utils.NestedJsonReads._
import v2.hateoas.{HateoasLinks, HateoasLinksFactory}
import v2.models.hateoas.{HateoasData, Link}

case class Message(id: String, text: String)

object Message {
  implicit val format: OFormat[Message] = Json.format[Message]
}

case class MessagesResponse(info: Option[Seq[Message]], warnings: Option[Seq[Message]], errors: Option[Seq[Message]], id: String) {

  val hasMessages: Boolean = this match {
    case MessagesResponse(None, None, None, _) => false
    case _                                     => true
  }

}

object MessagesResponse extends HateoasLinks {

  implicit val writes: OWrites[MessagesResponse] = new OWrites[MessagesResponse] {

    def writes(response: MessagesResponse): JsObject =
      response.info.fold(Json.obj())(a => Json.obj("info" -> a)) ++
        response.warnings.fold(Json.obj())(a => Json.obj("warnings" -> a)) ++
        response.errors.fold(Json.obj())(a => Json.obj("errors" -> a))

  }

  implicit val reads: Reads[MessagesResponse] = (
    (__ \ "messages" \ "info").readNestedNullable[Seq[Message]] and
      (__ \ "messages" \ "warnings").readNestedNullable[Seq[Message]] and
      (__ \ "messages" \ "errors").readNestedNullable[Seq[Message]] and
      (__ \ "metadata" \ "id").read[String]
  )(MessagesResponse.apply _)

  implicit object LinksFactory extends HateoasLinksFactory[MessagesResponse, MessagesHateoasData] {

    override def links(appConfig: AppConfig, data: MessagesHateoasData): Seq[Link] = {
      import data.{id, nino}
      Seq(
        getMetadata(appConfig, nino, id, isSelf = false),
        getMessages(appConfig, nino, id, isSelf = true)
      )
    }

  }

}

case class MessagesHateoasData(nino: String, id: String) extends HateoasData
