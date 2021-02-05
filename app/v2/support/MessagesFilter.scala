/*
 * Copyright 2021 HM Revenue & Customs
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

package v2.support

import v2.models.domain.MessageType
import v2.models.response.getMessages.MessagesResponse

import scala.annotation.tailrec

trait MessagesFilter {
  def filter(calculationMessages: MessagesResponse, typeQueries: Seq[MessageType]): MessagesResponse = {

    def filterMessages(filteredMessages: MessagesResponse, typeQuery: MessageType): MessagesResponse = {
      typeQuery match {
        case MessageType.error => filteredMessages.copy(errors = calculationMessages.errors)
        case MessageType.warning => filteredMessages.copy(warnings = calculationMessages.warnings)
        case MessageType.info => filteredMessages.copy(info = calculationMessages.info)
        case _ => filteredMessages
      }
    }

    @tailrec
    def filterLoop(filteredMessages: MessagesResponse, typeQueries: Seq[MessageType]): MessagesResponse = {
      typeQueries.toList match {
        case Nil => calculationMessages
        case query :: Nil => filterMessages(filteredMessages, query)
        case query :: tail => filterLoop(filterMessages(filteredMessages, query), tail)
      }
    }

    filterLoop(MessagesResponse(None, None, None, calculationMessages.id), typeQueries)
  }
}