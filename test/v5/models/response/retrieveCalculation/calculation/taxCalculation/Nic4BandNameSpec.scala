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

package v5.models.response.retrieveCalculation.calculation.taxCalculation

import support.UnitSpec
import utils.enums.EnumJsonSpecSupport
import v5.models.response.retrieveCalculation.calculation.taxCalculation.Nic4BandName._

class Nic4BandNameSpec extends UnitSpec with EnumJsonSpecSupport {

  testReads[Nic4BandName](
    "ZRT" -> `zero-rate`,
    "BRT" -> `basic-rate`,
    "HRT" -> `higher-rate`
  )

  testWrites[Nic4BandName](
    `zero-rate`   -> "zero-rate",
    `basic-rate`  -> "basic-rate",
    `higher-rate` -> "higher-rate"
  )

}
