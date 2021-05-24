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

package v1.fixtures.getIncomeTaxAndNics.detail

import play.api.libs.json.{JsValue, Json}
import v1.models.response.getIncomeTaxAndNics.detail.ShortServiceRefundBands

object ShortServiceRefundBandsFixture {

  val name: String = "lowerBand"
  val rate: BigDecimal = 40.25
  val bandLimit: BigInt = 1000
  val apportionedBandLimit: BigInt = 2000
  val shortServiceRefundAmount: BigDecimal = 200.23
  val shortServiceRefundCharge: BigDecimal = 300.25

  val shortServiceRefundBandsModel: ShortServiceRefundBands =
    ShortServiceRefundBands(
      name = name,
      rate = rate,
      bandLimit = bandLimit,
      apportionedBandLimit = apportionedBandLimit,
      shortServiceRefundAmount = shortServiceRefundAmount,
      shortServiceRefundCharge = shortServiceRefundCharge
    )

  val shortServiceRefundBandsJson: JsValue = Json.parse(
    s"""
       |{
       |   "name": "$name",
       |   "rate": $rate,
       |   "bandLimit": $bandLimit,
       |   "apportionedBandLimit": $apportionedBandLimit,
       |   "shortServiceRefundAmount": $shortServiceRefundAmount,
       |   "shortServiceRefundCharge": $shortServiceRefundCharge
       |}
     """.stripMargin
  )
}