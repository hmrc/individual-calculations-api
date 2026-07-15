/*
 * Copyright 2026 HM Revenue & Customs
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

package v9.retrieveCalculation.def4.model.response.metadata

import api.utils.UnitSpec
import common.utils.enums.EnumJsonSpecSupport
import v9.retrieveCalculation.def4.model.response.metadata.CalculationReason.*

class CalculationReasonSpec extends UnitSpec with EnumJsonSpecSupport {

  testReads[CalculationReason](
    "customerRequest"                -> `customer-request`,
    "class2NICEvent"                 -> `class2-nic-event`,
    "newAnnualAdjustmentEvent"       -> `new-annual-adjustment-event`,
    "updatedAnnualAdjustmentEvent"   -> `updated-annual-adjustment-event`,
    "unattendedCalculation"          -> `unattended-calculation`,
    "HMRCrevenueAmendment"           -> `enquiry-amendment`,
    "HMRCautoCorrection"             -> `auto-correction`,
    "HMRCmanualCorrection"           -> `manual-correction`,
    "marriageAllowance"              -> `marriage-allowance`,
    "nationalInsurance"              -> `class2-national-insurance`,
    "devolvedResidency"              -> `devolved-residency`,
    "customerRejectionOfaCorrection" -> `customer-rejection-of-a-correction`
  )

  testWrites[CalculationReason](
    `customer-request`                   -> "customer-request",
    `class2-nic-event`                   -> "class2-nic-event",
    `new-annual-adjustment-event`        -> "new-annual-adjustment-event",
    `updated-annual-adjustment-event`    -> "updated-annual-adjustment-event",
    `unattended-calculation`             -> "unattended-calculation",
    `enquiry-amendment`                  -> "enquiry-amendment",
    `auto-correction`                    -> "auto-correction",
    `manual-correction`                  -> "manual-correction",
    `marriage-allowance`                 -> "marriage-allowance",
    `class2-national-insurance`          -> "class2-national-insurance",
    `devolved-residency`                 -> "devolved-residency",
    `customer-rejection-of-a-correction` -> "customer-rejection-of-a-correction"
  )

}
