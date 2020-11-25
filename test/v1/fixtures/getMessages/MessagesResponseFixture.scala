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

package v1.fixtures.getMessages

import play.api.libs.json.{JsValue, Json}

object MessagesResponseFixture {

  val calculationId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"

  def backendJson(messagesResponse: JsValue, errorCount: Int = 0): JsValue = Json.obj(
    "data" -> Json.obj(
      "metadata" -> Json.obj(
        "id" -> calculationId,
        "calculationErrorCount" -> errorCount
      ),
      "messages" -> messagesResponse
    )
  )


  val err1: JsValue = Json.obj("id" -> "err1", "text" -> "text1")
  val err2: JsValue = Json.obj("id" -> "err2", "text" -> "text2")
  val info1: JsValue = Json.obj("id" -> "info1", "text" -> "text1")
  val info2: JsValue = Json.obj("id" -> "info2", "text" -> "text2")
  val warn1: JsValue = Json.obj("id" -> "warn1", "text" -> "text1")
  val warn2: JsValue = Json.obj("id" -> "warn2", "text" -> "text2")

  val messagesResponseJson: JsValue = Json.parse(
    s"""
       |{
       |  "info": [
       |    {"id":"info1", "text":"text1"},
       |    {"id":"info2", "text":"text2"}
       |  ],
       |  "warnings":[
       |    {"id":"warn1", "text":"text1"},
       |    {"id":"warn2", "text":"text2"}
       |  ],
       |  "errors":[
       |    {"id":"err1", "text":"text1"},
       |    {"id":"err2", "text":"text2"}
       |  ]
       |}
    """.stripMargin
  )

  val messagesResponseJsonErrors: JsValue = Json.parse(
    """
      |{
      |  "errors":[
      |    {"id":"err1", "text":"text1"},
      |    {"id":"err2", "text":"text2"}
      |  ]
      |}
    """.stripMargin
  )

  val messagesResponseJsonWarnings: JsValue = Json.parse(
    """
      |{
      |  "warnings":[
      |    {"id":"warn1", "text":"text1"},
      |    {"id":"warn2", "text":"text2"}
      |  ]
      |}
    """.stripMargin
  )

  val messagesResponseJsonInfo: JsValue = Json.parse(
    """
      |{
      |  "info": [
      |    {"id":"info1", "text":"text1"},
      |    {"id":"info2", "text":"text2"}
      |  ]
      |}
    """.stripMargin
  )

  val messagesResponseFromBackendAllFields: JsValue = backendJson(messagesResponseJson)
  val messagesResponseFromBackendErrors: JsValue = backendJson(messagesResponseJsonErrors)
  val messagesResponseFromBackendWarnings: JsValue = backendJson(messagesResponseJsonWarnings)
  val messagesResponseFromBackendInfo: JsValue = backendJson(messagesResponseJsonInfo)
  val messagesResponseFromBackendNoMessages: JsValue = backendJson(Json.obj())
}
