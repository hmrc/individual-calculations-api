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

package v1.fixtures.getTaxableIncome.ukPropertyFhl

import play.api.libs.json.{JsValue, Json}
import v1.models.response.getTaxableIncome.detail.businessProfitAndLoss.ukPropertyFhl.detail.LossBroughtForward

object LossBroughtForwardFixtures {
  val lossId: String = "0yriP9QrW2jTa6n"
  val incomeSourceId: String = "AAIS12345678904"
  val incomeSourceType: String = "04"
  val submissionTimestamp: String = "2019-07-13T07:51:43Z"
  val taxYearLossIncurred: String = "2054-55"
  val currentLossValue: BigInt = 673350334
  val mtdLoss: Boolean = false

  val lossBroughtForwardResponse: LossBroughtForward =
    LossBroughtForward(taxYearLossIncurred, currentLossValue, mtdLoss)

  val lossBroughtForwardJson: JsValue = Json.parse(
    f"""{
       |    "taxYearLossIncurred": "$taxYearLossIncurred",
       |    "currentLossValue": $currentLossValue,
       |    "mtdLoss": $mtdLoss
       |}""".stripMargin)

  val lossBroughtForwardInvalidJson: JsValue = Json.parse(
    f"""{
       |    "taxYearLossIncurred": "$taxYearLossIncurred}",
       |    "currentLossValue": $currentLossValue,
       |    "mtdLoss": $mtdLoss
       |}""".stripMargin)
}
