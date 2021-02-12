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

package v1r2.support

import support.UnitSpec
import v1r2.models.domain.MessageType
import v1r2.models.response.getMessages.{MessagesResponse, Message}

class MessagesFilterSpec extends UnitSpec {

  val filter: MessagesFilter = new MessagesFilter {}
  val calcId: String = "someCalcId"

  val messages: MessagesResponse = MessagesResponse(
    Some(Seq(Message("infoId", "infoMessage"))),
    Some(Seq(Message("warningId", "warningMessage"))),
    Some(Seq(Message("errorId", "errorMessage"))),
    calcId
  )

  "Calling filter" should {

    "return only error messages" when {

      "provided with an error filter" in {
        filter.filter(messages, Seq(MessageType.error)) shouldBe MessagesResponse(None, None, Some(Seq(Message("errorId", "errorMessage"))), calcId)
      }
    }

    "return only warning messages" when {

      "provided with a warning filter" in {
        filter.filter(messages, Seq(MessageType.warning)) shouldBe MessagesResponse(None, Some(Seq(Message("warningId", "warningMessage"))), None, calcId)
      }
    }

    "return only info messages" when {

      "provided with an info filter" in {
        filter.filter(messages, Seq(MessageType.info)) shouldBe MessagesResponse(Some(Seq(Message("infoId", "infoMessage"))), None, None, calcId)
      }
    }

    "return multiple message types" when {

      "provided with info and warning filters" in {
        filter.filter(messages, Seq(MessageType.info, MessageType.warning)) shouldBe messages.copy(errors = None)
      }

      "provided with info and error filters" in {
        filter.filter(messages, Seq(MessageType.info, MessageType.error)) shouldBe messages.copy(warnings = None)
      }

      "provided with error and warning filters" in {
        filter.filter(messages, Seq(MessageType.error, MessageType.warning)) shouldBe messages.copy(info = None)
      }
    }

    "return the original messages object" when {

      "provided with no filter" in {
        filter.filter(messages, Seq()) shouldBe messages
      }

      "provided with filters for every type" in {
        filter.filter(messages, Seq(MessageType.info, MessageType.warning, MessageType.error)) shouldBe messages
      }
    }

    "return an empty message set" when {

      "provided with an error filter and no error messages" in {
        filter.filter(messages.copy(errors = None), Seq(MessageType.error)) shouldBe MessagesResponse(None, None, None, calcId)
      }

      "provided with a warning filter and no warning messages" in {
        filter.filter(messages.copy(warnings = None), Seq(MessageType.warning)) shouldBe MessagesResponse(None, None, None, calcId)
      }

      "provided with an info filter and no info messages" in {
        filter.filter(messages.copy(info = None), Seq(MessageType.info)) shouldBe MessagesResponse(None, None, None, calcId)
      }

      "provided with only an invalid filter" in {
        filter.filter(messages, Seq(MessageType.none)) shouldBe MessagesResponse(None, None, None, calcId)
      }
    }
  }
}