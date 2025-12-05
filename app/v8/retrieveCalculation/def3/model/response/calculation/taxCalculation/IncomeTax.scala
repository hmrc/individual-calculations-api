/*
 * Copyright 2025 HM Revenue & Customs
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

package v8.retrieveCalculation.def3.model.response.calculation.taxCalculation

import play.api.libs.json.*

case class IncomeTax(
    totalIncomeReceivedFromAllSources: BigInt,
    totalAllowancesAndDeductions: BigInt,
    totalTaxableIncome: BigInt,
    payPensionsProfit: Option[IncomeTaxItem],
    savingsAndGains: Option[IncomeTaxItem],
    dividends: Option[IncomeTaxItem],
    lumpSums: Option[IncomeTaxItem],
    gainsOnLifePolicies: Option[IncomeTaxItem],
    incomeTaxCharged: BigDecimal,
    incomeTaxChargedOnTransitionProfits: Option[BigDecimal],
    incomeTaxChargedAfterTransitionProfits: Option[BigDecimal],
    totalReliefs: Option[BigDecimal],
    incomeTaxDueAfterReliefs: Option[BigDecimal],
    totalNotionalTax: Option[BigDecimal],
    marriageAllowanceRelief: Option[BigDecimal],
    incomeTaxDueAfterTaxReductions: Option[BigDecimal],
    incomeTaxDueAfterGiftAid: Option[BigDecimal],
    totalPensionSavingsTaxCharges: Option[BigDecimal],
    statePensionLumpSumCharges: Option[BigDecimal],
    payeUnderpaymentsCodedOut: Option[BigDecimal],
    totalIncomeTaxDue: Option[BigDecimal],
    giftAidTaxChargeWhereBasicRateDiffers: Option[BigDecimal],
    highIncomeBenefitCharge: Option[BigDecimal]
)

object IncomeTax {

  given Reads[IncomeTax] = for {
    totalIncomeReceivedFromAllSources      <- (JsPath \ "totalIncomeReceivedFromAllSources").read[BigInt]
    totalAllowancesAndDeductions           <- (JsPath \ "totalAllowancesAndDeductions").read[BigInt]
    totalTaxableIncome                     <- (JsPath \ "totalTaxableIncome").read[BigInt]
    payPensionsProfit                      <- (JsPath \ "payPensionsProfit").readNullable[IncomeTaxItem]
    savingsAndGains                        <- (JsPath \ "savingsAndGains").readNullable[IncomeTaxItem]
    dividends                              <- (JsPath \ "dividends").readNullable[IncomeTaxItem]
    lumpSums                               <- (JsPath \ "lumpSums").readNullable[IncomeTaxItem]
    gainsOnLifePolicies                    <- (JsPath \ "gainsOnLifePolicies").readNullable[IncomeTaxItem]
    incomeTaxCharged                       <- (JsPath \ "incomeTaxCharged").read[BigDecimal]
    incomeTaxChargedOnTransitionProfits    <- (JsPath \ "incomeTaxChargedOnTransitionProfits").readNullable[BigDecimal]
    incomeTaxChargedAfterTransitionProfits <- (JsPath \ "incomeTaxChargedAfterTransitionProfits").readNullable[BigDecimal]
    totalReliefs                           <- (JsPath \ "totalReliefs").readNullable[BigDecimal]
    incomeTaxDueAfterReliefs               <- (JsPath \ "incomeTaxDueAfterReliefs").readNullable[BigDecimal]
    totalNotionalTax                       <- (JsPath \ "totalNotionalTax").readNullable[BigDecimal]
    marriageAllowanceRelief                <- (JsPath \ "marriageAllowanceRelief").readNullable[BigDecimal]
    incomeTaxDueAfterTaxReductions         <- (JsPath \ "incomeTaxDueAfterTaxReductions").readNullable[BigDecimal]
    incomeTaxDueAfterGiftAid               <- (JsPath \ "incomeTaxDueAfterGiftAid").readNullable[BigDecimal]
    totalPensionSavingsTaxCharges          <- (JsPath \ "totalPensionSavingsTaxCharges").readNullable[BigDecimal]
    statePensionLumpSumCharges             <- (JsPath \ "statePensionLumpSumCharges").readNullable[BigDecimal]
    payeUnderpaymentsCodedOut              <- (JsPath \ "payeUnderpaymentsCodedOut").readNullable[BigDecimal]
    totalIncomeTaxDue                      <- (JsPath \ "totalIncomeTaxDue").readNullable[BigDecimal]
    giftAidTaxChargeWhereBasicRateDiffers  <- (JsPath \ "giftAidTaxChargeWhereBasicRateDiffers").readNullable[BigDecimal]
    highIncomeBenefitCharge                <- (JsPath \ "highIncomeChildBenefitCharge").readNullable[BigDecimal]
  } yield IncomeTax(
    totalIncomeReceivedFromAllSources,
    totalAllowancesAndDeductions,
    totalTaxableIncome,
    payPensionsProfit,
    savingsAndGains,
    dividends,
    lumpSums,
    gainsOnLifePolicies,
    incomeTaxCharged,
    incomeTaxChargedOnTransitionProfits,
    incomeTaxChargedAfterTransitionProfits,
    totalReliefs,
    incomeTaxDueAfterReliefs,
    totalNotionalTax,
    marriageAllowanceRelief,
    incomeTaxDueAfterTaxReductions,
    incomeTaxDueAfterGiftAid,
    totalPensionSavingsTaxCharges,
    statePensionLumpSumCharges,
    payeUnderpaymentsCodedOut,
    totalIncomeTaxDue,
    giftAidTaxChargeWhereBasicRateDiffers,
    highIncomeBenefitCharge
  )

  given OWrites[IncomeTax] = Json.writes[IncomeTax]
}
