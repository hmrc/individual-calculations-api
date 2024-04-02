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

package v5.retrieveCalculation.def1.model.response.common

import play.api.libs.json._
import utils.enums.Values.MkValues
import utils.enums._

sealed trait Def1_IncomeSourceType

object Def1_IncomeSourceType {

  case object `self-employment`           extends Def1_IncomeSourceType
  case object `uk-property-non-fhl`       extends Def1_IncomeSourceType
  case object `foreign-property-fhl-eea`  extends Def1_IncomeSourceType
  case object `uk-property-fhl`           extends Def1_IncomeSourceType
  case object `employments`               extends Def1_IncomeSourceType
  case object `foreign-income`            extends Def1_IncomeSourceType
  case object `foreign-dividends`         extends Def1_IncomeSourceType
  case object `uk-savings-and-gains`      extends Def1_IncomeSourceType
  case object `uk-dividends`              extends Def1_IncomeSourceType
  case object `state-benefits`            extends Def1_IncomeSourceType
  case object `gains-on-life-policies`    extends Def1_IncomeSourceType
  case object `share-schemes`             extends Def1_IncomeSourceType
  case object `foreign-property`          extends Def1_IncomeSourceType
  case object `foreign-savings-and-gains` extends Def1_IncomeSourceType
  case object `other-dividends`           extends Def1_IncomeSourceType
  case object `uk-securities`             extends Def1_IncomeSourceType
  case object `other-income`              extends Def1_IncomeSourceType
  case object `foreign-pension`           extends Def1_IncomeSourceType
  case object `non-paye-income`           extends Def1_IncomeSourceType
  case object `capital-gains-tax`         extends Def1_IncomeSourceType
  case object `charitable-giving`         extends Def1_IncomeSourceType

  implicit val incomeSourceTypeValues: MkValues[Def1_IncomeSourceType] = new MkValues[Def1_IncomeSourceType] {

    override def values: List[Def1_IncomeSourceType] =
      List(
        `self-employment`,
        `uk-property-non-fhl`,
        `foreign-property-fhl-eea`,
        `uk-property-fhl`,
        `employments`,
        `foreign-income`,
        `foreign-dividends`,
        `uk-savings-and-gains`,
        `uk-dividends`,
        `state-benefits`,
        `gains-on-life-policies`,
        `share-schemes`,
        `foreign-property`,
        `foreign-savings-and-gains`,
        `other-dividends`,
        `uk-securities`,
        `other-income`,
        `foreign-pension`,
        `non-paye-income`,
        `capital-gains-tax`,
        `charitable-giving`
      )

  }

  implicit val incomeSourceTypeWrites: Writes[Def1_IncomeSourceType] = Enums.writes[Def1_IncomeSourceType]

  implicit val reads: Reads[Def1_IncomeSourceType] = Enums.readsUsing {
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

  def formatRestricted(types: Def1_IncomeSourceType*): Format[Def1_IncomeSourceType] = new Format[Def1_IncomeSourceType] {
    override def writes(o: Def1_IncomeSourceType): JsValue = incomeSourceTypeWrites.writes(o)

    override def reads(json: JsValue): JsResult[Def1_IncomeSourceType] = json.validate[Def1_IncomeSourceType](Enums.readsRestricted(types: _*))
  }

}
