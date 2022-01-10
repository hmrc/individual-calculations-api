/*
 * Copyright 2022 HM Revenue & Customs
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

package v2.fixtures.getMetadata

import play.api.libs.json.{JsObject, JsValue, Json}
import v2.fixtures.getMessages.MessagesResponseFixture._
import v2.models.response.common.{CalculationReason, CalculationRequestor, CalculationType}
import v2.models.response.getMetadata.MetadataResponse

object MetadataResponseFixture {

  val id: String = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"
  val taxYear: String = "2018-19"
  val requestedBy: CalculationRequestor = CalculationRequestor.customer
  val calculationReason: CalculationReason = CalculationReason.customerRequest
  val calculationTimestamp: Option[String] = Some("2019-11-15T09:35:15.094Z")
  val calculationType: CalculationType = CalculationType.inYear
  val intentToCrystallise: Boolean = true
  val crystallised: Boolean = false
  val totalIncomeTaxAndNicsDue: Option[BigDecimal] = None
  val calculationErrorCount: Option[Int] = None

  val metadataResponseModel: MetadataResponse =
    MetadataResponse(
      id = id,
      taxYear = taxYear,
      requestedBy = requestedBy,
      calculationReason = calculationReason,
      calculationTimestamp = calculationTimestamp,
      calculationType = calculationType,
      intentToCrystallise = intentToCrystallise,
      crystallised = crystallised,
      totalIncomeTaxAndNicsDue = totalIncomeTaxAndNicsDue,
      calculationErrorCount = calculationErrorCount,
      metadataExistence = None
    )

  val metadataResponseJson: JsValue = Json.parse(
    s"""
      |{
      |   "id": "$id",
      |   "taxYear": "$taxYear",
      |   "requestedBy": "$requestedBy",
      |   "calculationReason": "$calculationReason",
      |   "calculationTimestamp": "${calculationTimestamp.get}",
      |   "calculationType": "$calculationType",
      |   "intentToCrystallise": $intentToCrystallise,
      |   "crystallised": $crystallised
      |}
    """.stripMargin
  )

  val metadataResponseTopLevelJsonWithErrors: JsValue = Json.parse(
    s"""
       |{
       |   "metadata": {
       |      "id": "$id",
       |      "taxYear": "$taxYear",
       |      "requestedBy": "$requestedBy",
       |      "calculationReason": "$calculationReason",
       |      "calculationTimestamp": "${calculationTimestamp.get}",
       |      "calculationType": "$calculationType",
       |      "intentToCrystallise": $intentToCrystallise,
       |      "crystallised": $crystallised,
       |      "calculationErrorCount" : 2
       |   },
       |   "messages": $messagesResponseJsonErrors
       |}
     """.stripMargin
  )

  val metadataResponseTopLevelJsonWithoutErrors: JsObject =
    Json.obj("metadata" -> metadataResponseJson)

  val metadataResponseTopLevelJsonCrystallised: JsValue = Json.parse(
    s"""
       |{
       |   "metadata" : {
       |      "id": "$id",
       |      "taxYear": "$taxYear",
       |      "requestedBy": "$requestedBy",
       |      "calculationReason": "$calculationReason",
       |      "calculationTimestamp": "${calculationTimestamp.get}",
       |      "calculationType": "crystallisation",
       |      "intentToCrystallise": $intentToCrystallise,
       |      "crystallised": true
       |   }
       |}
     """.stripMargin
  )
}