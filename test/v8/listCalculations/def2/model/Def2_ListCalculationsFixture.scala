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

package v8.listCalculations.def2.model

import play.api.libs.json.{JsValue, Json}
import v8.listCalculations.def2.model.response.Def2_Calculation
import v8.listCalculations.model.response.{Calculation, Def2_ListCalculationsResponse, ListCalculationType, ListCalculationsResponse}

trait Def2_ListCalculationsFixture {

  val listCalculationsDownstreamJson: JsValue = Json.parse("""
                                                             |{
                                                             |  "calculationsSummary": [
                                                             |    {
                                                             |      "calculationId": "c432a56d-e811-474c-a26a-76fc3bcaefe5",
                                                             |      "calculationTimestamp": "2023-10-31T12:55:51.159Z",
                                                             |      "calculationType": "IY",
                                                             |      "calculationTrigger": "attended",
                                                             |      "calculationOutcome": "PROCESSED",
                                                             |      "liabilityAmount": 12345.67
                                                             |    }
                                                             |  ]
                                                             |}
    """.stripMargin)

  val listCalculationsMtdJson: JsValue = Json.parse("""
                                                      |{
                                                      |   "calculations": [
                                                      |      {
                                                      |         "calculationId":"c432a56d-e811-474c-a26a-76fc3bcaefe5",
                                                      |         "calculationTimestamp":"2023-10-31T12:55:51.159Z",
                                                      |         "calculationType":"in-year",
                                                      |         "calculationTrigger": "attended",
                                                      |         "calculationOutcome": "PROCESSED",
                                                      |         "liabilityAmount": 12345.67
                                                      |      }
                                                      |   ]
                                                      |}
    """.stripMargin)

  val calculationModel: Def2_Calculation = Def2_Calculation(
    calculationId = "c432a56d-e811-474c-a26a-76fc3bcaefe5",
    calculationTimestamp = "2023-10-31T12:55:51.159Z",
    calculationType = ListCalculationType.`in-year`,
    calculationTrigger = "attended",
    calculationOutcome = "PROCESSED",
    liabilityAmount = Some(12345.67)
  )

  val listCalculationsResponseModel: ListCalculationsResponse[Calculation] =
    Def2_ListCalculationsResponse(Seq(calculationModel))

}
