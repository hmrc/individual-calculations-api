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

package v8.retrieveCalculation.def4.model.response.calculation.reliefs

import common.utils.enums.Enums
import play.api.libs.json.{Reads, Writes}

sealed trait ReliefsClaimedType

object ReliefsClaimedType {
  case object `vct-subscriptions`                                           extends ReliefsClaimedType
  case object `eis-subscriptions`                                           extends ReliefsClaimedType
  case object `community-investment`                                        extends ReliefsClaimedType
  case object `seed-enterprise-investment`                                  extends ReliefsClaimedType
  case object `social-enterprise-investment`                                extends ReliefsClaimedType
  case object `maintenance-payments`                                        extends ReliefsClaimedType
  case object `deficiency-relief`                                           extends ReliefsClaimedType
  case object `non-deductible-loan-interest`                                extends ReliefsClaimedType
  case object `qualifying-distribution-redemption-of-shares-and-securities` extends ReliefsClaimedType

  implicit val writes: Writes[ReliefsClaimedType] = Enums.writes[ReliefsClaimedType]

  implicit val reads: Reads[ReliefsClaimedType] = Enums.readsUsing {
    case "vctSubscriptions"                                      => `vct-subscriptions`
    case "eisSubscriptions"                                      => `eis-subscriptions`
    case "communityInvestment"                                   => `community-investment`
    case "seedEnterpriseInvestment"                              => `seed-enterprise-investment`
    case "socialEnterpriseInvestment"                            => `social-enterprise-investment`
    case "maintenancePayments"                                   => `maintenance-payments`
    case "deficiencyRelief"                                      => `deficiency-relief`
    case "nonDeductableLoanInterest"                             => `non-deductible-loan-interest`
    case "qualifyingDistributionRedemptionOfSharesAndSecurities" => `qualifying-distribution-redemption-of-shares-and-securities`
  }

}
