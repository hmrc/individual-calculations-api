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

  val taxBandModel: TaxBand =
    TaxBand(
      name = "name",
      rate = 100.25,
      bandLimit = 400.25,
      apportionedBandLimit = 500.25,
      income = 600.25,
      taxAmount = 700.25
    )

  val taxBandJson: JsValue = Json.parse(
    """
      |{
      | "name": "name",
      | "rate": 100.25,
      | "bandLimit" : 400.25,
      | "apportionedBandLimit" : 500.25,
      | "income" : 600.25,
      | "taxAmount" : 700.25
      |}
    """.stripMargin
  )

  def taxBandModel(input: BigDecimal): TaxBand = {
    TaxBand(
      name = "name",
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
      | "name": "name",
      | "rate": ${input + 0.17},
      | "bandLimit" : ${input + 0.34},
      | "apportionedBandLimit" : ${input + 0.51},
      | "income" : ${input + 0.68},
      | "taxAmount" : ${input + 0.85}
      |}
    """.stripMargin
  )
}