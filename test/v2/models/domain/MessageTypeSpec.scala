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

package v2.models.domain

import MessageType._
import support.UnitSpec

class MessageTypeSpec extends UnitSpec {

  "MessageType" when {
    "toTypeClass" should {
      "return the correct MessageType object for valid message types" in {
        toTypeClass(`type` = "info") shouldBe MessageType.info
        toTypeClass(`type` = "warning") shouldBe MessageType.warning
        toTypeClass(`type` = "error") shouldBe MessageType.error
      }

      "return a none message type for invalid options" in {
        toTypeClass(`type` = "shminfo") shouldBe MessageType.none
      }
    }
  }

}
