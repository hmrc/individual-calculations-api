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

package v7.retrieveCalculation.def2.model.response.inputs

import play.api.libs.json.{Format, Json, OFormat}
import v7.common.model.response.IncomeSourceType
import IncomeSourceType._

case class NonBusinessIncomeSource(incomeSourceId: Option[String],
                                   incomeSourceType: IncomeSourceType,
                                   incomeSourceName: Option[String],
                                   startDate: String,
                                   endDate: Option[String],
                                   source: String,
                                   periodId: Option[String],
                                   latestReceivedDateTime: Option[String])

case object NonBusinessIncomeSource {

  implicit val incomeSourceTypeFormat: Format[IncomeSourceType] = IncomeSourceType.formatRestricted(
    `employments`,
    `foreign-dividends`,
    `uk-savings-and-gains`,
    `uk-dividends`,
    `state-benefits`,
    `gains-on-life-policies`,
    `foreign-savings-and-gains`,
    `other-dividends`,
    `uk-securities`,
    `other-income`,
    `foreign-pension`,
    `non-paye-income`,
    `capital-gains-tax`,
    `charitable-giving`
  )

  implicit val format: OFormat[NonBusinessIncomeSource] = Json.format[NonBusinessIncomeSource]
}
