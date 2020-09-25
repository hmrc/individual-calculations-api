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

import play.api.libs.json.{JsValue, Json}

object MetadataResponseFixture {

  def backendJson(metadata: JsValue): JsValue = Json.obj(
    "data" -> Json.obj(
      "metadata" -> metadata
    )
  )

  def metadataJson(errorCount: Int = 0): JsValue = Json.obj(
    "id" -> "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
    "calculationErrorCount" -> errorCount
  )

  val metadataJsonFromBackend: JsValue = backendJson(metadataJson())
  val metadataJsonFromBackendWithErrors: JsValue = backendJson(metadataJson(1))
}
