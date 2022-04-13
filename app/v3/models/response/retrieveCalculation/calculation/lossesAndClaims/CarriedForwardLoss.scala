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

package v3.models.response.retrieveCalculation.calculation.lossesAndClaims

import play.api.libs.json.{Format, Json, OFormat}
import v3.models.domain.TaxYear
import v3.models.response.common.{ClaimType, IncomeSourceType}

case class CarriedForwardLoss(
    claimId: Option[String],
    originatingClaimId: Option[String],
    incomeSourceId: String,
    incomeSourceType: IncomeSourceType,
    claimType: ClaimType,
    taxYearClaimMade: Option[TaxYear],
    taxYearLossIncurred: TaxYear,
    currentLossValue: BigInt,
    lossType: Option[String]
)

object CarriedForwardLoss {
  implicit val incomeSourceTypeFormat: Format[IncomeSourceType] = IncomeSourceType.formatRestricted(
    IncomeSourceType.`self-employment`,
    IncomeSourceType.`uk-property-non-fhl`,
    IncomeSourceType.`foreign-property-fhl-eea`,
    IncomeSourceType.`uk-property-fhl`,
    IncomeSourceType.`foreign-property`
  )

  implicit val format: OFormat[CarriedForwardLoss] = Json.format[CarriedForwardLoss]
}
