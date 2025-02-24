/*
 * Copyright 2023 HM Revenue & Customs
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

package v8.listCalculations.def3.model

import play.api.libs.json.{JsValue, Json}
import v8.listCalculations.def3.model.response.Def3_Calculation
import v8.listCalculations.model.response.{Calculation, Def3_ListCalculationsResponse, ListCalculationType, ListCalculationsResponse}

trait Def3_ListCalculationsFixture {

  val listCalculationsDownstreamJson: JsValue = Json.parse("""
                                                             |{
                                                             |  "calculationsSummary": [
                                                             |    {
                                                             |      "calculationId": "c432a56d-e811-474c-a26a-76fc3bcaefe5",
                                                             |      "calculationTimestamp": "2025-10-31T12:55:51.159Z",
                                                             |      "calculationType": "IY",
                                                             |      "calculationTrigger": "attended",
                                                             |      "calculationOutcome": "PROCESSED",
                                                             |      "liabilityAmount": 12345.67,
                                                             |      "fromDate": "2025-04-06",
                                                             |      "toDate": "2026-04-05"
                                                             |    }
                                                             |  ]
                                                             |}
    """.stripMargin)

  val listCalculationsMtdJson: JsValue = Json.parse("""
                                                      |{
                                                      |   "calculations": [
                                                      |      {
                                                      |         "calculationId":"c432a56d-e811-474c-a26a-76fc3bcaefe5",
                                                      |         "calculationTimestamp":"2025-10-31T12:55:51.159Z",
                                                      |         "calculationType":"in-year",
                                                      |         "calculationTrigger": "attended",
                                                      |         "calculationOutcome": "PROCESSED",
                                                      |         "liabilityAmount": 12345.67,
                                                      |         "fromDate": "2025-04-06",
                                                      |         "toDate": "2026-04-05"
                                                      |      }
                                                      |   ]
                                                      |}
    """.stripMargin)

  val calculationModel: Def3_Calculation = Def3_Calculation(
    calculationId = "c432a56d-e811-474c-a26a-76fc3bcaefe5",
    calculationTimestamp = "2025-10-31T12:55:51.159Z",
    calculationType = ListCalculationType.`in-year`,
    calculationTrigger = "attended",
    calculationOutcome = "PROCESSED",
    liabilityAmount = Some(12345.67),
    fromDate = Some("2025-04-06"),
    toDate = Some("2026-04-05")
  )

  val listCalculationsResponseModel: ListCalculationsResponse[Calculation] =
    Def3_ListCalculationsResponse(Seq(calculationModel))

}
