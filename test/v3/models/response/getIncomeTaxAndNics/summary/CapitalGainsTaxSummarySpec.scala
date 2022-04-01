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

package v3.models.response.getIncomeTaxAndNics.summary

import support.UnitSpec
import v3.fixtures.getIncomeTaxAndNics.summary.CapitalGainsTaxSummaryFixture._
import v3.models.utils.JsonErrorValidators

class CapitalGainsTaxSummarySpec extends UnitSpec with JsonErrorValidators {

  testJsonProperties[CapitalGainsTaxSummary](capitalGainsTaxSummaryJson)(
    mandatoryProperties = Seq(
      "totalCapitalGainsIncome",
      "annualExemptionAmount",
      "totalTaxableGains",
      "capitalGainsTaxDue"
    ),
    optionalProperties = Seq(
      "capitalGainsTaxAmount",
      "adjustments",
      "adjustedCapitalGainsTax",
      "foreignTaxCreditRelief",
      "capitalGainsTaxAfterFTCR",
      "taxOnGainsAlreadyPaid",
      "capitalGainsOverpaid"
    )
  )
}