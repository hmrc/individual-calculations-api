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

package v7.retrieveCalculation.def2.model.response.inputs

import common.utils.enums.EnumJsonSpecSupport
import shared.utils.UnitSpec
import v7.retrieveCalculation.def2.model.response.inputs.AllowancesReliefsAndDeductionsType._

class AllowancesReliefsAndDeductionsTypeSpec extends UnitSpec with EnumJsonSpecSupport {

  testReads[AllowancesReliefsAndDeductionsType](
    "investmentReliefs" -> `investment-reliefs`,
    "otherReliefs"      -> `other-reliefs`,
    "otherExpenses"     -> `other-expenses`,
    "otherDeductions"   -> `other-deductions`,
    "foreignReliefs"    -> `foreign-reliefs`
  )

  testWrites[AllowancesReliefsAndDeductionsType](
    `investment-reliefs` -> "investment-reliefs",
    `other-reliefs`      -> "other-reliefs",
    `other-expenses`     -> "other-expenses",
    `other-deductions`   -> "other-deductions",
    `foreign-reliefs`    -> "foreign-reliefs"
  )
}
