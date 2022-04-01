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

package v3.fixtures.getIncomeTaxAndNics.detail.incomeTax

import play.api.libs.json.{JsValue, Json}
import v3.models.response.getIncomeTaxAndNics.detail.incomeTax.GiftAid

object GiftAidFixture {

  val grossGiftAidPayments: BigInt = 100
  val rate: BigDecimal = 200.25
  val giftAidTax: BigDecimal = 300.25
  val giftAidTaxReductions: Option[BigDecimal] = Some(400.25)
  val incomeTaxChargedAfterGiftAidTaxReductions: Option[BigDecimal] = Some(500.25)
  val giftAidCharge: Option[BigDecimal] = Some(600.25)

  val giftAidModel: GiftAid =
    GiftAid(
      grossGiftAidPayments = grossGiftAidPayments,
      rate = rate,
      giftAidTax = giftAidTax,
      giftAidTaxReductions = giftAidTaxReductions,
      incomeTaxChargedAfterGiftAidTaxReductions = incomeTaxChargedAfterGiftAidTaxReductions,
      giftAidCharge = giftAidCharge
    )

  val giftAidJson: JsValue = Json.parse(
    s"""
       |{
       |   "grossGiftAidPayments": $grossGiftAidPayments,
       |   "rate": $rate,
       |   "giftAidTax": $giftAidTax,
       |   "giftAidTaxReductions": ${giftAidTaxReductions.get},
       |   "incomeTaxChargedAfterGiftAidTaxReductions": ${incomeTaxChargedAfterGiftAidTaxReductions.get},
       |   "giftAidCharge": ${giftAidCharge.get}
       |}
     """.stripMargin
  )
}