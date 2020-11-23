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

//noinspection ScalaStyle
object PayPensionsProfitFixture {

  val incomeReceivedPPP: BigInt = 1
  val taxableIncomePPP: BigInt = 2
  val totalSelfEmploymentProfit: Option[BigInt] = Some(3)
  val totalPropertyProfit: Option[BigInt] = Some(1)
  val totalFHLPropertyProfit: Option[BigInt] = Some(2)
  val totalUKOtherPropertyProfit: Option[BigInt] = Some(3)

  val totalForeignPropertyProfit: Option[BigInt] = Some(1)
  val totalEeaFhlProfit: Option[BigInt] = Some(4)
  val totalOccupationalPensionIncome: Option[BigDecimal] = Some(2)
  val totalStateBenefitsIncome: Option[BigDecimal] = Some(5)
  val totalBenefitsInKind: Option[BigDecimal] = Some(9)
  val totalPayeEmploymentAndLumpSumIncome: Option[BigDecimal] = Some(1)
  val totalEmploymentExpenses: Option[BigDecimal] = Some(4)
  val totalEmploymentIncome: Option[BigInt] = Some(2)

  val payPensionsProfitJson: JsValue = Json.parse(
    s"""
       |{
       |    "incomeReceived" : $incomeReceivedPPP,
       |    "taxableIncome" : $taxableIncomePPP,
       |    "totalSelfEmploymentProfit" : ${totalSelfEmploymentProfit.get},
       |    "totalPropertyProfit" : ${totalPropertyProfit.get},
       |    "totalFHLPropertyProfit" : ${totalFHLPropertyProfit.get},
       |    "totalUKOtherPropertyProfit" : ${totalUKOtherPropertyProfit.get},
       |    "totalForeignPropertyProfit" : ${totalForeignPropertyProfit.get},
       |    "totalEeaFhlProfit" : ${totalEeaFhlProfit.get},
       |    "totalOccupationalPensionIncome" : ${totalOccupationalPensionIncome.get},
       |    "totalStateBenefitsIncome" : ${totalStateBenefitsIncome.get},
       |    "totalBenefitsInKind" : ${totalBenefitsInKind.get},
       |    "totalPayeEmploymentAndLumpSumIncome" : ${totalPayeEmploymentAndLumpSumIncome.get},
       |    "totalEmploymentExpenses" : ${totalEmploymentExpenses.get},
       |    "totalEmploymentIncome" : ${totalEmploymentIncome.get},
       |    "businessProfitAndLoss" : $businessProfitAndLossJson
       |}
    """.stripMargin
  )
}
