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

package v1.fixtures.getMessages

import play.api.libs.json.{JsValue, Json}
import v1.models.response.common.{CalculationReason, CalculationRequestor, CalculationType}
import v1.models.response.getMessages.{Message, MessagesResponse}

object MessageFixture {

  val calcId: String = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"

  val messageJson: JsValue = Json.parse(
    """
      |{
      |  "id": "info1",
      |  "text": "text1"
      |}
    """.stripMargin
  )
}

object MessagesResponseFixture {
  val calcId : String = "someCalcId"

  val err1: Message = Message(id = "err1", text = "text1")
  val err2: Message = Message(id = "err2", text = "text2")

  val info1: Message = Message(id = "info1", text = "text1")
  val info2: Message = Message(id = "info2", text = "text2")

  val warn1: Message = Message(id = "warn1", text = "text1")
  val warn2: Message = Message(id = "warn2", text = "text2")

  val messagesResponseModel: MessagesResponse =
    MessagesResponse(
      info = Some(Seq(info1, info2)),
      warnings = Some(Seq(warn1, warn2)),
      errors = Some(Seq(err1, err2)), calcId
    )

  val messagesResponseJson: JsValue = Json.parse(
    """
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

  val messagesResponseTopLevelJson: JsValue = Json.parse(
    s"""
       |{
       |   "metadata": {
       |      "id": "$calcId",
       |      "taxYear": "2018-19",
       |      "requestedBy": "${CalculationRequestor.customer}",
       |      "calculationReason": "${CalculationReason.customerRequest}",
       |      "calculationTimestamp": "2019-11-15T09:35:15.094Z",
       |      "calculationType": "${CalculationType.inYear}",
       |      "intentToCrystallise": true,
       |      "crystallised": false
       |   },
       |   "messages": {
       |      "info": [
       |         {"id": "info1","text": "text1"},
       |         {"id": "info2","text": "text2"}
       |      ],
       |      "warnings": [
       |         {"id": "warn1","text": "text1"},
       |         {"id": "warn2","text": "text2"}
       |      ],
       |      "errors": [
       |         {"id": "err1", "text": "text1"},
       |         {"id": "err2", "text": "text2"}
       |      ]
       |   }
       |}
     """.stripMargin
  )

  val messagesResponseTopLevelJsonEmpty: JsValue = Json.parse(
    s"""
      |{
      |   "metadata": {
      |     "id": "$calcId",
      |     "taxYear": "2018-19",
      |     "requestedBy": "${CalculationRequestor.customer}",
      |     "calculationReason": "${CalculationReason.customerRequest}",
      |     "calculationTimestamp": "2019-11-15T09:35:15.094Z",
      |     "calculationType": "${CalculationType.inYear}",
      |     "intentToCrystallise": true,
      |     "crystallised": false
      |   },
      |  "messages": { }
      |}
    """.stripMargin
  )

  val messagesResponseTopLevelJsonInfo: JsValue = Json.parse(
    s"""
      |{
      |  "metadata": {
      |     "id": "$calcId",
      |     "taxYear": "2018-19",
      |     "requestedBy": "${CalculationRequestor.customer}",
      |     "calculationReason": "${CalculationReason.customerRequest}",
      |     "calculationTimestamp": "2019-11-15T09:35:15.094Z",
      |     "calculationType": "${CalculationType.inYear}",
      |     "intentToCrystallise": true,
      |     "crystallised": false
      |  },
      |  "messages": {
      |     "info":[
      |       {"id":"info1", "text":"text1"},
      |       {"id":"info2", "text":"text2"}
      |     ]
      |  }
      |}
    """.stripMargin
  )
}