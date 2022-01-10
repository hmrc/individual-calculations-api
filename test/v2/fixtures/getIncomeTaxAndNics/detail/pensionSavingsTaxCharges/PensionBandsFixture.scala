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

package v2.fixtures.getIncomeTaxAndNics.detail.pensionSavingsTaxCharges

import play.api.libs.json.{JsValue, Json}
import v2.models.response.getIncomeTaxAndNics.detail.pensionSavingsTaxCharges.PensionBands

object PensionBandsFixture {

  val name: String = "BRT"
  val rate: BigDecimal = 30.25
  val bandLimit: BigInt = 2000
  val apportionedBandLimit: BigInt = 3000
  val contributionAmount: BigDecimal = 400.23
  val pensionCharge: BigDecimal = 500.25

  val pensionBandsModel: PensionBands =
    PensionBands(
      name = name,
      rate = rate,
      bandLimit = bandLimit,
      apportionedBandLimit = apportionedBandLimit,
      contributionAmount = contributionAmount,
      pensionCharge = pensionCharge
    )

  val pensionBandsJson: JsValue = Json.parse(
    s"""
       |{
       |   "name": "$name",
       |   "rate": $rate,
       |   "bandLimit": $bandLimit,
       |   "apportionedBandLimit": $apportionedBandLimit,
       |   "contributionAmount": $contributionAmount,
       |   "pensionCharge": $pensionCharge
       |}
     """.stripMargin
  )
}