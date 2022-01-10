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

package v2.fixtures.getIncomeTaxAndNics.detail.incomeTax

import play.api.libs.json.{JsValue, Json}
import v2.fixtures.getIncomeTaxAndNics.detail.incomeTax.TaxBandFixture._
import v2.models.response.getIncomeTaxAndNics.detail.incomeTax.IncomeTypeBreakdown

object IncomeTypeBreakdownFixture {

  val allowancesAllocated: BigInt = 1000
  val incomeTaxAmount: BigDecimal = 100.50

  val incomeTypeBreakdownModel: IncomeTypeBreakdown =
    IncomeTypeBreakdown(
      allowancesAllocated = allowancesAllocated,
      incomeTaxAmount = incomeTaxAmount,
      taxBands = Some(Seq(taxBandModel))
    )

  val incomeTypeBreakdownJson: JsValue = Json.parse(
    s"""
       |{
       |   "allowancesAllocated": $allowancesAllocated,
       |   "incomeTaxAmount": $incomeTaxAmount,
       |   "taxBands": [$taxBandJson]
       |}
     """.stripMargin
  )

  def incomeTypeBreakdownModel(input: BigDecimal): IncomeTypeBreakdown =
    IncomeTypeBreakdown(
      allowancesAllocated = allowancesAllocated,
      incomeTaxAmount = input + 0.5,
      taxBands = Some(Seq(taxBandModel(2 * input)))
    )

  def incomeTypeBreakdownJson(input: BigDecimal): JsValue = Json.parse(
    s"""
       |{
       |   "allowancesAllocated": $allowancesAllocated,
       |   "incomeTaxAmount": ${input + 0.5},
       |   "taxBands": [${taxBandJson(input * 2).toString()}]
       |}
     """.stripMargin
  )
}