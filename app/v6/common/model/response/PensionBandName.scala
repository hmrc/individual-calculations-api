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

package v6.common.model.response

import common.utils.enums.Enums
import play.api.libs.json.{Reads, Writes}

sealed trait PensionBandName

object PensionBandName {
  case object `basic-rate`        extends PensionBandName
  case object `intermediate-rate` extends PensionBandName
  case object `higher-rate`       extends PensionBandName
  case object `additional-rate`   extends PensionBandName
  case object `advanced-rate`     extends PensionBandName

  implicit val writes: Writes[PensionBandName] = Enums.writes[PensionBandName]

  implicit val reads: Reads[PensionBandName] = Enums.readsUsing {
    case "BRT"  => `basic-rate`
    case "IRT"  => `intermediate-rate`
    case "HRT"  => `higher-rate`
    case "ART"  => `additional-rate`
    case "AVRT" => `advanced-rate`
  }

}
