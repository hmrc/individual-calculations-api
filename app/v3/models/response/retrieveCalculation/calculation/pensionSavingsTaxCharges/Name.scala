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

package v3.models.response.retrieveCalculation.calculation.pensionSavingsTaxCharges

import play.api.libs.json.{Reads, Writes}
import utils.enums.Enums

sealed trait Name

object Name {
  case object `basic-rate` extends Name

  case object `intermediate-rate` extends Name

  case object `higher-rate` extends Name

  case object `additional-rate` extends Name

  implicit val writes: Writes[Name] = Enums.writes[Name]

  implicit val reads: Reads[Name] = Enums.readsUsing {
    case "BRT" => `basic-rate`
    case "IRT" => `intermediate-rate`
    case "HRT" => `higher-rate`
    case "ART" => `additional-rate`
  }
}
