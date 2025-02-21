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

package v8.retrieveCalculation.def3.model.response.calculation.businessProfitAndLoss

import play.api.libs.json._
import v8.common.model.response.IncomeSourceType
import IncomeSourceType._

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
                                      carrySidewaysIncomeTaxLossesUsed: Option[BigInt],
                                      broughtForwardCarrySidewaysIncomeTaxLossesUsed: Option[BigInt],
                                      totalIncomeTaxLossesCarriedForward: Option[BigInt],
                                      class4Loss: Option[BigInt],
                                      totalBroughtForwardClass4Losses: Option[BigInt],
                                      broughtForwardClass4LossesUsed: Option[BigInt],
                                      carrySidewaysClass4LossesUsed: Option[BigInt],
                                      totalClass4LossesCarriedForward: Option[BigInt])

object BusinessProfitAndLoss {

  implicit val incomeSourceTypeFormat: Format[IncomeSourceType] = IncomeSourceType.formatRestricted(
    `self-employment`,
    `uk-property`,
    `foreign-property`
  )

  implicit val reads: Reads[BusinessProfitAndLoss] = for {
    incomeSourceId                                 <- (JsPath \ "incomeSourceId").read[String]
    incomeSourceType                               <- (JsPath \ "incomeSourceType").read[IncomeSourceType]
    incomeSourceName                               <- (JsPath \ "incomeSourceName").readNullable[String]
    totalIncome                                    <- (JsPath \ "totalIncome").readNullable[BigDecimal]
    totalExpenses                                  <- (JsPath \ "totalExpenses").readNullable[BigDecimal]
    netProfit                                      <- (JsPath \ "netProfit").readNullable[BigDecimal]
    netLoss                                        <- (JsPath \ "netLoss").readNullable[BigDecimal]
    totalAdditions                                 <- (JsPath \ "totalAdditions").readNullable[BigDecimal]
    totalDeductions                                <- (JsPath \ "totalDeductions").readNullable[BigDecimal]
    accountingAdjustments                          <- (JsPath \ "accountingAdjustments").readNullable[BigDecimal]
    taxableProfit                                  <- (JsPath \ "taxableProfit").readNullable[BigInt]
    adjustedIncomeTaxLoss                          <- (JsPath \ "adjustedIncomeTaxLoss").readNullable[BigInt]
    totalBroughtForwardIncomeTaxLosses             <- (JsPath \ "totalBroughtForwardIncomeTaxLosses").readNullable[BigInt]
    broughtForwardIncomeTaxLossesUsed              <- (JsPath \ "broughtForwardIncomeTaxLossesUsed").readNullable[BigInt]
    taxableProfitAfterIncomeTaxLossesDeduction     <- (JsPath \ "taxableProfitAfterIncomeTaxLossesDeduction").readNullable[BigInt]
    carrySidewaysIncomeTaxLossesUsed               <- (JsPath \ "carrySidewaysIncomeTaxLossesUsed").readNullable[BigInt]
    broughtForwardCarrySidewaysIncomeTaxLossesUsed <- (JsPath \ "broughtForwardCarrySidewaysIncomeTaxLossesUsed").readNullable[BigInt]
    totalIncomeTaxLossesCarriedForward             <- (JsPath \ "totalIncomeTaxLossesCarriedForward").readNullable[BigInt]
    class4Loss                                     <- (JsPath \ "class4Loss").readNullable[BigInt]
    totalBroughtForwardClass4Losses                <- (JsPath \ "totalBroughtForwardClass4Losses").readNullable[BigInt]
    broughtForwardClass4LossesUsed                 <- (JsPath \ "broughtForwardClass4LossesUsed").readNullable[BigInt]
    carrySidewaysClass4LossesUsed                  <- (JsPath \ "carrySidewaysClass4LossesUsed").readNullable[BigInt]
    totalClass4LossesCarriedForward                <- (JsPath \ "totalClass4LossesCarriedForward").readNullable[BigInt]

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
      carrySidewaysIncomeTaxLossesUsed = carrySidewaysIncomeTaxLossesUsed,
      broughtForwardCarrySidewaysIncomeTaxLossesUsed = broughtForwardCarrySidewaysIncomeTaxLossesUsed,
      totalIncomeTaxLossesCarriedForward = totalIncomeTaxLossesCarriedForward,
      class4Loss = class4Loss,
      totalBroughtForwardClass4Losses = totalBroughtForwardClass4Losses,
      broughtForwardClass4LossesUsed = broughtForwardClass4LossesUsed,
      carrySidewaysClass4LossesUsed = carrySidewaysClass4LossesUsed,
      totalClass4LossesCarriedForward = totalClass4LossesCarriedForward
    )
  }

  implicit val writes: OWrites[BusinessProfitAndLoss] = (o: BusinessProfitAndLoss) => {
    JsObject(
      Map(
        "incomeSourceId"                                 -> Json.toJson(o.incomeSourceId),
        "incomeSourceType"                               -> Json.toJson(o.incomeSourceType),
        "incomeSourceName"                               -> Json.toJson(o.incomeSourceName),
        "totalIncome"                                    -> Json.toJson(o.totalIncome),
        "totalExpenses"                                  -> Json.toJson(o.totalExpenses),
        "netProfit"                                      -> Json.toJson(o.netProfit),
        "netLoss"                                        -> Json.toJson(o.netLoss),
        "totalAdditions"                                 -> Json.toJson(o.totalAdditions),
        "totalDeductions"                                -> Json.toJson(o.totalDeductions),
        "accountingAdjustments"                          -> Json.toJson(o.accountingAdjustments),
        "taxableProfit"                                  -> Json.toJson(o.taxableProfit),
        "adjustedIncomeTaxLoss"                          -> Json.toJson(o.adjustedIncomeTaxLoss),
        "totalBroughtForwardIncomeTaxLosses"             -> Json.toJson(o.totalBroughtForwardIncomeTaxLosses),
        "broughtForwardIncomeTaxLossesUsed"              -> Json.toJson(o.broughtForwardIncomeTaxLossesUsed),
        "taxableProfitAfterIncomeTaxLossesDeduction"     -> Json.toJson(o.taxableProfitAfterIncomeTaxLossesDeduction),
        "carrySidewaysIncomeTaxLossesUsed"               -> Json.toJson(o.carrySidewaysIncomeTaxLossesUsed),
        "broughtForwardCarrySidewaysIncomeTaxLossesUsed" -> Json.toJson(o.broughtForwardCarrySidewaysIncomeTaxLossesUsed),
        "totalIncomeTaxLossesCarriedForward"             -> Json.toJson(o.totalIncomeTaxLossesCarriedForward),
        "class4Loss"                                     -> Json.toJson(o.class4Loss),
        "totalBroughtForwardClass4Losses"                -> Json.toJson(o.totalBroughtForwardClass4Losses),
        "broughtForwardClass4LossesUsed"                 -> Json.toJson(o.broughtForwardClass4LossesUsed),
        "carrySidewaysClass4LossesUsed"                  -> Json.toJson(o.carrySidewaysClass4LossesUsed),
        "totalClass4LossesCarriedForward"                -> Json.toJson(o.totalClass4LossesCarriedForward)
      ).filterNot { case (_, value) =>
        value == JsNull
      }
    )
  }

}
