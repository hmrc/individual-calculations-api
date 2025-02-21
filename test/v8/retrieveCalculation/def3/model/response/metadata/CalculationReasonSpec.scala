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

package v8.retrieveCalculation.def3.model.response.metadata

import common.utils.enums.EnumJsonSpecSupport
import shared.utils.UnitSpec
import v7.retrieveCalculation.def3.model.response.metadata.CalculationReason._

class CalculationReasonSpec extends UnitSpec with EnumJsonSpecSupport {

  testReads[CalculationReason](
    "customerRequest"              -> `customer-request`,
    "class2NICEvent"               -> `class2-nic-event`,
    "newLossEvent"                 -> `new-loss-event`,
    "updatedLossEvent"             -> `updated-loss-event`,
    "newClaimEvent"                -> `new-claim-event`,
    "updatedClaimEvent"            -> `updated-claim-event`,
    "newAnnualAdjustmentEvent"     -> `new-annual-adjustment-event`,
    "updatedAnnualAdjustmentEvent" -> `updated-annual-adjustment-event`,
    "unattendedCalculation"        -> `unattended-calculation`
  )

  testWrites[CalculationReason](
    `customer-request`                -> "customer-request",
    `class2-nic-event`                -> "class2-nic-event",
    `new-loss-event`                  -> "new-loss-event",
    `updated-loss-event`              -> "updated-loss-event",
    `new-claim-event`                 -> "new-claim-event",
    `updated-claim-event`             -> "updated-claim-event",
    `new-annual-adjustment-event`     -> "new-annual-adjustment-event",
    `updated-annual-adjustment-event` -> "updated-annual-adjustment-event",
    `unattended-calculation`          -> "unattended-calculation"
  )

}
