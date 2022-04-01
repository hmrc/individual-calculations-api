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

package v3.fixtures.getTaxableIncome.detail.ukPropertyFhl.detail

import play.api.libs.json.{JsValue, Json}
import v3.models.response.getTaxableIncome.detail.ukPropertyFhl.detail.LossBroughtForward

object LossBroughtForwardFixture {

  val taxYearLossIncurred: String = "2054-55"
  val currentLossValue: BigInt = 673350334
  val mtdLoss: Boolean = false

  val lossBroughtForwardModel: LossBroughtForward =
    LossBroughtForward(
      taxYearLossIncurred = taxYearLossIncurred,
      currentLossValue = currentLossValue,
      mtdLoss = mtdLoss
    )

  val lossBroughtForwardJson: JsValue = Json.parse(
    s"""
       |{
       |   "taxYearLossIncurred": "$taxYearLossIncurred",
       |   "currentLossValue": $currentLossValue,
       |   "mtdLoss": $mtdLoss
       |}
     """.stripMargin
  )
}