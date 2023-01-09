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

package v2.fixtures.getIncomeTaxAndNics.detail.capitalGainsTax

import play.api.libs.json.{JsValue, Json}
import v2.models.response.getIncomeTaxAndNics.detail.capitalGainsTax.{CgtTaxBand, CgtTaxBandName}

object CgtTaxBandFixture {

  def cgtTaxBandModel(name: CgtTaxBandName): CgtTaxBand =
    CgtTaxBand(
      name = name,
      rate = 10.25,
      income = 200.25,
      taxAmount = 300.25
    )

  def cgtTaxBandJson(name: CgtTaxBandName): JsValue = Json.parse(
    s"""
      |{
      |  "name": "$name",
      |  "rate": 10.25,
      |  "income": 200.25,
      |  "taxAmount": 300.25
      |}
     """.stripMargin
  )

}
