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

package v8.retrieveCalculation.def1.model.response.calculation.taxCalculation

import common.utils.enums.Enums
import play.api.libs.json.{Reads, Writes}

sealed trait CgtBandName

object CgtBandName {
  case object `lower-rate`  extends CgtBandName
  case object `higher-rate` extends CgtBandName

  implicit val writes: Writes[CgtBandName] = Enums.writes[CgtBandName]

  implicit val reads: Reads[CgtBandName] = Enums.readsUsing {
    case "lowerRate"  => `lower-rate`
    case "higherRate" => `higher-rate`
  }

}
