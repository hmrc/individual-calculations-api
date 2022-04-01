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

package v3.models.response.getTaxableIncome.detail

import play.api.libs.functional.syntax._
import play.api.libs.json._

case class PayPensionsProfit(incomeReceived: BigInt,
                             taxableIncome: BigInt,
                             totalSelfEmploymentProfit: Option[BigInt],
                             totalPropertyProfit: Option[BigInt],
                             totalFHLPropertyProfit: Option[BigInt],
                             totalUKOtherPropertyProfit: Option[BigInt],
                             totalForeignPropertyProfit: Option[BigInt],
                             totalEeaFhlProfit: Option[BigInt],
                             totalOccupationalPensionIncome: Option[BigDecimal],
                             totalStateBenefitsIncome: Option[BigDecimal],
                             totalBenefitsInKind: Option[BigDecimal],
                             totalPayeEmploymentAndLumpSumIncome: Option[BigDecimal],
                             totalEmploymentExpenses: Option[BigDecimal],
                             totalSeafarersDeduction: Option[BigDecimal],
                             totalForeignTaxOnForeignEmployment: Option[BigDecimal],
                             totalEmploymentIncome: Option[BigInt],
                             totalShareSchemesIncome: Option[BigDecimal],
                             totalOverseasPensionsStateBenefitsRoyalties: Option[BigDecimal],
                             totalAllOtherIncomeReceivedWhilstAbroad: Option[BigDecimal],
                             totalOverseasIncomeAndGains: Option[BigDecimal],
                             totalForeignBenefitsAndGifts: Option[BigDecimal],
                             tipsIncome: Option[BigDecimal],
                             businessProfitAndLoss: Option[BusinessProfitAndLoss])

object PayPensionsProfit {
  private case class PayPensionsProfitPart1(incomeReceived: BigInt,
                                            taxableIncome: BigInt,
                                            totalSelfEmploymentProfit: Option[BigInt],
                                            totalPropertyProfit: Option[BigInt],
                                            totalFHLPropertyProfit: Option[BigInt],
                                            totalUKOtherPropertyProfit: Option[BigInt],
                                            totalForeignPropertyProfit: Option[BigInt],
                                            totalEeaFhlProfit: Option[BigInt],
                                            totalOccupationalPensionIncome: Option[BigDecimal],
                                            totalStateBenefitsIncome: Option[BigDecimal],
                                            totalBenefitsInKind: Option[BigDecimal])

  private case class PayPensionsProfitPart2(totalPayeEmploymentAndLumpSumIncome: Option[BigDecimal],
                                            totalEmploymentExpenses: Option[BigDecimal],
                                            totalSeafarersDeduction: Option[BigDecimal],
                                            totalForeignTaxOnForeignEmployment: Option[BigDecimal],
                                            totalEmploymentIncome: Option[BigInt],
                                            totalShareSchemesIncome: Option[BigDecimal],
                                            totalOverseasPensionsStateBenefitsRoyalties: Option[BigDecimal],
                                            totalAllOtherIncomeReceivedWhilstAbroad: Option[BigDecimal],
                                            totalOverseasIncomeAndGains: Option[BigDecimal],
                                            totalForeignBenefitsAndGifts: Option[BigDecimal],
                                            tipsIncome: Option[BigDecimal],
                                            businessProfitAndLoss: Option[BusinessProfitAndLoss])

  private val formatPt1: OFormat[PayPensionsProfitPart1] = Json.format[PayPensionsProfitPart1]
  private val formatPt2: OFormat[PayPensionsProfitPart2] = Json.format[PayPensionsProfitPart2]

  private def buildPayPensionsObjects(part1: PayPensionsProfitPart1, part2: PayPensionsProfitPart2): PayPensionsProfit = {
    import part1._
    import part2._

    PayPensionsProfit(
      incomeReceived = incomeReceived,
      taxableIncome = taxableIncome,
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
      businessProfitAndLoss = businessProfitAndLoss
    )
  }

  private def splitPayPensionsObject(o: PayPensionsProfit): (PayPensionsProfitPart1, PayPensionsProfitPart2) = {
    import o._

    (
      PayPensionsProfitPart1(
        incomeReceived = incomeReceived,
        taxableIncome = taxableIncome,
        totalSelfEmploymentProfit = totalSelfEmploymentProfit,
        totalPropertyProfit = totalPropertyProfit,
        totalFHLPropertyProfit = totalFHLPropertyProfit,
        totalUKOtherPropertyProfit = totalUKOtherPropertyProfit,
        totalForeignPropertyProfit = totalForeignPropertyProfit,
        totalEeaFhlProfit = totalEeaFhlProfit,
        totalOccupationalPensionIncome = totalOccupationalPensionIncome,
        totalStateBenefitsIncome = totalStateBenefitsIncome,
        totalBenefitsInKind = totalBenefitsInKind
      ),
      PayPensionsProfitPart2(
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
        businessProfitAndLoss = businessProfitAndLoss
      )
    )
  }

  implicit val writes: OWrites[PayPensionsProfit] = (o: PayPensionsProfit) => {
    val splitData = PayPensionsProfit.splitPayPensionsObject(o)
    Json.toJsObject(splitData._1)(formatPt1) ++ Json.toJsObject(splitData._2)(formatPt2)
  }

  implicit val reads: Reads[PayPensionsProfit] = (
    JsPath.read[PayPensionsProfitPart1](formatPt1) and
      JsPath.read[PayPensionsProfitPart2](formatPt2)
    )(PayPensionsProfit.buildPayPensionsObjects _)
}