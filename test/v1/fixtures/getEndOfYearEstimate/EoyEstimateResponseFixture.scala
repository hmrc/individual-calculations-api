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

package v1.fixtures.getEndOfYearEstimate

import play.api.libs.json.{JsValue, Json}
import v1.models.response.getEndOfYearEstimate.EoyEstimateResponse

object EoyEstimateResponseFixture {

  val backendJson: JsValue = Json.parse(
    s"""
      |{
      | "metadata": {
      |    "id": "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
      |    "taxYear": "2018-19",
      |    "requestedBy": "customer",
      |    "calculationReason": "customerRequest",
      |    "calculationTimestamp": "2019-11-15T09:35:15.094Z",
      |    "calculationType": "inYear",
      |    "intentToCrystallise": true,
      |    "crystallised": false,
      |    "calculationErrorCount": 0
      |  },
      | "endOfYearEstimate" : {
      |   "summary" : ${EoyEstimateSummaryFixture.json.toString()},
      |   "detail" : ${EoyEstimateDetailFixture.json.toString()}
      | }
      |}
    """.stripMargin)

  val errorCalculationTypeJson: JsValue = Json.parse(
    s"""
       |{
       | "metadata": {
       |    "id": "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
       |    "taxYear": "2018-19",
       |    "requestedBy": "customer",
       |    "calculationReason": "customerRequest",
       |    "calculationTimestamp": "2019-11-15T09:35:15.094Z",
       |    "calculationType": "crystallisation",
       |    "intentToCrystallise": true,
       |    "crystallised": false,
       |    "calculationErrorCount": 0
       |  }
       |}
    """.stripMargin)

  val outputJson: JsValue = Json.parse(
    s"""
      |{
      |  "summary" : ${EoyEstimateSummaryFixture.json.toString()},
      |  "detail" : ${EoyEstimateDetailFixture.json.toString()}
      |}
    """.stripMargin)

  val model = EoyEstimateResponse(EoyEstimateSummaryFixture.model, EoyEstimateDetailFixture.model)
}
