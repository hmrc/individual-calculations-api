/*
 * Copyright 2026 HM Revenue & Customs
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

package v9.retrieveCalculation.def5.model.response.inputs

import api.utils.UnitSpec
import play.api.libs.json.{JsError, JsObject, Json}

class AttachmentSpec extends UnitSpec {

  private val attachmentDownstreamJson = Json.parse(
    """
      |{
      |  "fileName": "name.pdf",
      |  "storeId": "abcdef123456",
      |  "uploadTimestamp": "2026-11-12T09:14:29.418Z"
      |}
      |""".stripMargin
  )

  private val attachmentModel = Attachment(documentName = "name.pdf", uploadTimestamp = "2026-11-12T09:14:29.418Z")

  private val attachmentMtdJson = Json.parse(
    """
      |{
      |  "documentName": "name.pdf",
      |  "uploadTimestamp": "2026-11-12T09:14:29.418Z"
      |}
      |""".stripMargin
  )

  "Attachment" when {
    "read from valid JSON" should {
      "return the expected data object" in {
        val result = attachmentDownstreamJson.as[Attachment]
        result shouldBe attachmentModel
      }
    }

    "read from invalid JSON" should {
      "return a JsError" in {
        val result = JsObject.empty.validate[Attachment]
        result shouldBe a[JsError]
      }
    }

    "written to JSON" should {
      "return the expected JSON" in {
        val result = Json.toJson(attachmentModel)
        result shouldBe attachmentMtdJson
      }
    }
  }

}
