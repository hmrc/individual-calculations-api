/*
 * Copyright 2019 HM Revenue & Customs
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
package v1.models.response.getTaxableIncome

import play.api.libs.json.{Json, OFormat}
import v1.models.response.getTaxableIncome.detail.selfEmployment.{CarriedForwardLoss, ClaimNotApplied, LossBroughtForward, LossClaimsDetail, LossClaimsSummary, ResultOfClaimApplied, SelfEmploymentBusiness, UnclaimedLoss}
import v1.models.response.getTaxableIncome.detail.ukPropertyFhl.UkPropertyFhl
import v1.models.response.getTaxableIncome.detail.ukPropertyNonFhl.UkPropertyNonFhl
import v1.models.response.getTaxableIncome.detail.{BusinessProfitAndLoss, CalculationDetail, Dividends, PayPensionsProfit, Savings, SavingsAndGains}

case class TaxableIncomeDetail(
                              carriedForwardLoss: Option[Seq[CarriedForwardLoss]],
                              claimNotApplied: Option[Seq[ClaimNotApplied]],
                              lossBroughtForward: Option[Seq[LossBroughtForward]],
                              lossClaimsDetail: Option[Seq[LossClaimsDetail]],
                              lossClaimsSummary: Option[Seq[LossClaimsSummary]],
                              resultOfClaimApplied: Option[Seq[ResultOfClaimApplied]],
                              selfEmploymentBusiness: Option[Seq[SelfEmploymentBusiness]],
                              unclaimedLoss: Option[Seq[UnclaimedLoss]],
                              ukPropertyFhl: Option[Seq[UkPropertyFhl]],
                              ukPropertyNonFhl: Option[Seq[UkPropertyNonFhl]],
                              businessProfitAndLoss: Option[Seq[BusinessProfitAndLoss]],
                              calculationDetail: Option[Seq[CalculationDetail]],
                              dividends: Option[Seq[Dividends]],
                              payPensionsProfit: Option[Seq[PayPensionsProfit]],
                              savings: Option[Seq[Savings]],
                              savingsAndGains: Option[Seq[SavingsAndGains]]
                              )

object TaxableIncomeDetail {
  implicit val formats: OFormat[TaxableIncomeDetail] = Json.format[TaxableIncomeDetail]
}
