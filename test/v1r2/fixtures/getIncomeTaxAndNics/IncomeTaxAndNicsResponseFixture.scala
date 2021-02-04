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

package v1r2.fixtures.getIncomeTaxAndNics

import play.api.libs.json.{JsValue, Json}
import v1r2.fixtures.getIncomeTaxAndNics.detail.CalculationDetailFixture._
import v1r2.fixtures.getIncomeTaxAndNics.summary.CalculationSummaryFixture._
import v1r2.fixtures.getMetadata.MetadataResponseFixture._
import v1r2.models.response.calculationWrappers.CalculationWrapperOrError
import v1r2.models.response.calculationWrappers.CalculationWrapperOrError._
import v1r2.models.response.getIncomeTaxAndNics.IncomeTaxAndNicsResponse

object IncomeTaxAndNicsResponseFixture {

  val fixtureCalculationId: String = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"

  val incomeTaxAndNicsResponseModel: IncomeTaxAndNicsResponse =
    IncomeTaxAndNicsResponse(
      summary = calculationSummaryModel,
      detail = calculationDetailModel,
      id = fixtureCalculationId
    )

  val wrappedIncomeTaxAndNicsResponseModel: CalculationWrapperOrError[IncomeTaxAndNicsResponse] =
    CalculationWrapper(incomeTaxAndNicsResponseModel)

  val incomeTaxNicsResponseJson: JsValue = Json.parse(
    s"""
       |{
       |   "summary": $calculationSummaryJson,
       |   "detail": $calculationDetailJson,
       |   "id": "$fixtureCalculationId"
       |}
     """.stripMargin
  )

  val incomeTaxAndNicsResponseTopLevelJson: JsValue = Json.parse(
    s"""
       |{
       |  "metadata": $metadataResponseJson,
       |  "incomeTaxAndNicsCalculated": {
       |      "summary": $calculationSummaryJson,
       |      "detail": $calculationDetailJson
       |   }
       |}
     """.stripMargin
  )

  val errorResponseTopLevelJson: JsValue = Json.parse(
    s"""
       |{
       |  "metadata": {
       |    "id": "$fixtureCalculationId",
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
     """.stripMargin
  )
}