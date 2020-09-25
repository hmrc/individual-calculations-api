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

package v1.fixtures.getEndOfYearEstimate

import play.api.libs.json.{JsValue, Json}

object EoyEstimateResponseFixture {

  def backendJson(endOfYearEstimateResponse: JsValue, errorCount: Int = 0, calculationType: String = "inYear"): JsValue = Json.obj(
    "data" -> Json.obj(
      "metadata" -> Json.obj(
        "id" -> "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
        "calculationErrorCount" -> errorCount,
        "calculationType" -> calculationType
      ),
      "endOfYearEstimate" -> endOfYearEstimateResponse
    )
  )

  val eoyEstimateResponseJson: JsValue = Json.parse(
    """
      |{
      |  "summary": {
      |    "foo": "bar"
      |  },
      |  "details": {
      |    "foo": "bar"
      |  }
      |}
      |""".stripMargin
  )

  val eoyEstimateResponseJsonFromBackend: JsValue = backendJson(eoyEstimateResponseJson)

  val eoyEstimateResponseJsonWithErrorFromBackend: JsValue = backendJson(eoyEstimateResponseJson, errorCount = 1)
}
