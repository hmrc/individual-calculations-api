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

package v4.fixtures.getIncomeTaxAndNics.detail.pensionSavingsTaxCharges

import play.api.libs.json.{JsValue, Json}
import v4.fixtures.getIncomeTaxAndNics.detail.pensionSavingsTaxCharges.PensionSavingsDetailBreakdownFixture._
import v4.models.response.getIncomeTaxAndNics.detail.pensionSavingsTaxCharges.ExcessOfLifetimeAllowance

object ExcessOfLifetimeAllowanceFixture {

  val totalChargeableAmount: Option[BigDecimal] = Some(1000.99)
  val totalTaxPaid: Option[BigDecimal]          = Some(1500.99)

  val excessOfLifetimeAllowanceModel: ExcessOfLifetimeAllowance =
    ExcessOfLifetimeAllowance(
      totalChargeableAmount = totalChargeableAmount,
      totalTaxPaid = totalTaxPaid,
      lumpSumBenefitTakenInExcessOfLifetimeAllowance = Some(pensionSavingsDetailBreakdownModel),
      benefitInExcessOfLifetimeAllowance = Some(pensionSavingsDetailBreakdownModel)
    )

  val excessOfLifetimeAllowanceJson: JsValue = Json.parse(
    s"""
       |{
       |   "totalChargeableAmount": ${totalChargeableAmount.get},
       |   "totalTaxPaid": ${totalTaxPaid.get},
       |   "lumpSumBenefitTakenInExcessOfLifetimeAllowance": $pensionSavingsDetailBreakdownJson,
       |   "benefitInExcessOfLifetimeAllowance": $pensionSavingsDetailBreakdownJson
       |}
     """.stripMargin
  )

}
