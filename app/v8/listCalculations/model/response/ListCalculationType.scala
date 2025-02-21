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

package v8.listCalculations.model.response

import common.utils.enums.Enums
import play.api.libs.json.{Reads, Writes}

sealed trait ListCalculationType

object ListCalculationType {

  case object `in-year`               extends ListCalculationType
  case object `intent-to-finalise`    extends ListCalculationType
  case object `intent-to-amend`       extends ListCalculationType
  case object `confirm-amendment`     extends ListCalculationType
  case object `final-declaration`     extends ListCalculationType
  case object `correction`            extends ListCalculationType

  implicit val writes: Writes[ListCalculationType] = Enums.writes[ListCalculationType]

  implicit val reads: Reads[ListCalculationType] = Enums.readsUsing[ListCalculationType] {
    case "inYear" | "IY"                  => `in-year`
    case "IF" | "IC"                      => `intent-to-finalise`
    case "IA"                             => `intent-to-amend`   // AM from 2150?
    case "CA"                             => `confirm-amendment` // AM from 2150?
    case "crystallisation" | "CR" | "DF"  => `final-declaration`
    case "CO"                             => `correction`
  }

}
