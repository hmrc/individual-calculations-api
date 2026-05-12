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

import common.utils.enums.EnumJsonSpecSupport
import shared.utils.UnitSpec
import v7.retrieveCalculation.def3.model.response.metadata.Def3_CalculationType._

class Def3_Retrieve_CalculationTypeSpec extends UnitSpec with EnumJsonSpecSupport {

  testReads[Def3_CalculationType](
    "IY" -> `in-year`,
    "IF" -> `intent-to-finalise`,
    "IA" -> `intent-to-amend`,
    "DF" -> `declare-finalisation`,
    "CA" -> `confirm-amendment`
  )

  testWrites[Def3_CalculationType](
    `in-year`              -> "in-year",
    `intent-to-finalise`   -> "intent-to-finalise",
    `intent-to-amend`      -> "intent-to-amend",
    `declare-finalisation` -> "declare-finalisation",
    `confirm-amendment`    -> "confirm-amendment"
  )

}
