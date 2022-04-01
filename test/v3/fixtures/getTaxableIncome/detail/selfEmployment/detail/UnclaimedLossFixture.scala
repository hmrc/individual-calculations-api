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

package v3.fixtures.getTaxableIncome.detail.selfEmployment.detail

import play.api.libs.json.{JsValue, Json}
import v3.models.response.common.LossType
import v3.models.response.getTaxableIncome.detail.selfEmployment.detail.UnclaimedLoss

object UnclaimedLossFixture {

  val taxYearLossIncurred: String = "2051-52"
  val typeOfLoss: LossType = LossType.INCOME
  val currentLossValue: BigInt = 71438847594L

  val unclaimedLossModel: UnclaimedLoss =
    UnclaimedLoss(
      taxYearLossIncurred = taxYearLossIncurred,
      currentLossValue = currentLossValue,
      lossType = LossType.INCOME
    )

  val unclaimedLossJson: JsValue = Json.parse(
    s"""
       |{
       |   "taxYearLossIncurred": "$taxYearLossIncurred",
       |   "currentLossValue": $currentLossValue,
       |   "lossType": ${Json.toJson(typeOfLoss)}
       |}
    """.stripMargin
  )
}