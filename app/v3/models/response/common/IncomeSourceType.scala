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

package v3.models.response.common

import play.api.libs.json.{Reads, Writes}
import utils.enums.Enums

sealed trait IncomeSourceType

object IncomeSourceType {
  case object `self-employment`           extends IncomeSourceType
  case object `uk-property-non-fhl`       extends IncomeSourceType
  case object `foreign-property-fhl-eea`  extends IncomeSourceType
  case object `uk-property-fhl`           extends IncomeSourceType
  case object `employments`               extends IncomeSourceType
  case object `foreign-income`            extends IncomeSourceType
  case object `foreign-dividends`         extends IncomeSourceType
  case object `uk-savings-and-gains`      extends IncomeSourceType
  case object `uk-dividends`              extends IncomeSourceType
  case object `state-benefits`            extends IncomeSourceType
  case object `gains-on-life-policies`    extends IncomeSourceType
  case object `share-schemes`             extends IncomeSourceType
  case object `foreign-property`          extends IncomeSourceType
  case object `foreign-savings-and-gains` extends IncomeSourceType
  case object `other-dividends`           extends IncomeSourceType
  case object `uk-securities`             extends IncomeSourceType
  case object `other-income`              extends IncomeSourceType
  case object `foreign-pension`           extends IncomeSourceType
  case object `non-paye-income`           extends IncomeSourceType
  case object `capital-gains-tax`         extends IncomeSourceType
  case object `charitable-giving`         extends IncomeSourceType

  implicit val writes: Writes[IncomeSourceType] = Enums.writes[IncomeSourceType]

  implicit val reads: Reads[IncomeSourceType] = Enums.readsUsing {
    case "01" => `self-employment`
    case "02" => `uk-property-non-fhl`
    case "03" => `foreign-property-fhl-eea`
    case "04" => `uk-property-fhl`
    case "05" => `employments`
    case "06" => `foreign-income`
    case "07" => `foreign-dividends`
    case "09" => `uk-savings-and-gains`
    case "10" => `uk-dividends`
    case "11" => `state-benefits`
    case "12" => `gains-on-life-policies`
    case "13" => `share-schemes`
    case "15" => `foreign-property`
    case "16" => `foreign-savings-and-gains`
    case "17" => `other-dividends`
    case "18" => `uk-securities`
    case "19" => `other-income`
    case "20" => `foreign-pension`
    case "21" => `non-paye-income`
    case "22" => `capital-gains-tax`
    case "98" => `charitable-giving`
  }

}
