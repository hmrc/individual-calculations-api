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

package v5.retrieveCalculation.def1.model.response.calculation.endOfYearEstimate

import play.api.libs.json.{Format, Json, OFormat}
import v5.retrieveCalculation.def1.model.response.common.IncomeSourceType

case class IncomeSource(
    incomeSourceId: Option[String],
    incomeSourceType: IncomeSourceType,
    incomeSourceName: Option[String],
    taxableIncome: BigInt,
    finalised: Option[Boolean]
)

object IncomeSource {

  implicit val incomeSourceTypeFormat: Format[IncomeSourceType] = IncomeSourceType.formatRestricted(
    IncomeSourceType.`self-employment`,
    IncomeSourceType.`uk-property-non-fhl`,
    IncomeSourceType.`foreign-property-fhl-eea`,
    IncomeSourceType.`uk-property-fhl`,
    IncomeSourceType.`employments`,
    IncomeSourceType.`foreign-income`,
    IncomeSourceType.`foreign-dividends`,
    IncomeSourceType.`uk-savings-and-gains`,
    IncomeSourceType.`uk-dividends`,
    IncomeSourceType.`state-benefits`,
    IncomeSourceType.`gains-on-life-policies`,
    IncomeSourceType.`share-schemes`,
    IncomeSourceType.`foreign-property`,
    IncomeSourceType.`foreign-savings-and-gains`,
    IncomeSourceType.`other-dividends`,
    IncomeSourceType.`uk-securities`,
    IncomeSourceType.`other-income`,
    IncomeSourceType.`foreign-pension`,
    IncomeSourceType.`non-paye-income`,
    IncomeSourceType.`capital-gains-tax`,
    IncomeSourceType.`charitable-giving`
  )

  implicit val format: OFormat[IncomeSource] = Json.format[IncomeSource]
}
