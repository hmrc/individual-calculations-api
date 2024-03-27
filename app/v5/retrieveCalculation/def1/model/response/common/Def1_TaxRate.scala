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

package v5.retrieveCalculation.def1.model.response.common

import play.api.libs.json.{Reads, Writes}
import utils.enums.Enums

sealed trait Def1_TaxRate

object Def1_TaxRate {
  case object `basic-rate`        extends Def1_TaxRate
  case object `intermediate-rate` extends Def1_TaxRate
  case object `higher-rate`       extends Def1_TaxRate
  case object `additional-rate`   extends Def1_TaxRate

  implicit val writes: Writes[Def1_TaxRate] = Enums.writes[Def1_TaxRate]

  implicit val reads: Reads[Def1_TaxRate] = Enums.readsUsing {
    case "BRT" => `basic-rate`
    case "IRT" => `intermediate-rate`
    case "HRT" => `higher-rate`
    case "ART" => `additional-rate`
  }

}
