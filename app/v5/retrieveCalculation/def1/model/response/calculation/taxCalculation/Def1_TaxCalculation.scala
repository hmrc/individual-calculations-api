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

package v5.retrieveCalculation.def1.model.response.calculation.taxCalculation

import play.api.libs.json.{Format, Json}

case class Def1_TaxCalculation(
    incomeTax: Option[Def1_IncomeTax],
    nics: Option[Def1_Nics],
    totalTaxDeductedBeforeCodingOut: Option[BigDecimal],
    saUnderpaymentsCodedOut: Option[BigDecimal],
    totalIncomeTaxNicsCharged: Option[BigDecimal],
    totalStudentLoansRepaymentAmount: Option[BigDecimal],
    totalAnnuityPaymentsTaxCharged: Option[BigDecimal],
    totalRoyaltyPaymentsTaxCharged: Option[BigDecimal],
    totalTaxDeducted: Option[BigDecimal],
    totalIncomeTaxAndNicsDue: Option[BigDecimal],
    capitalGainsTax: Option[Def1_CapitalGainsTax],
    totalIncomeTaxAndNicsAndCgt: Option[BigDecimal]
) {

  def withoutUnderLowerProfitThreshold: Def1_TaxCalculation =
    copy(nics = nics.map(_.withoutUnderLowerProfitThreshold).filter(_.isDefined))

  def withoutGiftAidTaxChargeWhereBasicRateDiffers: Def1_TaxCalculation =
    copy(incomeTax = incomeTax.map(_.withoutGiftAidTaxChargeWhereBasicRateDiffers))

}

object Def1_TaxCalculation {
  implicit val format: Format[Def1_TaxCalculation] = Json.format
}
