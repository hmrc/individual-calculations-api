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

package v3.models.response.retrieveCalculation.calculation.dividendsIncome

import play.api.libs.json.{Reads, Writes}
import utils.enums.Enums

sealed trait IncomeSourceType

object IncomeSourceType {

  case object `uk-dividends` extends IncomeSourceType
  case object `uk-savings` extends IncomeSourceType
  case object `uk-securities` extends IncomeSourceType
  case object `foreign-dividends` extends IncomeSourceType
  case object `foreign-savings` extends IncomeSourceType

  implicit val writes: Writes[IncomeSourceType] = Enums.writes[IncomeSourceType]

  implicit val reads: Reads[IncomeSourceType] = Enums.readsUsing {
    case "07" => `foreign-dividends`
    case "09" => `uk-savings`
    case "10" => `uk-dividends`
    case "16" => `foreign-savings`
    case "18" => `uk-securities`
  }

}
