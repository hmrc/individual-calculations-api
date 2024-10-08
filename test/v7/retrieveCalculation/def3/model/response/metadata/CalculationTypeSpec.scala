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

package v7.retrieveCalculation.def3.model.response.metadata

import shared.utils.UnitSpec
import utils.enums.EnumJsonSpecSupport
import v7.retrieveCalculation.def3.model.response.metadata.CalculationType._

class CalculationTypeSpec extends UnitSpec with EnumJsonSpecSupport {

  testReads[CalculationType](
    "IY" -> `in-year`,
    "IF" -> `intend-to-finalise`,
    "IA" -> `intend-to-amend`,
    "DF" -> `declare-finalisation`,
    "CA" -> `confirm-amendment`
  )

  testWrites[CalculationType](
    `in-year`              -> "in-year",
    `intend-to-finalise`   -> "intend-to-finalise",
    `intend-to-amend`      -> "intend-to-amend",
    `declare-finalisation` -> "declare-finalisation",
    `confirm-amendment`    -> "confirm-amendment"
  )

}
