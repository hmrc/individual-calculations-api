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

package v2.fixtures.getIncomeTaxAndNics.summary

import play.api.libs.json.{JsValue, Json}
import v2.models.response.getIncomeTaxAndNics.summary.IncomeTaxSummary

object IncomeTaxSummaryFixture {

  val incomeTaxCharged: BigDecimal = 2000.00
  val incomeTaxDueAfterReliefs: Option[BigDecimal] = Some(1525.22)
  val incomeTaxDueAfterGiftAid: Option[BigDecimal] = Some(120.10)
  val totalNotionalTax: Option[BigDecimal] = Some(130.10)
  val totalPensionSavingsTaxCharges: Option[BigDecimal] = Some(140.10)
  val statePensionLumpSumCharges: Option[BigDecimal] = Some(150.10)
  val incomeTaxDueAfterTaxReductions: Option[BigDecimal] = Some(160.10)
  val totalIncomeTaxDue: Option[BigDecimal] = Some(170.10)
  val payeUnderpaymentsCodedOut: Option[BigDecimal] = Some(180)
  val totalTaxDeductedBeforeCodingOut: Option[BigDecimal] = Some(190)
  val saUnderpaymentsCodedOut: Option[BigDecimal] = Some(200)

  val incomeTaxSummaryModel: IncomeTaxSummary =
    IncomeTaxSummary(
      incomeTaxCharged = incomeTaxCharged,
      incomeTaxDueAfterReliefs = incomeTaxDueAfterReliefs,
      incomeTaxDueAfterGiftAid = incomeTaxDueAfterGiftAid,
      totalNotionalTax = totalNotionalTax,
      totalPensionSavingsTaxCharges = totalPensionSavingsTaxCharges,
      statePensionLumpSumCharges = statePensionLumpSumCharges,
      incomeTaxDueAfterTaxReductions = incomeTaxDueAfterTaxReductions,
      totalIncomeTaxDue = totalIncomeTaxDue,
      payeUnderpaymentsCodedOut = payeUnderpaymentsCodedOut,
      totalTaxDeductedBeforeCodingOut = totalTaxDeductedBeforeCodingOut,
      saUnderpaymentsCodedOut = saUnderpaymentsCodedOut
    )

  val incomeTaxSummaryJson: JsValue = Json.parse(
    s"""
       |{
       |   "incomeTaxCharged": $incomeTaxCharged,
       |   "incomeTaxDueAfterReliefs": ${incomeTaxDueAfterReliefs.get},
       |   "incomeTaxDueAfterGiftAid": ${incomeTaxDueAfterGiftAid.get},
       |   "totalNotionalTax": ${totalNotionalTax.get},
       |   "totalPensionSavingsTaxCharges": ${totalPensionSavingsTaxCharges.get},
       |   "statePensionLumpSumCharges": ${statePensionLumpSumCharges.get},
       |   "incomeTaxDueAfterTaxReductions": ${incomeTaxDueAfterTaxReductions.get},
       |   "totalIncomeTaxDue": ${totalIncomeTaxDue.get},
       |   "payeUnderpaymentsCodedOut": ${payeUnderpaymentsCodedOut.get},
       |    "totalTaxDeductedBeforeCodingOut": ${totalTaxDeductedBeforeCodingOut.get},
       |    "saUnderpaymentsCodedOut": ${saUnderpaymentsCodedOut.get}
       |}
    """.stripMargin
  )
}