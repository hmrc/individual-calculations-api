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

package v8.retrieveCalculation.def3.model.response.calculation.reliefs

import common.utils.enums.EnumJsonSpecSupport
import shared.utils.UnitSpec
import v7.retrieveCalculation.def3.model.response.calculation.reliefs.ReliefsClaimedType._


class ReliefsClaimedTypeSpec extends UnitSpec with EnumJsonSpecSupport {

  testReads[ReliefsClaimedType](
    "vctSubscriptions"                                      -> `vct-subscriptions`,
    "eisSubscriptions"                                      -> `eis-subscriptions`,
    "communityInvestment"                                   -> `community-investment`,
    "seedEnterpriseInvestment"                              -> `seed-enterprise-investment`,
    "socialEnterpriseInvestment"                            -> `social-enterprise-investment`,
    "maintenancePayments"                                   -> `maintenance-payments`,
    "deficiencyRelief"                                      -> `deficiency-relief`,
    "nonDeductableLoanInterest"                             -> `non-deductible-loan-interest`,
    "qualifyingDistributionRedemptionOfSharesAndSecurities" -> `qualifying-distribution-redemption-of-shares-and-securities`
  )

  testWrites[ReliefsClaimedType](
    `vct-subscriptions`                                           -> "vct-subscriptions",
    `eis-subscriptions`                                           -> "eis-subscriptions",
    `community-investment`                                        -> "community-investment",
    `seed-enterprise-investment`                                  -> "seed-enterprise-investment",
    `social-enterprise-investment`                                -> "social-enterprise-investment",
    `maintenance-payments`                                        -> "maintenance-payments",
    `deficiency-relief`                                           -> "deficiency-relief",
    `non-deductible-loan-interest`                                -> "non-deductible-loan-interest",
    `qualifying-distribution-redemption-of-shares-and-securities` -> "qualifying-distribution-redemption-of-shares-and-securities"
  )
}
