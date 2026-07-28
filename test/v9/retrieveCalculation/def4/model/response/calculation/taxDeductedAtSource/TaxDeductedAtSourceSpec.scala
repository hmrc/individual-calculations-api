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

package v9.retrieveCalculation.def4.model.response.calculation.taxDeductedAtSource

import api.utils.UnitSpec
import play.api.libs.json.*

class TaxDeductedAtSourceSpec extends UnitSpec {

  def json: JsValue = Json.parse(s"""
       |{
       |  "bbsi": 5000.99,
       |  "ukLandAndProperty": 5000.99,
       |  "cis": 5000.99,
       |  "securities": 5000.99,
       |  "voidedIsa": 5000.99,
       |  "payeEmployments": 5000.99,
       |  "occupationalPensions": 5000.99,
       |  "stateBenefits": -99999999999.99,
       |  "specialWithholdingTaxOrUkTaxPaid": 5000.99,
       |  "inYearAdjustmentCodedInLaterTaxYear": 5000.99,
       |  "taxTakenOffTradingIncome": 5000.99,
       |  "taxTakenOffPartnerIncome": 5000.99,
       |  "taxTakenOffOtherIncome": 5000.99
       |}
       |""".stripMargin)

  def minModel: TaxDeductedAtSource = TaxDeductedAtSource(
    bbsi = None,
    ukLandAndProperty = None,
    cis = None,
    securities = None,
    voidedIsa = None,
    payeEmployments = None,
    occupationalPensions = None,
    stateBenefits = None,
    specialWithholdingTaxOrUkTaxPaid = None,
    inYearAdjustmentCodedInLaterTaxYear = None,
    taxTakenOffTradingIncome = None,
    taxTakenOffPartnerIncome = None,
    taxTakenOffOtherIncome = None
  )

  def maxModel: TaxDeductedAtSource = TaxDeductedAtSource(
    bbsi = Some(BigDecimal(5000.99)),
    ukLandAndProperty = Some(BigDecimal(5000.99)),
    cis = Some(BigDecimal(5000.99)),
    securities = Some(BigDecimal(5000.99)),
    voidedIsa = Some(BigDecimal(5000.99)),
    payeEmployments = Some(BigDecimal(5000.99)),
    occupationalPensions = Some(BigDecimal(5000.99)),
    stateBenefits = Some(BigDecimal(-99999999999.99)),
    specialWithholdingTaxOrUkTaxPaid = Some(BigDecimal(5000.99)),
    inYearAdjustmentCodedInLaterTaxYear = Some(BigDecimal(5000.99)),
    taxTakenOffTradingIncome = Some(BigDecimal(5000.99)),
    taxTakenOffPartnerIncome = Some(BigDecimal(5000.99)),
    taxTakenOffOtherIncome = Some(BigDecimal(5000.99))
  )

  "reads" should {
    "successfully read in a model with all fields as None" in {
      JsObject.empty.as[TaxDeductedAtSource] shouldBe minModel
    }

    "successfully read in a model with all fields" in {
      json.as[TaxDeductedAtSource] shouldBe maxModel
    }
  }

  "writes" should {
    "successfully write a model with all fields as None to json" in {
      Json.toJson(minModel) shouldBe JsObject.empty
    }

    "successfully write a model with all fields to json" in {
      Json.toJson(maxModel) shouldBe json
    }
  }

}
