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

package v1.fixtures.getTaxableIncome.detail.ukPropertyFhl.detail

import play.api.libs.json.{JsValue, Json}
import v1.models.response.getTaxableIncome.detail.ukPropertyFhl.detail.DefaultCarriedForwardLoss

object DefaultCarriedForwardLossFixtures {

  val incomeSourceId: String = "AAIS12345678904"
  val taxYearLossIncurred: String = "2044-45"
  val currentLossValue: BigInt = 49177

  val defaultCarriedForwardLossModel: DefaultCarriedForwardLoss =
    DefaultCarriedForwardLoss(
      taxYearLossIncurred = taxYearLossIncurred,
      currentLossValue = currentLossValue
    )

  val defaultCarriedForwardLossJson: JsValue = Json.parse(
    f"""
       |{
       |    "taxYearLossIncurred": "$taxYearLossIncurred",
       |    "currentLossValue": $currentLossValue
       |}
    """.stripMargin
  )
}