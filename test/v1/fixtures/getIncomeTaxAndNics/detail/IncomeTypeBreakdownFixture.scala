/*
 * Copyright 2020 HM Revenue & Customs
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
import v1.fixtures.getIncomeTaxAndNics.detail.TaxBandFixture._

object IncomeTypeBreakdownFixture {

  val allowancesAllocated: BigInt = 1000
  val incomeTaxAmount: BigDecimal = 100.50

  val incomeTypeBreakdownJson: JsValue = Json.parse(
    s"""
       |{
       |   "allowancesAllocated" : $allowancesAllocated,
       |   "incomeTaxAmount" : $incomeTaxAmount,
       |   "taxBands" : [$taxBandJson]
       |}
    """.stripMargin
  )

  def incomeTypeBreakdownJson(input: BigDecimal): JsValue = Json.parse(
    s"""
       |{
       |   "allowancesAllocated" : $allowancesAllocated,
       |   "incomeTaxAmount" : ${input + 0.5},
       |   "taxBands" : [${taxBandJson(input * 2).toString()}]
       |}
    """.stripMargin
  )
}
