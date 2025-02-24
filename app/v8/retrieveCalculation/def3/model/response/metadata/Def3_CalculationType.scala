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

package v8.retrieveCalculation.def3.model.response.metadata

import common.utils.enums.Enums
import play.api.libs.json.{Reads, Writes}

sealed trait Def3_CalculationType

object Def3_CalculationType {

  case object `in-year`              extends Def3_CalculationType
  case object `intent-to-finalise`   extends Def3_CalculationType
  case object `intent-to-amend`      extends Def3_CalculationType
  case object `declare-finalisation` extends Def3_CalculationType
  case object `confirm-amendment`    extends Def3_CalculationType

  implicit val writes: Writes[Def3_CalculationType] = Enums.writes[Def3_CalculationType]

  implicit val reads: Reads[Def3_CalculationType] = Enums.readsUsing[Def3_CalculationType] {
    case "IY" => `in-year`
    case "IF" => `intent-to-finalise`
    case "IA" => `intent-to-amend`
    case "DF" => `declare-finalisation`
    case "CA" => `confirm-amendment`
  }

}
