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

package v8.common.model.response

import common.utils.enums.Enums
import play.api.libs.json.{Reads, Writes}

sealed trait ClaimType

object ClaimType {
  case object `carry-forward`                   extends ClaimType
  case object `carry-sideways`                  extends ClaimType
  case object `carry-forward-to-carry-sideways` extends ClaimType
  case object `carry-sideways-fhl`              extends ClaimType
  case object `carry-backwards`                 extends ClaimType
  case object `carry-backwards-general-income`  extends ClaimType

  implicit val writes: Writes[ClaimType] = Enums.writes[ClaimType]

  implicit val reads: Reads[ClaimType] = Enums.readsUsing {
    case "CF"     => `carry-forward`
    case "CSGI"   => `carry-sideways`
    case "CFCSGI" => `carry-forward-to-carry-sideways`
    case "CSFHL"  => `carry-sideways-fhl`
    case "CB"     => `carry-backwards`
    case "CBGI"   => `carry-backwards-general-income`
  }

}
