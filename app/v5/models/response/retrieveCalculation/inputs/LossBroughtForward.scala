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

package v5.models.response.retrieveCalculation.inputs

import api.models.domain.TaxYear
import play.api.libs.json.{Format, Json, OFormat}
import v5.models.response.common.IncomeSourceType
import v5.models.response.common.IncomeSourceType._

case class LossBroughtForward(lossId: Option[String],
                              incomeSourceId: String,
                              incomeSourceType: IncomeSourceType,
                              submissionTimestamp: Option[String],
                              lossType: Option[String],
                              taxYearLossIncurred: TaxYear,
                              currentLossValue: BigInt,
                              mtdLoss: Option[Boolean])

object LossBroughtForward {

  implicit val taxYearFormat: Format[TaxYear] = TaxYear.downstreamIntToMtdFormat

  implicit val incomeSourceTypeFormat: Format[IncomeSourceType] = IncomeSourceType.formatRestricted(
    `self-employment`,
    `uk-property-non-fhl`,
    `uk-property-fhl`,
    `foreign-property-fhl-eea`,
    `foreign-property`
  )

  implicit val format: OFormat[LossBroughtForward] = Json.format[LossBroughtForward]
}
