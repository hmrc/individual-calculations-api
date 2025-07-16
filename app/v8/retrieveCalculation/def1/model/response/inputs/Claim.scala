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

package v8.retrieveCalculation.def1.model.response.inputs

import common.TaxYearFormats
import shared.models.domain.TaxYear
import play.api.libs.json.{Format, Json, OFormat}
import v8.common.model.response.{ClaimType, IncomeSourceType}
import v8.common.model.response.IncomeSourceType._

case class Claim(claimId: Option[String],
                      originatingClaimId: Option[String],
                      incomeSourceId: String,
                      incomeSourceType: IncomeSourceType,
                      submissionTimestamp: Option[String],
                      taxYearClaimMade: TaxYear,
                      claimType: ClaimType,
                      sequence: Option[Int])

object Claim {

  implicit val taxYearFormat: Format[TaxYear] = TaxYearFormats.downstreamIntToMtdFormat

  implicit val incomeSourceTypeFormat: Format[IncomeSourceType] = IncomeSourceType.formatRestricted(
    `self-employment`,
    `uk-property`,
    `uk-property-fhl`,
    `foreign-property-fhl-eea`,
    `foreign-property`
  )

  implicit val format: OFormat[Claim] = Json.format[Claim]
}
