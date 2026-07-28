/*
 * Copyright 2026 HM Revenue & Customs
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

package v9.retrieveCalculation.def4.model.response.calculation.otherIncome

import api.utils.UnitSpec
import play.api.libs.json.{JsError, JsObject, JsValue, Json}

class AdditionalIncomeBreakdownSpec extends UnitSpec {

  val minJson: JsValue = Json.parse("""
       |{
       |  "amountBeforeTax": 5000.99
       |}
       |""".stripMargin)

  val maxJson: JsValue = Json.parse("""
       |{
       |  "amountBeforeTax": 5000.99,
       |  "allowableExpenses": 5000.99,
       |  "lossesBroughtForward": 5000.99,
       |  "taxableIncome": 5000.99
       |}
       |""".stripMargin)

  val minModel: AdditionalIncomeBreakdown = AdditionalIncomeBreakdown(
    amountBeforeTax = BigDecimal(5000.99),
    allowableExpenses = None,
    lossesBroughtForward = None,
    taxableIncome = None
  )

  val maxModel: AdditionalIncomeBreakdown = AdditionalIncomeBreakdown(
    amountBeforeTax = BigDecimal(5000.99),
    allowableExpenses = Some(BigDecimal(5000.99)),
    lossesBroughtForward = Some(BigDecimal(5000.99)),
    taxableIncome = Some(BigDecimal(5000.99))
  )

  "reads" should {
    "successfully read in a model with only required fields" in {
      minJson.as[AdditionalIncomeBreakdown] shouldBe minModel
    }

    "successfully read in a model with all fields" in {
      maxJson.as[AdditionalIncomeBreakdown] shouldBe maxModel
    }
  }

  "writes" should {
    "successfully write a model with only required fields to json" in {
      Json.toJson(minModel) shouldBe minJson
    }

    "successfully write a model with all fields to json" in {
      Json.toJson(maxModel) shouldBe maxJson
    }
  }

  "error when JSON is invalid" in {
    JsObject.empty.validate[AdditionalIncomeBreakdown] shouldBe a[JsError]
  }

}
