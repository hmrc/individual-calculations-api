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

package v7.retrieveCalculation.def3.model.response.calculation.lossesAndClaims

import common.TaxYearFormats
import shared.models.domain.TaxYear
import play.api.libs.json.{Format, Json, OFormat}
import v7.common.model.response.{ClaimType, IncomeSourceType}

case class ResultOfClaimsApplied(
    claimId: Option[String],
    originatingClaimId: Option[String],
    incomeSourceId: String,
    incomeSourceType: IncomeSourceType,
    taxYearClaimMade: TaxYear,
    claimType: ClaimType,
    mtdLoss: Option[Boolean],
    taxYearLossIncurred: TaxYear,
    lossAmountUsed: BigInt,
    remainingLossValue: BigInt,
    lossType: Option[LossType]
)

object ResultOfClaimsApplied {

  implicit val taxYearFormat: Format[TaxYear] = TaxYearFormats.downstreamIntToMtdFormat

  implicit val incomeSourceTypeFormat: Format[IncomeSourceType] = IncomeSourceType.formatRestricted(
    IncomeSourceType.`self-employment`,
    IncomeSourceType.`uk-property`,
    IncomeSourceType.`foreign-property`
  )

  implicit val format: OFormat[ResultOfClaimsApplied] = Json.format[ResultOfClaimsApplied]
}
