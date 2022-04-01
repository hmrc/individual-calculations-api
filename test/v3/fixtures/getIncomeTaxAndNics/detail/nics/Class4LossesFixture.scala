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

package v3.fixtures.getIncomeTaxAndNics.detail.nics

import play.api.libs.json.{JsValue, Json}
import v3.models.response.getIncomeTaxAndNics.detail.nics.Class4Losses

object Class4LossesFixture {

  val totalClass4LossesAvailable: Option[BigInt] = Some(3001)
  val totalClass4LossesUsed: Option[BigInt] = Some(3002)
  val totalClass4LossesCarriedForward: Option[BigInt] = Some(3003)

  val class4LossesModel: Class4Losses =
    Class4Losses(
      totalClass4LossesAvailable = totalClass4LossesAvailable,
      totalClass4LossesUsed = totalClass4LossesUsed,
      totalClass4LossesCarriedForward = totalClass4LossesCarriedForward
    )

  val class4LossesJson: JsValue = Json.parse(
    s"""
       |{
       |   "totalClass4LossesAvailable": ${totalClass4LossesAvailable.get},
       |   "totalClass4LossesUsed": ${totalClass4LossesUsed.get},
       |   "totalClass4LossesCarriedForward": ${totalClass4LossesCarriedForward.get}
       |}
     """.stripMargin
  )
}