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

package v5.models.response.retrieveCalculation.calculation.endOfYearEstimate

import play.api.libs.json.{Json, OFormat}

case class EndOfYearEstimate(
    incomeSource: Option[Seq[IncomeSource]],
    totalEstimatedIncome: Option[BigInt],
    totalAllowancesAndDeductions: Option[BigInt],
    totalTaxableIncome: Option[BigInt],
    incomeTaxAmount: Option[BigDecimal],
    nic2: Option[BigDecimal],
    nic4: Option[BigDecimal],
    totalTaxDeductedBeforeCodingOut: Option[BigDecimal],
    saUnderpaymentsCodedOut: Option[BigDecimal],
    totalNicAmount: Option[BigDecimal],
    totalStudentLoansRepaymentAmount: Option[BigDecimal],
    totalAnnuityPaymentsTaxCharged: Option[BigDecimal],
    totalRoyaltyPaymentsTaxCharged: Option[BigDecimal],
    totalTaxDeducted: Option[BigDecimal],
    incomeTaxNicAmount: Option[BigDecimal],
    cgtAmount: Option[BigDecimal],
    incomeTaxNicAndCgtAmount: Option[BigDecimal]
) {

  def withoutTotalAllowanceAndDeductions: EndOfYearEstimate =
    copy(totalAllowancesAndDeductions = None)

  val isDefined: Boolean =
    !(totalEstimatedIncome.isEmpty &&
      totalAllowancesAndDeductions.isEmpty &&
      totalTaxableIncome.isEmpty &&
      incomeTaxAmount.isEmpty &&
      nic2.isEmpty &&
      nic4.isEmpty &&
      totalTaxDeductedBeforeCodingOut.isEmpty &&
      saUnderpaymentsCodedOut.isEmpty &&
      totalNicAmount.isEmpty &&
      totalStudentLoansRepaymentAmount.isEmpty &&
      totalAnnuityPaymentsTaxCharged.isEmpty &&
      totalRoyaltyPaymentsTaxCharged.isEmpty &&
      totalTaxDeducted.isEmpty &&
      incomeTaxNicAmount.isEmpty &&
      cgtAmount.isEmpty &&
      incomeTaxNicAndCgtAmount.isEmpty)

}

object EndOfYearEstimate {
  implicit val format: OFormat[EndOfYearEstimate] = Json.format
}
