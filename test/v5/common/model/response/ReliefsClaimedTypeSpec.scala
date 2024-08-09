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

package v5.common.model.response

import shared.utils.UnitSpec
import utils.enums.EnumJsonSpecSupport
import v5.common.model.response.ReliefsClaimedType._

class ReliefsClaimedTypeSpec extends UnitSpec with EnumJsonSpecSupport {

  testReads[ReliefsClaimedType](
    "vctSubscriptions"                                      -> vctSubscriptions,
    "eisSubscriptions"                                      -> eisSubscriptions,
    "communityInvestment"                                   -> communityInvestment,
    "seedEnterpriseInvestment"                              -> seedEnterpriseInvestment,
    "socialEnterpriseInvestment"                            -> socialEnterpriseInvestment,
    "maintenancePayments"                                   -> maintenancePayments,
    "deficiencyRelief"                                      -> deficiencyRelief,
    "nonDeductableLoanInterest"                             -> nonDeductibleLoanInterest,
    "qualifyingDistributionRedemptionOfSharesAndSecurities" -> qualifyingDistributionRedemptionOfSharesAndSecurities
  )

  testWrites[ReliefsClaimedType](
    vctSubscriptions                                      -> "vctSubscriptions",
    eisSubscriptions                                      -> "eisSubscriptions",
    communityInvestment                                   -> "communityInvestment",
    seedEnterpriseInvestment                              -> "seedEnterpriseInvestment",
    socialEnterpriseInvestment                            -> "socialEnterpriseInvestment",
    maintenancePayments                                   -> "maintenancePayments",
    deficiencyRelief                                      -> "deficiencyRelief",
    nonDeductibleLoanInterest                             -> "nonDeductibleLoanInterest",
    qualifyingDistributionRedemptionOfSharesAndSecurities -> "qualifyingDistributionRedemptionOfSharesAndSecurities"
  )

}
