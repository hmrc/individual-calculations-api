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

package v1.fixtures.getCalculationMessages

import play.api.libs.json.{JsValue, Json}
import v1.models.response.getCalculationMessages.{CalculationMessages, Message}

object MessageFixture {
  val mtdJson: JsValue = Json.parse(
    """
      |{
      |  "id":"info1",
      |  "text":"text1"
      |}
    """.stripMargin)
}

object GetCalculationMessagesFixture {

  val err1: Message = Message("err1", "text1")
  val err2: Message = Message("err2", "text2")
  val info1: Message = Message("info1", "text1")
  val info2: Message = Message("info2", "text2")
  val warn1: Message = Message("warn1", "text1")
  val warn2: Message = Message("warn2", "text2")

  val response: CalculationMessages = CalculationMessages(
    Some(Seq(info1, info2)),
    Some(Seq(warn1, warn2)),
    Some(Seq(err1, err2))
  )

  val mtdJson: JsValue = Json.parse(
    """{
      |  "info":[
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
    """.stripMargin)

  val backendNoMessagesJson: JsValue = Json.parse(
    """
      |{
      |  "messages" :{
      |  }
      |}
    """.stripMargin)

  val backendMessagesInfoJson: JsValue = Json.parse(
    """
      |{
      |  "messages" :{
      |     "info":[
      |     {"id":"info1", "text":"text1"},
      |     {"id":"info2", "text":"text2"}
      |     ]
      |  }
      |}
    """.stripMargin)

  val outputMessagesJson: JsValue = Json.parse(
    """
      |{
      |  "info":[
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
    """.stripMargin)

  val outputMessagesErrorsJson: JsValue = Json.parse(
    """
      |{
      |  "errors":[
      |    {"id":"err1", "text":"text1"},
      |    {"id":"err2", "text":"text2"}
      |  ]
      |}
    """.stripMargin)
}
