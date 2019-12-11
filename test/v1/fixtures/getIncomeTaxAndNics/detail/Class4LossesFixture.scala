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

package v1.fixtures.getIncomeTaxAndNics.detail

import play.api.libs.json.{JsValue, Json}
import v1.models.response.getIncomeTaxAndNics.detail.Class4Losses

object Class4LossesFixture {

  val class4LossesModel: Class4Losses =
    Class4Losses(
      totalClass4LossesAvailable = Some(3001),
      totalClass4LossesUsed = Some(3002),
      totalClass4LossesCarriedForward = Some(3003)
    )

  val class4LossesJson: JsValue = Json.parse(
    """
      |{
      | "totalClass4LossesAvailable" : 3001,
      | "totalClass4LossesUsed" : 3002,
      | "totalClass4LossesCarriedForward" : 3003
      |}
    """.stripMargin
  )
}