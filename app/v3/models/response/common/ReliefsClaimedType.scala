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

package v3.models.response.common

import play.api.libs.json.{Format, JsResult, JsString, JsValue, Json, Reads, Writes}
import utils.enums.Enums

sealed trait ReliefsClaimedType

object ReliefsClaimedType {
  case object `vctSubscriptions`                                      extends ReliefsClaimedType
  case object `eisSubscriptions`                                      extends ReliefsClaimedType
  case object `communityInvestment`                                   extends ReliefsClaimedType
  case object `seedEnterpriseInvestment`                              extends ReliefsClaimedType
  case object `socialEnterpriseInvestment`                            extends ReliefsClaimedType
  case object `maintenancePayments`                                   extends ReliefsClaimedType
  case object `deficiencyRelief`                                      extends ReliefsClaimedType
  case object `nonDeductibleLoanInterest`                             extends ReliefsClaimedType
  case object `qualifyingDistributionRedemptionOfSharesAndSecurities` extends ReliefsClaimedType

  implicit val reliefsClaimedTypeWrites: Writes[ReliefsClaimedType] = Enums.writes[ReliefsClaimedType]

  implicit val reads: Reads[ReliefsClaimedType] = Enums.readsUsing {
    case "vctSubscriptions"                                      => `vctSubscriptions`
    case "eisSubscriptions"                                      => `eisSubscriptions`
    case "communityInvestment"                                   => `communityInvestment`
    case "seedEnterpriseInvestment"                              => `seedEnterpriseInvestment`
    case "socialEnterpriseInvestment"                            => `socialEnterpriseInvestment`
    case "maintenancePayments"                                   => `maintenancePayments`
    case "deficiencyRelief"                                      => `deficiencyRelief`
    case "nonDeductableLoanInterest"                             => `nonDeductibleLoanInterest`
    case "qualifyingDistributionRedemptionOfSharesAndSecurities" => `qualifyingDistributionRedemptionOfSharesAndSecurities`
  }

  def formatRestricted(types: ReliefsClaimedType*): Format[ReliefsClaimedType] = new Format[ReliefsClaimedType] {
    override def writes(o: ReliefsClaimedType): JsString            = Json.toJson(o)(reliefsClaimedTypeWrites).as[JsString]
    override def reads(json: JsValue): JsResult[ReliefsClaimedType] = json.validate[ReliefsClaimedType](Enums.readsRestricted(types: _*))
  }

}
