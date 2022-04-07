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

package v2.fixtures.getIncomeTaxAndNics.detail.nics

import play.api.libs.json.{JsValue, Json}
import v2.models.response.getIncomeTaxAndNics.detail.nics.NicBand

object NicBandFixture {

  val name: String                         = "name"
  val rate: BigDecimal                     = 100.25
  val threshold: Option[BigInt]            = Some(200)
  val apportionedThreshold: Option[BigInt] = Some(300)
  val income: BigInt                       = 400
  val amount: BigDecimal                   = 500.25

  val nicBandModel: NicBand =
    NicBand(
      name = name,
      rate = rate,
      threshold = threshold,
      apportionedThreshold = apportionedThreshold,
      income = income,
      amount = amount
    )

  val nicBandJson: JsValue = Json.parse(
    s"""
       |{
       |   "name": "$name",
       |	 "rate": $rate,
       |	 "threshold": ${threshold.get},
       |	 "apportionedThreshold": ${apportionedThreshold.get},
       |	 "income": $income,
       |	 "amount": $amount
       |}
     """.stripMargin
  )

}
