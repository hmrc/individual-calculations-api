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

sealed trait Def1_CalculationType

object Def1_CalculationType {

  case object `inYear`           extends Def1_CalculationType
  case object `finalDeclaration` extends Def1_CalculationType

  implicit val writes: Writes[Def1_CalculationType] = Enums.writes[Def1_CalculationType]

  implicit val reads: Reads[Def1_CalculationType] = Enums.readsUsing[Def1_CalculationType] {
    case "inYear"          => `inYear`
    case "crystallisation" => `finalDeclaration`
  }

}
