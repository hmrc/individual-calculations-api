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

package v5.common.model.response

import common.utils.enums.Enums
import play.api.libs.json.{Reads, Writes}

sealed trait StudentLoanPlanType

object StudentLoanPlanType {

  case object `plan1`        extends StudentLoanPlanType
  case object `plan2`        extends StudentLoanPlanType
  case object `postgraduate` extends StudentLoanPlanType
  case object `plan4`        extends StudentLoanPlanType

  implicit val writes: Writes[StudentLoanPlanType] = Enums.writes[StudentLoanPlanType]

  implicit val reads: Reads[StudentLoanPlanType] = Enums.readsUsing {
    case "01" => `plan1`
    case "02" => `plan2`
    case "03" => `postgraduate`
    case "04" => `plan4`
  }

}
