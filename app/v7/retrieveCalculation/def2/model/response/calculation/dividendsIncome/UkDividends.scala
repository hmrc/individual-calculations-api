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

package v7.retrieveCalculation.def2.model.response.calculation.dividendsIncome

import play.api.libs.json.{Format, Json}
import v7.common.model.response.IncomeSourceType
import IncomeSourceType.`uk-dividends`

case class UkDividends(incomeSourceId: Option[String],
                       incomeSourceType: Option[IncomeSourceType],
                       dividends: Option[BigInt],
                       otherUkDividends: Option[BigInt])

object UkDividends {
  implicit val incomeSourceTypeFormat: Format[IncomeSourceType] = IncomeSourceType.formatRestricted(`uk-dividends`)
  implicit val format: Format[UkDividends]                      = Json.format[UkDividends]
}
