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
import play.api.libs.json.{JsObject, JsValue, Json}

class AdditionalIncomeDetailSpec extends UnitSpec {

  val additionalIncomeBreakdownJson: JsValue = Json.parse("""
      |{
      |  "amountBeforeTax": 5000.99,
      |  "allowableExpenses": 5000.99,
      |  "lossesBroughtForward": 5000.99,
      |  "taxableIncome": 5000.99
      |}
      |""".stripMargin)

  val additionalIncomeDetailJson: JsValue = Json.parse(s"""
      |{
      |  "propertyIncomeDistributions": $additionalIncomeBreakdownJson,
      |  "personalInsuranceBenefits": $additionalIncomeBreakdownJson,
      |  "incomeFromUnauthorisedUnitTrust": $additionalIncomeBreakdownJson,
      |  "profitsFromCertificateOfDeposit": $additionalIncomeBreakdownJson,
      |  "nonCashBenefitsFromFormerEmployer": $additionalIncomeBreakdownJson,
      |  "authorisedPaymentsFromOverseasPensionScheme": $additionalIncomeBreakdownJson,
      |  "taxableAnnualPayments": $additionalIncomeBreakdownJson,
      |  "miscellaneousIncome": $additionalIncomeBreakdownJson
      |}
      |""".stripMargin)

  val additionalIncomeBreakdownModel: AdditionalIncomeBreakdown = AdditionalIncomeBreakdown(
    amountBeforeTax = BigDecimal(5000.99),
    allowableExpenses = Some(BigDecimal(5000.99)),
    lossesBroughtForward = Some(BigDecimal(5000.99)),
    taxableIncome = Some(BigDecimal(5000.99))
  )

  val additionalIncomeDetailMinModel: AdditionalIncomeDetail = AdditionalIncomeDetail(
    propertyIncomeDistributions = None,
    personalInsuranceBenefits = None,
    incomeFromUnauthorisedUnitTrust = None,
    profitsFromCertificateOfDeposit = None,
    nonCashBenefitsFromFormerEmployer = None,
    authorisedPaymentsFromOverseasPensionScheme = None,
    taxableAnnualPayments = None,
    miscellaneousIncome = None
  )

  val additionalIncomeDetailMaxModel: AdditionalIncomeDetail = AdditionalIncomeDetail(
    propertyIncomeDistributions = Some(additionalIncomeBreakdownModel),
    personalInsuranceBenefits = Some(additionalIncomeBreakdownModel),
    incomeFromUnauthorisedUnitTrust = Some(additionalIncomeBreakdownModel),
    profitsFromCertificateOfDeposit = Some(additionalIncomeBreakdownModel),
    nonCashBenefitsFromFormerEmployer = Some(additionalIncomeBreakdownModel),
    authorisedPaymentsFromOverseasPensionScheme = Some(additionalIncomeBreakdownModel),
    taxableAnnualPayments = Some(additionalIncomeBreakdownModel),
    miscellaneousIncome = Some(additionalIncomeBreakdownModel)
  )

  "reads" should {
    "successfully read in an empty model" in {
      JsObject.empty.as[AdditionalIncomeDetail] shouldBe additionalIncomeDetailMinModel
    }

    "successfully read in a model with all fields" in {
      additionalIncomeDetailJson.as[AdditionalIncomeDetail] shouldBe additionalIncomeDetailMaxModel
    }
  }

  "writes" should {
    "successfully write a model with only required fields to json" in {
      Json.toJson(additionalIncomeDetailMinModel) shouldBe JsObject.empty
    }

    "successfully write a model with all fields to json" in {
      Json.toJson(additionalIncomeDetailMaxModel) shouldBe additionalIncomeDetailJson
    }
  }

}
