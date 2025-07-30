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

package v5.retrieveCalculation.def2.model.response.calculation.allowancesAndDeductions

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, Reads, Writes}

case class AllowancesAndDeductions(personalAllowance: Option[BigInt],
                                   marriageAllowanceTransferOut: Option[MarriageAllowanceTransferOut],
                                   reducedPersonalAllowance: Option[BigInt],
                                   giftOfInvestmentsAndPropertyToCharity: Option[BigInt],
                                   blindPersonsAllowance: Option[BigInt],
                                   lossesAppliedToGeneralIncome: Option[BigInt],
                                   cgtLossSetAgainstInYearGeneralIncome: Option[BigInt],
                                   qualifyingLoanInterestFromInvestments: Option[BigDecimal],
                                   postCessationTradeReceipts: Option[BigDecimal],
                                   paymentsToTradeUnionsForDeathBenefits: Option[BigDecimal],
                                   grossAnnuityPayments: Option[BigDecimal],
                                   annuityPayments: Option[AnnuityPayments],
                                   pensionContributions: Option[BigDecimal],
                                   pensionContributionsDetail: Option[PensionContributionsDetail])

object AllowancesAndDeductions {

  implicit val reads: Reads[AllowancesAndDeductions] = (
    (JsPath \ "personalAllowance").readNullable[BigInt] and
      (JsPath \ "marriageAllowanceTransferOut").readNullable[MarriageAllowanceTransferOut] and
      (JsPath \ "reducedPersonalAllowance").readNullable[BigInt] and
      (JsPath \ "giftOfInvestmentsAndPropertyToCharity").readNullable[BigInt] and
      (JsPath \ "blindPersonsAllowance").readNullable[BigInt] and
      (JsPath \ "lossesAppliedToGeneralIncome").readNullable[BigInt] and
      (JsPath \ "cgtLossSetAgainstInYearGeneralIncome").readNullable[BigInt] and
      (JsPath \ "qualifyingLoanInterestFromInvestments").readNullable[BigDecimal] and
      (JsPath \ "post-cessationTradeReceipts").readNullable[BigDecimal] and
      (JsPath \ "paymentsToTradeUnionsForDeathBenefits").readNullable[BigDecimal] and
      (JsPath \ "grossAnnuityPayments").readNullable[BigDecimal] and
      (JsPath \ "annuityPayments").readNullable[AnnuityPayments] and
      (JsPath \ "pensionContributions").readNullable[BigDecimal] and
      (JsPath \ "pensionContributionsDetail").readNullable[PensionContributionsDetail]
  )(AllowancesAndDeductions.apply)

  implicit val writes: Writes[AllowancesAndDeductions] = Json.writes[AllowancesAndDeductions]

}
