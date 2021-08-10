/*
 * Copyright 2021 HM Revenue & Customs
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

package v2.fixtures.getIncomeTaxAndNics.detail.capitalGainsTax

import play.api.libs.json.{JsValue, Json}
import v2.fixtures.getIncomeTaxAndNics.detail.capitalGainsTax.CgtTaxBandFixture._
import v2.models.response.getIncomeTaxAndNics.detail.capitalGainsTax.{CgtTaxBandName, OtherGains}

object OtherGainsFixture {
  def otherGainsModel: OtherGains =
    OtherGains(
      gainsIncome = Some(100.25),
      lossesBroughtForward = Some(200.25),
      lossesArisingThisYear = Some(300.25),
      gainsAfterLosses = Some(400.25),
      attributedGains = Some(500.25),
      netGains = Some(600.25),
      annualExemptionAmount = Some(700.25),
      taxableGains = Some(800.25),
      cgtTaxBands = Some(Seq(cgtTaxBandModel(CgtTaxBandName.lowerRate), cgtTaxBandModel(CgtTaxBandName.higherRate))),
      totalTaxAmount = Some(900.25)
    )

  def otherGainsJson: JsValue = Json.parse(
    s"""
       |{
       |  "gainsIncome": 100.25,
       |  "lossesBroughtForward": 200.25,
       |  "lossesArisingThisYear": 300.25,
       |  "gainsAfterLosses": 400.25,
       |  "attributedGains": 500.25,
       |  "netGains": 600.25,
       |  "annualExemptionAmount": 700.25,
       |  "taxableGains": 800.25,
       |  "cgtTaxBands": [
       |    ${cgtTaxBandJson(CgtTaxBandName.lowerRate)},
       |    ${cgtTaxBandJson(CgtTaxBandName.higherRate)}
       |  ],
       |  "totalTaxAmount": 900.25
       |}
     """.stripMargin
  )
}
