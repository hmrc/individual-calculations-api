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

package v3.models.response.retrieveCalculation.calculation.AllowancesAndDeductions

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
                                   pensionContributionsDetail: Option[PensionContributionsDetail]
                                  )


object AllowancesAndDeductions {

  implicit val reads: Reads[AllowancesAndDeductions] = Json.reads[AllowancesAndDeductions]

  implicit val writes: Writes[AllowancesAndDeductions] = (
    (JsPath \ "personalAllowance").writeNullable[BigInt] and
    (JsPath \ "marriageAllowanceTransferOut").writeNullable[MarriageAllowanceTransferOut] and
    (JsPath \ "reducedPersonalAllowance").writeNullable[BigInt] and
    (JsPath \ "giftOfInvestmentsAndPropertyToCharity").writeNullable[BigInt] and
    (JsPath \ "blindPersonsAllowance").writeNullable[BigInt] and
    (JsPath \ "lossesAppliedToGeneralIncome").writeNullable[BigInt] and
    (JsPath \ "cgtLossSetAgainstInYearGeneralIncome").writeNullable[BigInt] and
    (JsPath \ "qualifyingLoanInterestFromInvestments").writeNullable[BigDecimal] and
    (JsPath \ "post-cessationTradeReceipts").writeNullable[BigDecimal] and
    (JsPath \ "paymentsToTradeUnionsForDeathBenefits").writeNullable[BigDecimal] and
    (JsPath \ "grossAnnuityPayments").writeNullable[BigDecimal] and
    (JsPath \ "annuityPayments").writeNullable[AnnuityPayments] and
    (JsPath \ "pensionContributionsDetail").writeNullable[PensionContributionsDetail]
    )(unlift(AllowancesAndDeductions.unapply))
}
