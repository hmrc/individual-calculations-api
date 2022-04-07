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

package v2.fixtures.getIncomeTaxAndNics.summary

import play.api.libs.json.{JsValue, Json}
import v2.models.response.getIncomeTaxAndNics.summary.CapitalGainsTaxSummary

object CapitalGainsTaxSummaryFixture {

  val totalCapitalGainsIncome: BigDecimal          = 100.25
  val annualExemptionAmount: BigDecimal            = 200.25
  val totalTaxableGains: BigDecimal                = 300.25
  val capitalGainsTaxAmount: Option[BigDecimal]    = Some(400.25)
  val adjustments: Option[BigDecimal]              = Some(500.25)
  val adjustedCapitalGainsTax: Option[BigDecimal]  = Some(600.25)
  val foreignTaxCreditRelief: Option[BigDecimal]   = Some(700.25)
  val capitalGainsTaxAfterFTCR: Option[BigDecimal] = Some(800.25)
  val taxOnGainsAlreadyPaid: Option[BigDecimal]    = Some(900.25)
  val capitalGainsTaxDue: BigDecimal               = 1000.25
  val capitalGainsOverpaid: Option[BigDecimal]     = Some(1100.25)

  val capitalGainsTaxSummaryModel: CapitalGainsTaxSummary = CapitalGainsTaxSummary(
    totalCapitalGainsIncome = totalCapitalGainsIncome,
    annualExemptionAmount = annualExemptionAmount,
    totalTaxableGains = totalTaxableGains,
    capitalGainsTaxAmount = capitalGainsTaxAmount,
    adjustments = adjustments,
    adjustedCapitalGainsTax = adjustedCapitalGainsTax,
    foreignTaxCreditRelief = foreignTaxCreditRelief,
    capitalGainsTaxAfterFTCR = capitalGainsTaxAfterFTCR,
    taxOnGainsAlreadyPaid = taxOnGainsAlreadyPaid,
    capitalGainsTaxDue = capitalGainsTaxDue,
    capitalGainsOverpaid = capitalGainsOverpaid
  )

  val capitalGainsTaxSummaryJson: JsValue = Json.parse(s"""
      |{
      |  "totalCapitalGainsIncome": $totalCapitalGainsIncome,
      |  "annualExemptionAmount": $annualExemptionAmount,
      |  "totalTaxableGains": $totalTaxableGains,
      |  "capitalGainsTaxAmount": ${capitalGainsTaxAmount.get},
      |  "adjustments": ${adjustments.get},
      |  "adjustedCapitalGainsTax": ${adjustedCapitalGainsTax.get},
      |  "foreignTaxCreditRelief": ${foreignTaxCreditRelief.get},
      |  "capitalGainsTaxAfterFTCR": ${capitalGainsTaxAfterFTCR.get},
      |  "taxOnGainsAlreadyPaid": ${taxOnGainsAlreadyPaid.get},
      |  "capitalGainsTaxDue": $capitalGainsTaxDue,
      |  "capitalGainsOverpaid": ${capitalGainsOverpaid.get}
      |}
      |""".stripMargin)

}
