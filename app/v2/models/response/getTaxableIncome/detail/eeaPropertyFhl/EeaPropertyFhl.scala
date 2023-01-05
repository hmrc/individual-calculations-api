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

package v2.models.response.getTaxableIncome.detail.eeaPropertyFhl

import play.api.libs.json._
import v2.models.response.getTaxableIncome.detail.eeaPropertyFhl.detail.BusinessSourceAdjustableSummary
import v2.models.response.getTaxableIncome.detail.eeaPropertyFhl.detail.LossClaimsDetail
import v2.models.response.getTaxableIncome.detail.eeaPropertyFhl.summary.LossClaimsSummary

case class EeaPropertyFhl(totalIncome: Option[BigDecimal],
                          totalExpenses: Option[BigDecimal],
                          netProfit: Option[BigDecimal],
                          netLoss: Option[BigDecimal],
                          totalAdditions: Option[BigDecimal],
                          totalDeductions: Option[BigDecimal],
                          adjustedIncomeTaxLoss: Option[BigInt],
                          taxableProfit: Option[BigInt],
                          taxableProfitAfterIncomeTaxLossesDeduction: Option[BigInt],
                          lossClaimsSummary: Option[LossClaimsSummary],
                          lossClaimsDetail: Option[LossClaimsDetail],
                          bsas: Option[BusinessSourceAdjustableSummary])

object EeaPropertyFhl {
  implicit val formats: OFormat[EeaPropertyFhl] = Json.format[EeaPropertyFhl]
}
