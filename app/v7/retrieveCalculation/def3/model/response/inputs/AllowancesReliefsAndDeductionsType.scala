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

package v7.retrieveCalculation.def3.model.response.inputs

import common.utils.enums.Enums
import play.api.libs.json.{Reads, Writes}

sealed trait AllowancesReliefsAndDeductionsType

object AllowancesReliefsAndDeductionsType {
  case object `investment-reliefs`  extends AllowancesReliefsAndDeductionsType
  case object `other-reliefs`       extends AllowancesReliefsAndDeductionsType
  case object `other-expenses`      extends AllowancesReliefsAndDeductionsType
  case object `other-deductions`    extends AllowancesReliefsAndDeductionsType
  case object `foreign-reliefs`     extends AllowancesReliefsAndDeductionsType

  implicit val writes: Writes[AllowancesReliefsAndDeductionsType] = Enums.writes[AllowancesReliefsAndDeductionsType]

  implicit val reads: Reads[AllowancesReliefsAndDeductionsType] = Enums.readsUsing {
    case "investmentReliefs"  => `investment-reliefs`
    case "otherReliefs"       => `other-reliefs`
    case "otherExpenses"      => `other-expenses`
    case "otherDeductions"    => `other-deductions`
    case "foreignReliefs"     => `foreign-reliefs`
  }

}
