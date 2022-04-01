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

package v3.fixtures.getAllowancesDeductionsAndReliefs.detail

import play.api.libs.json.{ JsValue, Json }
import v3.fixtures.getAllowancesDeductionsAndReliefs.detail.AnnualPaymentsFixture._
import v3.fixtures.getAllowancesDeductionsAndReliefs.detail.MarriageAllowanceTransferOutFixture._
import v3.fixtures.getAllowancesDeductionsAndReliefs.detail.PensionContributionsFixture._
import v3.models.response.getAllowancesDeductionsAndReliefs.detail.AllowancesAndDeductions

object AllowancesAndDeductionsFixture {

  val personalAllowance: Option[BigInt]                         = Some(12500)
  val reducedPersonalAllowance: Option[BigInt]                  = Some(12501)
  val giftOfInvestmentsAndPropertyToCharity: Option[BigInt]     = Some(12502)
  val blindPersonsAllowance: Option[BigInt]                     = Some(12503)
  val lossesAppliedToGeneralIncome: Option[BigInt]              = Some(12504)
  val cgtLossSetAgainstInYearGeneralIncome: Option[BigInt]      = Some(12505)
  val qualifyingLoanInterestFromInvestments: Option[BigDecimal] = Some(12503.99)
  val postCessationTradeReceipts: Option[BigDecimal]            = Some(12503.99)
  val paymentsToTradeUnionsForDeathBenefits: Option[BigDecimal] = Some(12503.99)

  val allowancesAndDeductionsModel: AllowancesAndDeductions =
    AllowancesAndDeductions(
      personalAllowance = personalAllowance,
      reducedPersonalAllowance = reducedPersonalAllowance,
      giftOfInvestmentsAndPropertyToCharity = giftOfInvestmentsAndPropertyToCharity,
      blindPersonsAllowance = blindPersonsAllowance,
      lossesAppliedToGeneralIncome = lossesAppliedToGeneralIncome,
      cgtLossSetAgainstInYearGeneralIncome = cgtLossSetAgainstInYearGeneralIncome,
      qualifyingLoanInterestFromInvestments = qualifyingLoanInterestFromInvestments,
      postCessationTradeReceipts = postCessationTradeReceipts,
      paymentsToTradeUnionsForDeathBenefits = paymentsToTradeUnionsForDeathBenefits,
      annualPayments = Some(annualPaymentsModel),
      pensionContributions = Some(pensionContributionsModel),
      marriageAllowanceTransferOut = Some(marriageAllowanceTransferOutModel)
    )

  val allowancesAndDeductionsJson: JsValue = Json.parse(
    s"""
      |{
      |  "personalAllowance": ${personalAllowance.get},
      |  "reducedPersonalAllowance": ${reducedPersonalAllowance.get},
      |  "giftOfInvestmentsAndPropertyToCharity": ${giftOfInvestmentsAndPropertyToCharity.get},
      |  "blindPersonsAllowance": ${blindPersonsAllowance.get},
      |  "lossesAppliedToGeneralIncome": ${lossesAppliedToGeneralIncome.get},
      |  "cgtLossSetAgainstInYearGeneralIncome": ${cgtLossSetAgainstInYearGeneralIncome.get},
      |  "qualifyingLoanInterestFromInvestments": ${qualifyingLoanInterestFromInvestments.get},
      |  "postCessationTradeReceipts": ${postCessationTradeReceipts.get},
      |  "paymentsToTradeUnionsForDeathBenefits": ${paymentsToTradeUnionsForDeathBenefits.get},
      |  "annualPayments": $annualPaymentsJson,
      |  "pensionContributions": $pensionContributionsJson,
      |  "marriageAllowanceTransferOut": $marriageAllowanceTransferOutJson
      |}
    """.stripMargin
  )
}
