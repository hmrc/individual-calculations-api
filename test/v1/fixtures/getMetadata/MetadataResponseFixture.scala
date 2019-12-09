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

package v1.fixtures.getMetadata

import play.api.libs.json.{JsValue, Json}
import v1.models.response.common.{CalculationReason, CalculationRequestor, CalculationType}
import v1.models.response.getMetadata.MetadataResponse

object MetadataResponseFixture {

  val metadata = MetadataResponse(
    id = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
    taxYear = "2018-19",
    requestedBy = CalculationRequestor.customer,
    calculationReason = CalculationReason.customerRequest,
    calculationTimestamp = Some("2019-11-15T09:35:15.094Z"),
    calculationType = CalculationType.crystallisation,
    intentToCrystallise = true,
    crystallised = false,
    totalIncomeTaxAndNicsDue = None,
    calculationErrorCount = Some(123)
  )

  val errorBodyFromBackEnd: JsValue = Json.parse(
    s"""
      |{
      |  "metadata": {
      |    "id": "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
      |    "taxYear": "2018-19",
      |    "requestedBy": "customer",
      |    "calculationReason": "customerRequest",
      |    "calculationTimestamp": "2019-11-15T09:35:15.094Z",
      |    "calculationType": "crystallisation",
      |    "intentToCrystallise": true,
      |    "crystallised": false,
      |    "calculationErrorCount": 2
      |  },
      |  "messages" :{
      |     "errors":[
      |        {"id":"err1", "text":"text1"},
      |        {"id":"err2", "text":"text2"}
      |     ]
      |  }
      |}
    """.stripMargin)

  val mtdJson: JsValue = Json.parse(
    """
      |{
      |  "metadata": {
      |    "id": "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
      |    "taxYear": "2018-19",
      |    "requestedBy": "customer",
      |    "calculationReason": "customerRequest",
      |    "calculationTimestamp": "2019-11-15T09:35:15.094Z",
      |    "calculationType": "crystallisation",
      |    "intentToCrystallise": true,
      |    "crystallised": false,
      |    "calculationErrorCount": 123
      |  }
      |}
  """.stripMargin)

  val jsonOutput: JsValue = Json.parse(
    """
      |{
      |    "id": "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
      |    "taxYear": "2018-19",
      |    "requestedBy": "customer",
      |    "calculationReason": "customerRequest",
      |    "calculationTimestamp": "2019-11-15T09:35:15.094Z",
      |    "calculationType": "crystallisation",
      |    "intentToCrystallise": true,
      |    "crystallised": false,
      |    "calculationErrorCount": 123
      |}
  """.stripMargin)
}