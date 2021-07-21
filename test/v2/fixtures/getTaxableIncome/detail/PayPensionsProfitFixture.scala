/*
 * Copyright 2021 HM Revenue & Customs
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

package v2.fixtures.getTaxableIncome.detail

import play.api.libs.json.{JsValue, Json}
import v2.fixtures.getTaxableIncome.detail.BusinessProfitAndLossFixture._
import v2.models.response.getTaxableIncome.detail.PayPensionsProfit

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
  val totalSeafarersDeduction: Option[BigDecimal] = Some(10)
  val totalForeignTaxOnForeignEmployment: Option[BigDecimal] = Some(11)
  val totalEmploymentIncome: Option[BigInt] = Some(2)
  val totalShareSchemesIncome: Option[BigDecimal] = Some(12)
  val totalOverseasPensionsStateBenefitsRoyalties: Option[BigDecimal] = Some(13)
  val totalAllOtherIncomeReceivedWhilstAbroad: Option[BigDecimal] = Some(14)
  val totalOverseasIncomeAndGains: Option[BigDecimal] = Some(15)
  val totalForeignBenefitsAndGifts: Option[BigDecimal] = Some(16)
  val tipsIncome: Option[BigDecimal] = Some(17)

  val payPensionsProfitModel: PayPensionsProfit =
    PayPensionsProfit(
      incomeReceived = incomeReceivedPPP,
      taxableIncome = taxableIncomePPP,
      totalSelfEmploymentProfit = totalSelfEmploymentProfit,
      totalPropertyProfit = totalPropertyProfit,
      totalFHLPropertyProfit = totalFHLPropertyProfit,
      totalUKOtherPropertyProfit = totalUKOtherPropertyProfit,
      totalForeignPropertyProfit = totalForeignPropertyProfit,
      totalEeaFhlProfit = totalEeaFhlProfit,
      totalOccupationalPensionIncome = totalOccupationalPensionIncome,
      totalStateBenefitsIncome = totalStateBenefitsIncome,
      totalBenefitsInKind = totalBenefitsInKind,
      totalPayeEmploymentAndLumpSumIncome = totalPayeEmploymentAndLumpSumIncome,
      totalEmploymentExpenses = totalEmploymentExpenses,
      totalSeafarersDeduction = totalSeafarersDeduction,
      totalForeignTaxOnForeignEmployment = totalForeignTaxOnForeignEmployment,
      totalEmploymentIncome = totalEmploymentIncome,
      totalShareSchemesIncome = totalShareSchemesIncome,
      totalOverseasPensionsStateBenefitsRoyalties = totalOverseasPensionsStateBenefitsRoyalties,
      totalAllOtherIncomeReceivedWhilstAbroad = totalAllOtherIncomeReceivedWhilstAbroad,
      totalOverseasIncomeAndGains = totalOverseasIncomeAndGains,
      totalForeignBenefitsAndGifts = totalForeignBenefitsAndGifts,
      tipsIncome = tipsIncome,
      businessProfitAndLoss = Some(businessProfitAndLossModel)
    )

  val payPensionsProfitJson: JsValue = Json.parse(
    s"""
       |{
       |    "incomeReceived": $incomeReceivedPPP,
       |    "taxableIncome": $taxableIncomePPP,
       |    "totalSelfEmploymentProfit": ${totalSelfEmploymentProfit.get},
       |    "totalPropertyProfit": ${totalPropertyProfit.get},
       |    "totalFHLPropertyProfit": ${totalFHLPropertyProfit.get},
       |    "totalUKOtherPropertyProfit": ${totalUKOtherPropertyProfit.get},
       |    "totalForeignPropertyProfit": ${totalForeignPropertyProfit.get},
       |    "totalEeaFhlProfit": ${totalEeaFhlProfit.get},
       |    "totalOccupationalPensionIncome": ${totalOccupationalPensionIncome.get},
       |    "totalStateBenefitsIncome": ${totalStateBenefitsIncome.get},
       |    "totalBenefitsInKind": ${totalBenefitsInKind.get},
       |    "totalPayeEmploymentAndLumpSumIncome": ${totalPayeEmploymentAndLumpSumIncome.get},
       |    "totalEmploymentExpenses": ${totalEmploymentExpenses.get},
       |    "totalSeafarersDeduction": ${totalSeafarersDeduction.get},
       |    "totalForeignTaxOnForeignEmployment": ${totalForeignTaxOnForeignEmployment.get},
       |    "totalEmploymentIncome": ${totalEmploymentIncome.get},
       |    "totalShareSchemesIncome": ${totalShareSchemesIncome.get},
       |    "totalOverseasPensionsStateBenefitsRoyalties": ${totalOverseasPensionsStateBenefitsRoyalties.get},
       |    "totalAllOtherIncomeReceivedWhilstAbroad": ${totalAllOtherIncomeReceivedWhilstAbroad.get},
       |    "totalOverseasIncomeAndGains": ${totalOverseasIncomeAndGains.get},
       |    "totalForeignBenefitsAndGifts": ${totalForeignBenefitsAndGifts.get},
       |    "tipsIncome": ${tipsIncome.get},
       |    "businessProfitAndLoss": $businessProfitAndLossJson
       |}
     """.stripMargin
  )
}