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

import play.api.libs.json.JsObject
import support.UnitSpec
import v1.models.domain.MessageType
import v1.fixtures.getMessages.MessagesResponseFixture

class MessagesFilterSpec extends UnitSpec {
  val filter: MessagesFilter = new MessagesFilter {}
  val calcId = "someCalcId"

  "Calling filter" should {

    "return only error messages" when {

      "provided with an error filter" in {
        filter.filter(MessagesResponseFixture.messagesResponseFromBackendAllFields.as[JsObject], Seq(MessageType.error)) shouldBe MessagesResponseFixture.messagesResponseFromBackendErrors
      }
    }

    "return only warning messages" when {

      "provided with a warning filter" in {
        filter.filter(MessagesResponseFixture.messagesResponseFromBackendAllFields.as[JsObject], Seq(MessageType.warning)) shouldBe MessagesResponseFixture.messagesResponseFromBackendWarnings
      }
    }

    "return only info messages" when {

      "provided with an info filter" in {
        filter.filter(MessagesResponseFixture.messagesResponseFromBackendAllFields.as[JsObject], Seq(MessageType.info)) shouldBe MessagesResponseFixture.messagesResponseFromBackendInfo
      }
    }

    "return multiple message types" when {

      "provided with info and warning filters" in {
        filter.filter(MessagesResponseFixture.messagesResponseFromBackendAllFields.as[JsObject], Seq(MessageType.info, MessageType.warning)) shouldBe MessagesResponseFixture.backendJson(
          MessagesResponseFixture.messagesResponseFromBackendInfo.as[JsObject].deepMerge(MessagesResponseFixture.messagesResponseFromBackendWarnings.as[JsObject])
        )
      }

      "provided with info and error filters" in {
        filter.filter(MessagesResponseFixture.messagesResponseFromBackendAllFields.as[JsObject], Seq(MessageType.info, MessageType.error)) shouldBe MessagesResponseFixture.backendJson(
          MessagesResponseFixture.messagesResponseFromBackendInfo.as[JsObject].deepMerge(MessagesResponseFixture.messagesResponseFromBackendErrors.as[JsObject])
        )
      }

      "provided with error and warning filters" in {
        filter.filter(MessagesResponseFixture.messagesResponseFromBackendAllFields.as[JsObject], Seq(MessageType.error, MessageType.warning)) shouldBe MessagesResponseFixture.backendJson(
          MessagesResponseFixture.messagesResponseFromBackendErrors.as[JsObject].deepMerge(MessagesResponseFixture.messagesResponseFromBackendWarnings.as[JsObject])
        )
      }
    }

    "return the original messages object" when {

      "provided with no filter" in {
        filter.filter(MessagesResponseFixture.messagesResponseFromBackendAllFields.as[JsObject], Seq()) shouldBe MessagesResponseFixture.messagesResponseFromBackendAllFields
      }

      "provided with filters for every type" in {
        filter.filter(MessagesResponseFixture.messagesResponseFromBackendAllFields.as[JsObject], Seq(MessageType.info, MessageType.warning, MessageType.error)) shouldBe MessagesResponseFixture.messagesResponseFromBackendAllFields
      }
    }

    "return an empty message set" when {

      "provided with an error filter and no error messages" in {
        filter.filter(MessagesResponseFixture.messagesResponseFromBackendInfo.as[JsObject], Seq(MessageType.error)) shouldBe MessagesResponseFixture.messagesResponseFromBackendNoMessages.as[JsObject]
      }

      "provided with a warning filter and no warning messages" in {
        filter.filter(MessagesResponseFixture.messagesResponseFromBackendErrors.as[JsObject], Seq(MessageType.warning)) shouldBe MessagesResponseFixture.messagesResponseFromBackendNoMessages.as[JsObject]
      }

      "provided with an info filter and no info messages" in {
        filter.filter(MessagesResponseFixture.messagesResponseFromBackendErrors.as[JsObject], Seq(MessageType.info)) shouldBe MessagesResponseFixture.messagesResponseFromBackendNoMessages.as[JsObject]
      }

      "provided with only an invalid filter" in {
        filter.filter(MessagesResponseFixture.messagesResponseFromBackendAllFields.as[JsObject], Seq(MessageType.none)) shouldBe MessagesResponseFixture.messagesResponseFromBackendNoMessages.as[JsObject]
      }
    }
  }
}
