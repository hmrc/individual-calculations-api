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

import support.UnitSpec
import utils.enums.EnumJsonSpecSupport
import IncomeSourceType._

class IncomeSourceTypeSpec extends UnitSpec with EnumJsonSpecSupport {

  testReads[IncomeSourceType](
    "01" -> `self-employment`,
    "02" -> `uk-property-non-fhl`,
    "03" -> `foreign-property-fhl-eea`,
    "04" -> `uk-property-fhl`,
    "05" -> `employments`,
    "06" -> `foreign-income`,
    "07" -> `foreign-dividends`,
    "09" -> `uk-savings-and-gains`,
    "10" -> `uk-dividends`,
    "11" -> `state-benefits`,
    "12" -> `gains-on-life-policies`,
    "13" -> `share-schemes`,
    "15" -> `foreign-property`,
    "16" -> `foreign-savings-and-gains`,
    "17" -> `other-dividends`,
    "18" -> `uk-securities`,
    "19" -> `other-income`,
    "20" -> `foreign-pension`,
    "21" -> `non-paye-income`,
    "22" -> `capital-gains-tax`,
    "98" -> `charitable-giving`
  )

  testWrites[IncomeSourceType](
    `self-employment`           -> "self-employment",
    `uk-property-non-fhl`       -> "uk-property-non-fhl",
    `foreign-property-fhl-eea`  -> "foreign-property-fhl-eea",
    `uk-property-fhl`           -> "uk-property-fhl",
    `employments`               -> "employments",
    `foreign-income`            -> "foreign-income",
    `foreign-dividends`         -> "foreign-dividends",
    `uk-savings-and-gains`      -> "uk-savings-and-gains",
    `uk-dividends`              -> "uk-dividends",
    `state-benefits`            -> "state-benefits",
    `gains-on-life-policies`    -> "gains-on-life-policies",
    `share-schemes`             -> "share-schemes",
    `foreign-property`          -> "foreign-property",
    `foreign-savings-and-gains` -> "foreign-savings-and-gains",
    `other-dividends`           -> "other-dividends",
    `uk-securities`             -> "uk-securities",
    `other-income`              -> "other-income",
    `foreign-pension`           -> "foreign-pension",
    `non-paye-income`           -> "non-paye-income",
    `capital-gains-tax`         -> "capital-gains-tax",
    `charitable-giving`         -> "charitable-giving"
  )

}
