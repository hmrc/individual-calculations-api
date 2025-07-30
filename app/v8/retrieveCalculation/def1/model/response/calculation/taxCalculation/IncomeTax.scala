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

package v8.retrieveCalculation.def1.model.response.calculation.taxCalculation

import play.api.libs.json.{Format, Json}

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
    giftAidTaxChargeWhereBasicRateDiffers: Option[BigDecimal]
) {
  def withoutGiftAidTaxChargeWhereBasicRateDiffers: IncomeTax = copy(giftAidTaxChargeWhereBasicRateDiffers = None)
}

object IncomeTax {
  implicit val format: Format[IncomeTax] = Json.format
}
