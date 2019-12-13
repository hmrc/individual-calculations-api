/*
 * Copyright 2019 HM Revenue & Customs
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
import v1.models.response.getIncomeTaxAndNics.detail.TaxBand

object TaxBandFixture {

  val name: String = "name"
  val rate: BigDecimal = 100.25
  val bandLimit: BigDecimal = 400.25
  val apportionedBandLimit: BigDecimal= 500.25
  val income: BigDecimal = 600.25
  val taxAmount: BigDecimal = 700.25

  val taxBandModel: TaxBand =
    TaxBand(
      name = name,
      rate = rate,
      bandLimit = bandLimit,
      apportionedBandLimit = apportionedBandLimit,
      income = income,
      taxAmount = taxAmount
    )

  val taxBandJson: JsValue = Json.parse(
    s"""
      |{
      |   "name": "$name",
      |   "rate": $rate,
      |   "bandLimit" : $bandLimit,
      |   "apportionedBandLimit" : $apportionedBandLimit,
      |   "income" : $income,
      |   "taxAmount" : $taxAmount
      |}
    """.stripMargin
  )

  def taxBandModel(input: BigDecimal): TaxBand = {
    TaxBand(
      name = name,
      rate = input + 0.17,
      bandLimit = input + 0.34,
      apportionedBandLimit = input + 0.51,
      income = input + 0.68,
      taxAmount = input + 0.85
    )
  }

  def taxBandJson(input: BigDecimal): JsValue = Json.parse(
    s"""
      |{
      |   "name": "$name",
      |   "rate": ${input + 0.17},
      |   "bandLimit" : ${input + 0.34},
      |   "apportionedBandLimit" : ${input + 0.51},
      |   "income" : ${input + 0.68},
      |   "taxAmount" : ${input + 0.85}
      |}
    """.stripMargin
  )
}