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

package v7.common.model.response

import common.utils.enums.Enums
import play.api.libs.json.{Reads, Writes}

sealed trait ShortServiceRefundBandsName

object ShortServiceRefundBandsName {
  case object `lower-band` extends ShortServiceRefundBandsName
  case object `upper-band` extends ShortServiceRefundBandsName

  implicit val writes: Writes[ShortServiceRefundBandsName] = Enums.writes[ShortServiceRefundBandsName]

  implicit val reads: Reads[ShortServiceRefundBandsName] = Enums.readsUsing {
    case "lowerBand"  => `lower-band`
    case "upperBand"  => `upper-band`
  }

}