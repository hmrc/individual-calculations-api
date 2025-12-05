/*
 * Copyright 2025 HM Revenue & Customs
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

package v8.retrieveCalculation.def1.model.response.calculation.foreignPropertyIncome

import play.api.libs.json.{JsError, JsObject, JsValue, Json}
import shared.utils.UnitSpec
import v8.common.model.response.IncomeSourceType
import v8.common.model.response.IncomeSourceType.`foreign-property`

class ForeignPropertyIncomeSpec extends UnitSpec {

  val downstreamJson: JsValue = Json.parse(s"""
       |{
       |    "incomeSourceId": "000000000000210",
       |    "incomeSourceType": "15",
       |    "countryCode": "FRA",
       |    "totalIncome": 5000.99,
       |    "totalExpenses": 5000.99,
       |    "netProfit": 5000.99,
       |    "netLoss": 5000.99,
       |    "totalAdditions": 5000.99,
       |    "totalDeductions": 5000.99,
       |    "taxableProfit": 5000.99,
       |    "adjustedIncomeTaxLoss": 5000.99
       |}
       |""".stripMargin)

  val model: ForeignPropertyIncome =
    ForeignPropertyIncome(
      incomeSourceId = "000000000000210",
      incomeSourceType = `foreign-property`,
      countryCode = "FRA",
      totalIncome = Some(5000.99),
      totalExpenses = Some(5000.99),
      netProfit = Some(5000.99),
      netLoss = Some(5000.99),
      totalAdditions = Some(5000.99),
      totalDeductions = Some(5000.99),
      taxableProfit = Some(5000.99),
      adjustedIncomeTaxLoss = Some(5000.99)
    )

  val mtdJson: JsValue = Json.parse(s"""
       |{
       |    "incomeSourceId": "000000000000210",
       |    "incomeSourceType": "foreign-property",
       |    "countryCode": "FRA",
       |    "totalIncome": 5000.99,
       |    "totalExpenses": 5000.99,
       |    "netProfit": 5000.99,
       |    "netLoss": 5000.99,
       |    "totalAdditions": 5000.99,
       |    "totalDeductions": 5000.99,
       |    "taxableProfit": 5000.99,
       |    "adjustedIncomeTaxLoss": 5000.99
       |}
       |""".stripMargin)

  "reads" should {
    "successfully read in a model" when {

      s"provided downstream income source type '15'" in {
        downstreamJson.as[ForeignPropertyIncome] shouldBe model
      }
    }
  }

  "writes" should {
    "successfully write a model to json" when {

      s"provided income source type 'foreign-property" in {
        Json.toJson(model) shouldBe mtdJson
      }

    }
  }

  "error when JSON is invalid" in {
    JsObject.empty.validate[ForeignPropertyIncome] shouldBe a[JsError]
  }

}
