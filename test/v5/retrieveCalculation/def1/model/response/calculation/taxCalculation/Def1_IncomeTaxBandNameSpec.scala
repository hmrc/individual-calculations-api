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

package v5.retrieveCalculation.def1.model.response.calculation.taxCalculation

import support.UnitSpec
import utils.enums.EnumJsonSpecSupport
import v5.retrieveCalculation.def1.model.response.calculation.taxCalculation.Def1_IncomeTaxBandName._

class Def1_IncomeTaxBandNameSpec extends UnitSpec with EnumJsonSpecSupport {

  testReads[Def1_IncomeTaxBandName](
    "SSR"   -> `savings-starter-rate`,
    "ZRTBR" -> `allowance-awarded-at-basic-rate`,
    "ZRTHR" -> `allowance-awarded-at-higher-rate`,
    "ZRTAR" -> `allowance-awarded-at-additional-rate`,
    "SRT"   -> `starter-rate`,
    "BRT"   -> `basic-rate`,
    "IRT"   -> `intermediate-rate`,
    "HRT"   -> `higher-rate`,
    "ART"   -> `additional-rate`
  )

  testWrites[Def1_IncomeTaxBandName](
    `savings-starter-rate`                 -> "savings-starter-rate",
    `allowance-awarded-at-basic-rate`      -> "allowance-awarded-at-basic-rate",
    `allowance-awarded-at-higher-rate`     -> "allowance-awarded-at-higher-rate",
    `allowance-awarded-at-additional-rate` -> "allowance-awarded-at-additional-rate",
    `starter-rate`                         -> "starter-rate",
    `basic-rate`                           -> "basic-rate",
    `intermediate-rate`                    -> "intermediate-rate",
    `higher-rate`                          -> "higher-rate",
    `additional-rate`                      -> "additional-rate"
  )

}
