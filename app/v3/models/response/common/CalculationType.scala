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

package v3.models.response.common

import play.api.libs.json.Reads
import utils.enums.Enums

sealed trait CalculationType

object CalculationType {

  case object `inYear` extends CalculationType
  case object `endOfYear` extends CalculationType
  case object `biss` extends CalculationType
  case object `POA` extends CalculationType

  implicit val reads: Reads[CalculationType] = Enums.readsUsing{
    case "inYear" => `inYear`
    case "crystallisation" => `endOfYear`
    case "biss" => `biss`
    case "POA" => `POA`
  }
}