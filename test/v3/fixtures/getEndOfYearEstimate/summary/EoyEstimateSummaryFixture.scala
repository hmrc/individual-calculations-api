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

package v3.fixtures.getEndOfYearEstimate.summary

import play.api.libs.json.{JsValue, Json}
import v3.models.response.getEoyEstimate.summary.EoyEstimateSummary

object EoyEstimateSummaryFixture {

  val totalEstimatedIncome: Option[BigInt] = Some(1000)
  val totalTaxableIncome: Option[BigInt] = Some(2000)
  val incomeTaxAmount: Option[BigDecimal] = Some(3000.98)
  val nic2: Option[BigDecimal] = Some(4000.98)
  val nic4: Option[BigDecimal] = Some(5000.98)
  val totalNicAmount: Option[BigDecimal] = Some(6000.98)
  val totalStudentLoansRepaymentAmount: Option[BigDecimal] = Some(3000.98)
  val totalAnnualPaymentsTaxCharged: Option[BigDecimal] = Some(5000.98)
  val totalRoyaltyPaymentsTaxCharged: Option[BigDecimal] = Some(7000.98)
  val totalTaxDeducted: Option[BigDecimal] = Some(9000.98)
  val incomeTaxNicAmount: Option[BigDecimal] = Some(7000.98)
  val totalTaxDeductedBeforeCodingOut: Option[BigDecimal] = Some(8000.98)
  val saUnderpaymentsCodedOut: Option[BigDecimal] = Some(9000.98)
  val cgtAmount: Option[BigDecimal] = Some(9100.98)
  val incomeTaxNicAndCgtAmount: Option[BigDecimal] = Some(9200.98)

  val eoyEstimateSummaryModel: EoyEstimateSummary =
    EoyEstimateSummary(
      totalEstimatedIncome = totalEstimatedIncome,
      totalTaxableIncome = totalTaxableIncome,
      incomeTaxAmount = incomeTaxAmount,
      nic2 = nic2,
      nic4 = nic4,
      totalNicAmount = totalNicAmount,
      totalStudentLoansRepaymentAmount = totalStudentLoansRepaymentAmount,
      totalAnnualPaymentsTaxCharged = totalAnnualPaymentsTaxCharged,
      totalRoyaltyPaymentsTaxCharged = totalRoyaltyPaymentsTaxCharged,
      totalTaxDeducted = totalTaxDeducted,
      incomeTaxNicAmount = incomeTaxNicAmount,
      totalTaxDeductedBeforeCodingOut = totalTaxDeductedBeforeCodingOut,
      saUnderpaymentsCodedOut = saUnderpaymentsCodedOut,
      cgtAmount = cgtAmount,
      incomeTaxNicAndCgtAmount = incomeTaxNicAndCgtAmount
    )

  val eoyEstimateSummaryJson: JsValue = Json.parse(
    s"""
       |{
       |   "totalEstimatedIncome": ${totalEstimatedIncome.get},
       |   "totalTaxableIncome": ${totalTaxableIncome.get},
       |   "incomeTaxAmount": ${incomeTaxAmount.get},
       |   "nic2": ${nic2.get},
       |   "nic4": ${nic4.get},
       |   "totalNicAmount": ${totalNicAmount.get},
       |   "totalStudentLoansRepaymentAmount": ${totalStudentLoansRepaymentAmount.get},
       |   "totalAnnualPaymentsTaxCharged": ${totalAnnualPaymentsTaxCharged.get},
       |   "totalRoyaltyPaymentsTaxCharged": ${totalRoyaltyPaymentsTaxCharged.get},
       |   "totalTaxDeducted": ${totalTaxDeducted.get},
       |   "incomeTaxNicAmount": ${incomeTaxNicAmount.get},
       |   "totalTaxDeductedBeforeCodingOut": ${totalTaxDeductedBeforeCodingOut.get},
       |   "saUnderpaymentsCodedOut": ${saUnderpaymentsCodedOut.get},
       |   "cgtAmount": ${cgtAmount.get},
       |   "incomeTaxNicAndCgtAmount": ${incomeTaxNicAndCgtAmount.get}
       |}
    """.stripMargin
  )
}