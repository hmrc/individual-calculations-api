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

package v8.retrieveCalculation.def1.model.response.metadata

import common.utils.enums.Enums
import play.api.libs.json.{Reads, Writes}

sealed trait CalculationReason

object CalculationReason {
  case object `customer-request`                extends CalculationReason
  case object `class2-nic-event`                extends CalculationReason
  case object `new-loss-event`                  extends CalculationReason
  case object `updated-loss-event`              extends CalculationReason
  case object `new-claim-event`                 extends CalculationReason
  case object `updated-claim-event`             extends CalculationReason
  case object `new-annual-adjustment-event`     extends CalculationReason
  case object `updated-annual-adjustment-event` extends CalculationReason
  case object `unattended-calculation`          extends CalculationReason

  implicit val writes: Writes[CalculationReason] = Enums.writes[CalculationReason]

  implicit val reads: Reads[CalculationReason] = Enums.readsUsing {
    case "customerRequest"              => `customer-request`
    case "class2NICEvent"               => `class2-nic-event`
    case "newLossEvent"                 => `new-loss-event`
    case "updatedLossEvent"             => `updated-loss-event`
    case "newClaimEvent"                => `new-claim-event`
    case "updatedClaimEvent"            => `updated-claim-event`
    case "newAnnualAdjustmentEvent"     => `new-annual-adjustment-event`
    case "updatedAnnualAdjustmentEvent" => `updated-annual-adjustment-event`
    case "unattendedCalculation"        => `unattended-calculation`
  }

}
