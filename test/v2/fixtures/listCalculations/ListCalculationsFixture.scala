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

package v2.fixtures.listCalculations

import play.api.libs.json.{JsValue, Json}
import v2.models.response.common.{CalculationRequestor, CalculationType}
import v2.models.response.listCalculations.{CalculationListItem, ListCalculationsResponse}

object CalculationListItemFixture {

  val id: String = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"
  val calculationTimestamp: String = "2019-03-17T09:22:59Z"
  val `type`: CalculationType = CalculationType.inYear
  val requestedBy: Option[CalculationRequestor] = Some(CalculationRequestor.hmrc)

  val calculationListItemModel: CalculationListItem =
    CalculationListItem(
      id = id,
      calculationTimestamp = calculationTimestamp,
      `type` = `type`,
      requestedBy = requestedBy
    )

  val calculationListItemJson: JsValue = Json.parse(
    s"""
       |{
       |   "id": "$id",
       |   "calculationTimestamp": "$calculationTimestamp",
       |   "type": ${Json.toJson(`type`)},
       |   "requestedBy": ${Json.toJson(requestedBy.get)}
       |}
     """.stripMargin
  )
}

object ListCalculationsFixture {

  import v2.fixtures.listCalculations.CalculationListItemFixture._

  val listCalculationsResponseModel: ListCalculationsResponse[CalculationListItem] =
    ListCalculationsResponse(
      calculations = Seq(
        calculationListItemModel,
        CalculationListItem(
          id = "cf63c46a-1a4f-3c56-b9ea-9a82551d27bb",
          calculationTimestamp = "2019-06-17T18:45:59Z",
          `type` = CalculationType.crystallisation,
          requestedBy = None
        )
      )
    )

  val listCalculationsResponseJson: JsValue = Json.parse(
    s"""
      |{
      |  "calculations": [
      |    $calculationListItemJson,
      |    {
      |      "id": "cf63c46a-1a4f-3c56-b9ea-9a82551d27bb",
      |      "calculationTimestamp": "2019-06-17T18:45:59Z",
      |      "type": "crystallisation"
      |    }
      |  ]
      |}
    """.stripMargin
  )
}