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

package v4.models.response.triggerCalculation

import play.api.libs.json.{JsValue, Json}
import shared.utils.UnitSpec

class TriggerCalculationResponseSpec extends UnitSpec {
  private val calculationId                                       = "testId"
  val triggerCalculationResponseModel: TriggerCalculationResponse = TriggerCalculationResponse(calculationId)

  val downstreamResponseJson: JsValue = Json.parse(
    s"""
       |{
       | "id": "$calculationId"
       |}
    """.stripMargin
  )

  val vendorResponseJson: JsValue = Json.parse(
    s"""
       |{
       | "calculationId": "$calculationId"
       |}
    """.stripMargin
  )

  "TriggerCalculationResponse" when {
    "read from valid JSON from the downstream response" should {
      "produce the expected TriggerCalculationResponse object" in {
        downstreamResponseJson.as[TriggerCalculationResponse] shouldBe triggerCalculationResponseModel
      }
    }

    "written to JSON" should {
      "produce the expected JsObject" in {
        Json.toJson(triggerCalculationResponseModel) shouldBe vendorResponseJson
      }
    }
  }

}
