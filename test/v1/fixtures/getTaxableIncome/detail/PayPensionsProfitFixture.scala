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

package v1.fixtures.getTaxableIncome.detail

import play.api.libs.json.{JsValue, Json}
import v1.fixtures.getTaxableIncome.detail.BusinessProfitAndLossFixture._
import v1.models.response.getTaxableIncome.detail.PayPensionsProfit

object PayPensionsProfitFixture {

  val incomeReceivedPPP: BigInt = 1
  val taxableIncomePPP: BigInt = 2
  val totalSelfEmploymentProfit: Option[BigInt] = Some(3)
  val totalPropertyProfit: Option[BigInt] = Some(1)
  val totalFHLPropertyProfit: Option[BigInt] = Some(2)
  val totalUKOtherPropertyProfit: Option[BigInt] = Some(3)

  val payPensionsProfitModel: PayPensionsProfit =
    PayPensionsProfit(
      incomeReceived = incomeReceivedPPP,
      taxableIncome = taxableIncomePPP,
      totalSelfEmploymentProfit = totalSelfEmploymentProfit,
      totalPropertyProfit = totalPropertyProfit,
      totalFHLPropertyProfit = totalFHLPropertyProfit,
      totalUKOtherPropertyProfit = totalUKOtherPropertyProfit,
      businessProfitAndLoss = Some(businessProfitAndLossModel)
    )

  val payPensionsProfitJson: JsValue = Json.parse(
    s"""
       |{
       |    "incomeReceived" : $incomeReceivedPPP,
       |    "taxableIncome" : $taxableIncomePPP,
       |    "totalSelfEmploymentProfit" : ${totalSelfEmploymentProfit.get},
       |    "totalPropertyProfit" : ${totalPropertyProfit.get},
       |    "totalFHLPropertyProfit" : ${totalFHLPropertyProfit.get},
       |    "totalUKOtherPropertyProfit" : ${totalUKOtherPropertyProfit.get},
       |    "businessProfitAndLoss" : $businessProfitAndLossJson
       |}
    """.stripMargin
  )
}