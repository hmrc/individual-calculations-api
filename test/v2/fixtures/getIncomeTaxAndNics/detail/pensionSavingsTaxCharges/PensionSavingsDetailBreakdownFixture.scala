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
import v2.models.response.getIncomeTaxAndNics.detail.pensionSavingsTaxCharges.PensionSavingsDetailBreakdown

object PensionSavingsDetailBreakdownFixture {

  val amount: Option[BigDecimal] = Some(2000.99)
  val taxPaid: Option[BigDecimal] = Some(3000.99)
  val rate: Option[BigDecimal] = Some(20.25)
  val chargeableAmount: Option[BigDecimal] = Some(4000.99)

  val pensionSavingsDetailBreakdownModel: PensionSavingsDetailBreakdown =
    PensionSavingsDetailBreakdown(
      amount = amount,
      taxPaid = taxPaid,
      rate = rate,
      chargeableAmount = chargeableAmount
    )

  val pensionSavingsDetailBreakdownJson: JsValue = Json.parse(
    s"""
       |{
       |   "amount": ${amount.get},
       |   "taxPaid": ${taxPaid.get},
       |   "rate": ${rate.get},
       |   "chargeableAmount": ${chargeableAmount.get}
       |}
     """.stripMargin
  )
}