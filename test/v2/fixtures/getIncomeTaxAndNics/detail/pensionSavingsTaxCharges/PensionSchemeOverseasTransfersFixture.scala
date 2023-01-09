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

package v2.fixtures.getIncomeTaxAndNics.detail.pensionSavingsTaxCharges

import play.api.libs.json.{JsValue, Json}
import v2.models.response.getIncomeTaxAndNics.detail.pensionSavingsTaxCharges.PensionSchemeOverseasTransfers

object PensionSchemeOverseasTransfersFixture {

  val transferCharge: Option[BigDecimal]        = Some(2000.99)
  val transferChargeTaxPaid: Option[BigDecimal] = Some(3000.99)
  val rate: Option[BigDecimal]                  = Some(20.25)
  val chargeableAmount: Option[BigDecimal]      = Some(4000.99)

  val pensionSchemeOverseasTransfersModel: PensionSchemeOverseasTransfers =
    PensionSchemeOverseasTransfers(
      transferCharge = transferCharge,
      transferChargeTaxPaid = transferChargeTaxPaid,
      rate = rate,
      chargeableAmount = chargeableAmount
    )

  val pensionSchemeOverseasTransfersJson: JsValue = Json.parse(
    s"""
       |{
       |   "transferCharge": ${transferCharge.get},
       |   "transferChargeTaxPaid": ${transferChargeTaxPaid.get},
       |   "rate": ${rate.get},
       |   "chargeableAmount": ${chargeableAmount.get}
       |}
     """.stripMargin
  )

}
