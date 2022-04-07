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

package v2.models.response.getIncomeTaxAndNics.detail.capitalGainsTax

import support.UnitSpec
import v2.fixtures.getIncomeTaxAndNics.detail.capitalGainsTax.OtherGainsFixture.otherGainsJson
import v2.models.utils.JsonErrorValidators

class OtherGainsSpec extends UnitSpec with JsonErrorValidators {

  testJsonProperties[OtherGains](otherGainsJson)(
    mandatoryProperties = Seq(),
    optionalProperties = Seq(
      "gainsIncome",
      "lossesBroughtForward",
      "lossesArisingThisYear",
      "gainsAfterLosses",
      "attributedGains",
      "netGains",
      "annualExemptionAmount",
      "taxableGains",
      "cgtTaxBands",
      "totalTaxAmount"
    )
  )

}
