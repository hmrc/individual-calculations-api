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

package v5.retrieveCalculation.def1.model.response.calculation.lossesAndClaims

import api.models.domain.TaxYear
import play.api.libs.json.{Format, Json, OFormat}
import v5.retrieveCalculation.def1.model.response.common.{Def1_ClaimType, Def1_IncomeSourceType}

case class Def1_ClaimNotApplied(
    claimId: String,
    incomeSourceId: String,
    incomeSourceType: Def1_IncomeSourceType,
    taxYearClaimMade: TaxYear,
    claimType: Def1_ClaimType
)

object Def1_ClaimNotApplied {

  implicit val taxYearFormat: Format[TaxYear] = TaxYear.downstreamIntToMtdFormat

  implicit val incomeSourceTypeFormat: Format[Def1_IncomeSourceType] = Def1_IncomeSourceType.formatRestricted(
    Def1_IncomeSourceType.`self-employment`,
    Def1_IncomeSourceType.`uk-property-non-fhl`,
    Def1_IncomeSourceType.`foreign-property-fhl-eea`,
    Def1_IncomeSourceType.`uk-property-fhl`,
    Def1_IncomeSourceType.`foreign-property`
  )

  implicit val format: OFormat[Def1_ClaimNotApplied] = Json.format[Def1_ClaimNotApplied]
}
