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

package v5.retrieveCalculation.def1.model.response.common

import shared.utils.UnitSpec
import utils.enums.EnumJsonSpecSupport
import v5.retrieveCalculation.def1.model.response.common.TaxRate._

class TaxRateSpec extends UnitSpec with EnumJsonSpecSupport {

  testReads[TaxRate](
    "BRT" -> `basic-rate`,
    "IRT" -> `intermediate-rate`,
    "HRT" -> `higher-rate`,
    "ART" -> `additional-rate`
  )

  testWrites[TaxRate](
    `basic-rate`        -> "basic-rate",
    `intermediate-rate` -> "intermediate-rate",
    `higher-rate`       -> "higher-rate",
    `additional-rate`   -> "additional-rate"
  )

}
