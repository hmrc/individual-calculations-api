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

package v7.retrieveCalculation.def3.model.response.calculation.taxCalculation

import play.api.libs.functional.syntax.toFunctionalBuilderOps
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

  given Reads[IncomeTax] = (
    (JsPath \ "totalIncomeReceivedFromAllSources").read[BigInt] and
      (JsPath \ "totalAllowancesAndDeductions").read[BigInt] and
      (JsPath \ "totalTaxableIncome").read[BigInt] and
      (JsPath \ "payPensionsProfit").readNullable[IncomeTaxItem] and
      (JsPath \ "savingsAndGains").readNullable[IncomeTaxItem] and
      (JsPath \ "dividends").readNullable[IncomeTaxItem] and
      (JsPath \ "lumpSums").readNullable[IncomeTaxItem] and
      (JsPath \ "gainsOnLifePolicies").readNullable[IncomeTaxItem] and
      (JsPath \ "incomeTaxCharged").read[BigDecimal] and
      (JsPath \ "totalReliefs").readNullable[BigDecimal] and
      (JsPath \ "incomeTaxDueAfterReliefs").readNullable[BigDecimal] and
      (JsPath \ "totalNotionalTax").readNullable[BigDecimal] and
      (JsPath \ "marriageAllowanceRelief").readNullable[BigDecimal] and
      (JsPath \ "incomeTaxDueAfterTaxReductions").readNullable[BigDecimal] and
      (JsPath \ "incomeTaxDueAfterGiftAid").readNullable[BigDecimal] and
      (JsPath \ "totalPensionSavingsTaxCharges").readNullable[BigDecimal] and
      (JsPath \ "statePensionLumpSumCharges").readNullable[BigDecimal] and
      (JsPath \ "payeUnderpaymentsCodedOut").readNullable[BigDecimal] and
      (JsPath \ "totalIncomeTaxDue").readNullable[BigDecimal] and
      (JsPath \ "giftAidTaxChargeWhereBasicRateDiffers").readNullable[BigDecimal] and
      (JsPath \ "highIncomeChildBenefitCharge").readNullable[BigDecimal]
  )(IncomeTax.apply)

  given OWrites[IncomeTax] = Json.writes[IncomeTax]
}
