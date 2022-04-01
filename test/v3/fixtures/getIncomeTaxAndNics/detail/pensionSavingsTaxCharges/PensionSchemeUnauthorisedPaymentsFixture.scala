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

package v3.fixtures.getIncomeTaxAndNics.detail.pensionSavingsTaxCharges

import play.api.libs.json.{JsValue, Json}
import v3.fixtures.getIncomeTaxAndNics.detail.pensionSavingsTaxCharges.PensionSavingsDetailBreakdownFixture._
import v3.models.response.getIncomeTaxAndNics.detail.pensionSavingsTaxCharges.PensionSchemeUnauthorisedPayments

object PensionSchemeUnauthorisedPaymentsFixture {

  val totalChargeableAmount: Option[BigDecimal] = Some(2000.99)
  val totalTaxPaid: Option[BigDecimal] = Some(2500.99)

  val pensionSchemeUnauthorisedPaymentsModel: PensionSchemeUnauthorisedPayments =
    PensionSchemeUnauthorisedPayments(
      totalChargeableAmount = totalChargeableAmount,
      totalTaxPaid = totalTaxPaid,
      pensionSchemeUnauthorisedPaymentsSurcharge = Some(pensionSavingsDetailBreakdownModel),
      pensionSchemeUnauthorisedPaymentsNonSurcharge = Some(pensionSavingsDetailBreakdownModel)
    )

  val pensionSchemeUnauthorisedPaymentsJson: JsValue = Json.parse(
    s"""
       |{
       |   "totalChargeableAmount": ${totalChargeableAmount.get},
       |   "totalTaxPaid": ${totalTaxPaid.get},
       |   "pensionSchemeUnauthorisedPaymentsSurcharge": $pensionSavingsDetailBreakdownJson,
       |   "pensionSchemeUnauthorisedPaymentsNonSurcharge": $pensionSavingsDetailBreakdownJson
       |}
     """.stripMargin
  )
}