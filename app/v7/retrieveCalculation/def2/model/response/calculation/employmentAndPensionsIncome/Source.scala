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

package v7.retrieveCalculation.def2.model.response.calculation.employmentAndPensionsIncome

import common.utils.enums.Enums
import play.api.libs.json.{Reads, Writes}

sealed trait Source

object Source {

  case object `customer`  extends Source
  case object `hmrc-held` extends Source

  implicit val writes: Writes[Source] = Enums.writes[Source]

  implicit val reads: Reads[Source] = Enums.readsUsing[Source] {
    case "customer" => `customer`
    case "HMRC HELD" => `hmrc-held`
  }

}
