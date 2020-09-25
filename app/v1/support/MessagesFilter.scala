/*
 * Copyright 2020 HM Revenue & Customs
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

package v1.support

import play.api.libs.json._
import v1.models.domain.MessageType

import scala.annotation.tailrec

trait MessagesFilter {
  def filter(calculationMessages: JsObject, typeQueries: Seq[MessageType]): JsValue = {

    def filterMessages(filteredMessages: JsObject, typeQuery: MessageType): JsObject = {
      typeQuery match {
        case MessageType.error   => add(filteredMessages, "errors")
        case MessageType.warning => add(filteredMessages, "warnings")
        case MessageType.info    => add(filteredMessages, "info")
        case _                   => filteredMessages
      }
    }

    def add(filteredMessages: JsObject, path: String): JsObject = calculationMessages \ path match {
      case JsDefined(value) => filteredMessages.deepMerge(Json.obj("data" -> Json.obj("messages" -> Json.obj(path -> value))))
      case _: JsUndefined   => filteredMessages
    }

    @tailrec
    def filterLoop(filteredMessages: JsObject, typeQueries: Seq[MessageType]): JsObject = {
      typeQueries.toList match {
        case Nil           => calculationMessages
        case query :: Nil  => filterMessages(filteredMessages, query)
        case query :: tail => filterLoop(filterMessages(filteredMessages, query), tail)
      }
    }

    calculationMessages \ "data" \ "metadata" \ "id" match {
      case JsDefined(id: JsString) => filterLoop(Json.obj("data" -> Json.obj("metadata" -> Json.obj("id" -> id.value))), typeQueries)
    }
  }
}
