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

package v1.fixtures.getAllowancesAndDeductions.detail

import play.api.libs.json.{JsObject, JsValue, Json}
import v1.fixtures.getTaxableIncome.TaxableIncomeFixtures.metadataJson
import v1.models.response.getAllowancesDeductionsAndReliefs.AllowancesDeductionsAndReliefsResponse
import v1.models.response.getAllowancesDeductionsAndReliefs.detail.{AllowancesAndDeductions, CalculationDetail, Reliefs, ResidentialFinanceCosts}
import v1.models.response.getAllowancesDeductionsAndReliefs.summary.CalculationSummary

object ResidentialFinancialCostsFixture {

  val json: JsObject = Json.parse("""
      |{
      |  "amountClaimed": 12500,
      |      "allowableAmount": 12500,
      |      "rate": 20,
      |      "propertyFinanceRelief": 12500
      |}
  """.stripMargin).as[JsObject]
}
