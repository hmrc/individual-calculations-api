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

package v3.models.response.retrieveCalculation.calculation.reliefs

import play.api.libs.json.{Format, Json, OFormat}
import v3.models.response.common.ReliefsClaimedType
import v3.models.response.common.ReliefsClaimedType._

case class ReliefsClaimed(`type`: ReliefsClaimedType,
                          amountClaimed: Option[BigDecimal],
                          allowableAmount: Option[BigDecimal],
                          amountUsed: Option[BigDecimal],
                          rate: Option[BigDecimal],
                          reliefsClaimedDetail: Option[Seq[ReliefsClaimedDetail]])

object ReliefsClaimed {

  implicit val reliefClaimedTypeFormat: Format[ReliefsClaimedType] =
    ReliefsClaimedType.formatRestricted(
      `vctSubscriptions`,
      `eisSubscriptions`,
      `communityInvestment`,
      `seedEnterpriseInvestment`,
      `socialEnterpriseInvestment`,
      `maintenancePayments`,
      `deficiencyRelief`,
      `nonDeductibleLoanInterest`,
      `qualifyingDistributionRedemptionOfSharesAndSecurities`
    )

  implicit val format: OFormat[ReliefsClaimed] = Json.format[ReliefsClaimed]
}
