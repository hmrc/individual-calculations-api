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

package v8.retrieveCalculation.def3.model.response.calculation.taxCalculation

import common.utils.enums.Enums
import play.api.libs.json.{Reads, Writes}

sealed trait Nic4BandName

object Nic4BandName {
  case object `zero-rate` extends Nic4BandName

  case object `basic-rate` extends Nic4BandName

  case object `higher-rate` extends Nic4BandName

  implicit val writes: Writes[Nic4BandName] = Enums.writes[Nic4BandName]

  implicit val reads: Reads[Nic4BandName] = Enums.readsUsing {
    case "ZRT" => `zero-rate`
    case "BRT" => `basic-rate`
    case "HRT" => `higher-rate`
  }

}
