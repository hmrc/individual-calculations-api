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

package v1.fixtures.getMetadata

import play.api.libs.json.{JsObject, JsValue, Json}

object MetadataResponseFixture {

  val calcId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"

<<<<<<< HEAD
  def backendJson(metadata: JsValue): JsValue = Json.obj(
    "data" -> Json.obj(
      "metadata" -> metadata
=======
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
>>>>>>> 1cfe0a3f2a02146d80c23d4b6db3af2f2dfbc8d9
    )
  )

  def metadataJson(errorCount: Int = 0, crystallised: Boolean = false): JsValue = Json.obj(
    "id" -> "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
    "calculationErrorCount" -> errorCount,
    "crystallised" -> crystallised,
    "calculationType" -> {
      if(crystallised) "crystallisation" else "inYear"
    }
  )

  def metadataJsonWithoutErrorCount(crystallised: Boolean = false): JsValue = Json.obj(
    "id" -> "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
    "crystallised" -> crystallised,
    "calculationType" -> {
      if(crystallised) "crystallisation" else "inYear"
    }
  )

  val metadataJsonFromBackend: JsValue = backendJson(metadataJson())
  val metadataJsonFromBackendWithoutErrorCount: JsValue = backendJson(metadataJson().as[JsObject] - "calculationErrorCount")
  val metadataJsonFromBackendWithErrors: JsValue = backendJson(metadataJson(1))
  val metadataJsonFromBackendCrystallised: JsValue = backendJson(metadataJson(crystallised = true))
}
