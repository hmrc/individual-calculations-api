/*
 * Copyright 2023 HM Revenue & Customs
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

package v8.retrieveCalculation.def4.model.response.calculation.businessProfitAndLoss

import play.api.libs.json.*
import v8.common.model.response.IncomeSourceType
import v8.common.model.response.IncomeSourceType.*

case class BusinessProfitAndLoss(incomeSourceId: String,
                                 incomeSourceType: IncomeSourceType,
                                 incomeSourceName: Option[String],
                                 totalIncome: Option[BigDecimal],
                                 totalExpenses: Option[BigDecimal],
                                 netProfit: Option[BigDecimal],
                                 netLoss: Option[BigDecimal],
                                 totalAdditions: Option[BigDecimal],
                                 totalDeductions: Option[BigDecimal],
                                 accountingAdjustments: Option[BigDecimal],
                                 taxableProfit: Option[BigInt],
                                 adjustedIncomeTaxLoss: Option[BigInt],
                                 totalBroughtForwardIncomeTaxLosses: Option[BigInt],
                                 broughtForwardIncomeTaxLossesUsed: Option[BigInt],
                                 taxableProfitAfterIncomeTaxLossesDeduction: Option[BigInt],
                                 adjustedProfit: Option[BigDecimal],
                                 adjustedLoss: Option[BigDecimal],
                                 foreignTaxPaid: Option[BigDecimal],
                                 outstandingBusinessIncome: Option[BigDecimal])

object BusinessProfitAndLoss {

  implicit val incomeSourceTypeFormat: Format[IncomeSourceType] = IncomeSourceType.formatRestricted(
    `self-employment`,
    `uk-property`,
    `foreign-property`
  )

  implicit val reads: Reads[BusinessProfitAndLoss] = for {
    incomeSourceId                             <- (JsPath \ "incomeSourceId").read[String]
    incomeSourceType                           <- (JsPath \ "incomeSourceType").read[IncomeSourceType]
    incomeSourceName                           <- (JsPath \ "incomeSourceName").readNullable[String]
    totalIncome                                <- (JsPath \ "totalIncome").readNullable[BigDecimal]
    totalExpenses                              <- (JsPath \ "totalExpenses").readNullable[BigDecimal]
    netProfit                                  <- (JsPath \ "netProfit").readNullable[BigDecimal]
    netLoss                                    <- (JsPath \ "netLoss").readNullable[BigDecimal]
    totalAdditions                             <- (JsPath \ "totalAdditions").readNullable[BigDecimal]
    totalDeductions                            <- (JsPath \ "totalDeductions").readNullable[BigDecimal]
    accountingAdjustments                      <- (JsPath \ "accountingAdjustments").readNullable[BigDecimal]
    taxableProfit                              <- (JsPath \ "taxableProfit").readNullable[BigInt]
    adjustedIncomeTaxLoss                      <- (JsPath \ "adjustedIncomeTaxLoss").readNullable[BigInt]
    totalBroughtForwardIncomeTaxLosses         <- (JsPath \ "totalBroughtForwardIncomeTaxLosses").readNullable[BigInt]
    broughtForwardIncomeTaxLossesUsed          <- (JsPath \ "broughtForwardIncomeTaxLossesUsed").readNullable[BigInt]
    taxableProfitAfterIncomeTaxLossesDeduction <- (JsPath \ "taxableProfitAfterIncomeTaxLossesDeduction").readNullable[BigInt]
    adjustedProfit                             <- (JsPath \ "adjustedProfit").readNullable[BigDecimal]
    adjustedLoss                               <- (JsPath \ "adjustedLoss").readNullable[BigDecimal]
    foreignTaxPaid                             <- (JsPath \ "foreignTaxPaid").readNullable[BigDecimal]
    outstandingBusinessIncome                  <- (JsPath \ "outstandingBusinessIncome").readNullable[BigDecimal]

  } yield {
    BusinessProfitAndLoss(
      incomeSourceId = incomeSourceId,
      incomeSourceType = incomeSourceType,
      incomeSourceName = incomeSourceName,
      totalIncome = totalIncome,
      totalExpenses = totalExpenses,
      netProfit = netProfit,
      netLoss = netLoss,
      totalAdditions = totalAdditions,
      totalDeductions = totalDeductions,
      accountingAdjustments = accountingAdjustments,
      taxableProfit = taxableProfit,
      adjustedIncomeTaxLoss = adjustedIncomeTaxLoss,
      totalBroughtForwardIncomeTaxLosses = totalBroughtForwardIncomeTaxLosses,
      broughtForwardIncomeTaxLossesUsed = broughtForwardIncomeTaxLossesUsed,
      taxableProfitAfterIncomeTaxLossesDeduction = taxableProfitAfterIncomeTaxLossesDeduction,
      adjustedProfit = adjustedProfit,
      adjustedLoss = adjustedLoss,
      foreignTaxPaid = foreignTaxPaid,
      outstandingBusinessIncome = outstandingBusinessIncome
    )
  }

  implicit val writes: OWrites[BusinessProfitAndLoss] = (o: BusinessProfitAndLoss) => {
    JsObject(
      Map(
        "incomeSourceId"                             -> Json.toJson(o.incomeSourceId),
        "incomeSourceType"                           -> Json.toJson(o.incomeSourceType),
        "incomeSourceName"                           -> Json.toJson(o.incomeSourceName),
        "totalIncome"                                -> Json.toJson(o.totalIncome),
        "totalExpenses"                              -> Json.toJson(o.totalExpenses),
        "netProfit"                                  -> Json.toJson(o.netProfit),
        "netLoss"                                    -> Json.toJson(o.netLoss),
        "totalAdditions"                             -> Json.toJson(o.totalAdditions),
        "totalDeductions"                            -> Json.toJson(o.totalDeductions),
        "accountingAdjustments"                      -> Json.toJson(o.accountingAdjustments),
        "taxableProfit"                              -> Json.toJson(o.taxableProfit),
        "adjustedIncomeTaxLoss"                      -> Json.toJson(o.adjustedIncomeTaxLoss),
        "totalBroughtForwardIncomeTaxLosses"         -> Json.toJson(o.totalBroughtForwardIncomeTaxLosses),
        "broughtForwardIncomeTaxLossesUsed"          -> Json.toJson(o.broughtForwardIncomeTaxLossesUsed),
        "taxableProfitAfterIncomeTaxLossesDeduction" -> Json.toJson(o.taxableProfitAfterIncomeTaxLossesDeduction),
        "adjustedProfit"                             -> Json.toJson(o.adjustedProfit),
        "adjustedLoss"                               -> Json.toJson(o.adjustedLoss),
        "foreignTaxPaid"                             -> Json.toJson(o.foreignTaxPaid),
        "outstandingBusinessIncome"                  -> Json.toJson(o.outstandingBusinessIncome)
      ).filterNot { case (_, value) =>
        value == JsNull
      }
    )
  }

}
