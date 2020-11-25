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

import play.api.libs.json.{JsObject, JsValue, Json}
import v1.fixtures.getEndOfYearEstimate.detail.EoyEstimateDetailFixture._
import v1.fixtures.getEndOfYearEstimate.summary.EoyEstimateSummaryFixture._
import v1.models.response.getEoyEstimate.EoyEstimateResponse
import v1.fixtures.getMetadata.MetadataResponseFixture._

object EoyEstimateResponseFixture {

  val eoyEstimateResponseModel: EoyEstimateResponse =
    EoyEstimateResponse(
      summary = eoyEstimateSummaryModel,
      detail = eoyEstimateDetailModel,
      id = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"
    )

  val eoyEstimateResponseJson: JsObject = Json.parse(
    s"""
       |{
       |  "summary" : $eoyEstimateSummaryJson,
       |  "detail" : $eoyEstimateDetailJson
       |}
    """.stripMargin
  ).as[JsObject]

  val eoyEstimateResponseTopLevelJson: JsValue = Json.parse(
    s"""
       |{
       | "metadata": $metadataResponseJson,
       | "endOfYearEstimate" : {
       |   "summary" : $eoyEstimateSummaryJson,
       |   "detail" : $eoyEstimateDetailJson
       | }
       |}
    """.stripMargin
  )
}
