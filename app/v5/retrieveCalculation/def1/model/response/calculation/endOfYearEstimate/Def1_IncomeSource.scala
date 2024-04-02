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
import v5.retrieveCalculation.def1.model.response.common.Def1_IncomeSourceType

case class Def1_IncomeSource(
    incomeSourceId: Option[String],
    incomeSourceType: Def1_IncomeSourceType,
    incomeSourceName: Option[String],
    taxableIncome: BigInt,
    finalised: Option[Boolean]
)

object Def1_IncomeSource {

  implicit val incomeSourceTypeFormat: Format[Def1_IncomeSourceType] = Def1_IncomeSourceType.formatRestricted(
    Def1_IncomeSourceType.`self-employment`,
    Def1_IncomeSourceType.`uk-property-non-fhl`,
    Def1_IncomeSourceType.`foreign-property-fhl-eea`,
    Def1_IncomeSourceType.`uk-property-fhl`,
    Def1_IncomeSourceType.`employments`,
    Def1_IncomeSourceType.`foreign-income`,
    Def1_IncomeSourceType.`foreign-dividends`,
    Def1_IncomeSourceType.`uk-savings-and-gains`,
    Def1_IncomeSourceType.`uk-dividends`,
    Def1_IncomeSourceType.`state-benefits`,
    Def1_IncomeSourceType.`gains-on-life-policies`,
    Def1_IncomeSourceType.`share-schemes`,
    Def1_IncomeSourceType.`foreign-property`,
    Def1_IncomeSourceType.`foreign-savings-and-gains`,
    Def1_IncomeSourceType.`other-dividends`,
    Def1_IncomeSourceType.`uk-securities`,
    Def1_IncomeSourceType.`other-income`,
    Def1_IncomeSourceType.`foreign-pension`,
    Def1_IncomeSourceType.`non-paye-income`,
    Def1_IncomeSourceType.`capital-gains-tax`,
    Def1_IncomeSourceType.`charitable-giving`
  )

  implicit val format: OFormat[Def1_IncomeSource] = Json.format[Def1_IncomeSource]
}
