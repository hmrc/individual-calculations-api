/*
 * Copyright 2026 HM Revenue & Customs
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

package v7.listCalculations.def1.model.response

import common.utils.enums.EnumJsonSpecSupport
import shared.utils.UnitSpec
import v7.listCalculations.model.response.ListCalculationType
import v7.listCalculations.model.response.ListCalculationType.*

class ListCalculationTypeSpec extends UnitSpec with EnumJsonSpecSupport {

  testReads[ListCalculationType](
    "inYear"          -> `in-year`,
    "IY"              -> `in-year`,
    "IF"              -> `intent-to-finalise`,
    "IC"              -> `intent-to-finalise`,
    "IA"              -> `intent-to-amend`,
    "CA"              -> `confirm-amendment`,
    "crystallisation" -> `final-declaration`,
    "CR"              -> `final-declaration`,
    "DF"              -> `final-declaration`,
    "CO"              -> `correction`
  )

  testWrites[ListCalculationType](
    `in-year`            -> "in-year",
    `intent-to-finalise` -> "intent-to-finalise",
    `intent-to-amend`    -> "intent-to-amend",
    `confirm-amendment`  -> "confirm-amendment",
    `final-declaration`  -> "final-declaration",
    `correction`         -> "correction"
  )

}
