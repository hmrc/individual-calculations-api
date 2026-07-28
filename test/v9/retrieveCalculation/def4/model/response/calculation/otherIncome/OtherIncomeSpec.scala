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

class OtherIncomeSpec extends UnitSpec {

  val postCessationReceiptJson: JsValue = Json.parse("""
      |{
      |  "amount": 5000.99,
      |  "taxYearIncomeToBeTaxed": "2026-27"
      |}
      |""".stripMargin)

  val postCessationIncomeJson: JsValue = Json.parse(s"""
       |{
       |  "totalPostCessationReceipts": 5000.99,
       |  "postCessationReceipts": [$postCessationReceiptJson]
       |}
       |""".stripMargin)

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

  val additionalIncomeJson: JsValue = Json.parse(s"""
       |{
       |  "totalAdditionalIncome": 5000.99,
       |  "additionalIncomeDetail": $additionalIncomeDetailJson
       |}
       |""".stripMargin)

  val benefitFromPreOwnedAssetsDetailJson: JsValue = Json.parse("""
      |{
      |  "typeOfAsset": "Residential property",
      |  "amountOfBenefit": 5000.99
      |}
      |""".stripMargin)

  val benefitFromPreOwnedAssetsJson: JsValue = Json.parse(s"""
       |{
       |  "totalBenefitFromPreOwnedAssets": 5000.99,
       |  "benefitFromPreOwnedAssetsDetail": $benefitFromPreOwnedAssetsDetailJson
       |}
       |""".stripMargin)

  val otherIncomeMinJson: JsValue = Json.parse("""
      |{
      |  "totalOtherIncome": 0
      |}
      |""".stripMargin)

  val otherIncomeMaxJson: JsValue = Json.parse(s"""
      |{
      |  "totalOtherIncome": 5000.99,
      |  "postCessationIncome": $postCessationIncomeJson,
      |  "additionalIncome": $additionalIncomeJson,
      |  "benefitFromPreOwnedAssets": $benefitFromPreOwnedAssetsJson
      |}
      |""".stripMargin)

  val postCessationReceiptModel: PostCessationReceipt = PostCessationReceipt(
    amount = BigDecimal(5000.99),
    taxYearIncomeToBeTaxed = "2026-27"
  )

  val postCessationIncomeModel: PostCessationIncome = PostCessationIncome(
    totalPostCessationReceipts = 5000.99,
    postCessationReceipts = Seq(postCessationReceiptModel)
  )

  val additionalIncomeBreakdownModel: AdditionalIncomeBreakdown = AdditionalIncomeBreakdown(
    amountBeforeTax = BigDecimal(5000.99),
    allowableExpenses = Some(BigDecimal(5000.99)),
    lossesBroughtForward = Some(BigDecimal(5000.99)),
    taxableIncome = Some(BigDecimal(5000.99))
  )

  val additionalIncomeDetailModel: AdditionalIncomeDetail = AdditionalIncomeDetail(
    propertyIncomeDistributions = Some(additionalIncomeBreakdownModel),
    personalInsuranceBenefits = Some(additionalIncomeBreakdownModel),
    incomeFromUnauthorisedUnitTrust = Some(additionalIncomeBreakdownModel),
    profitsFromCertificateOfDeposit = Some(additionalIncomeBreakdownModel),
    nonCashBenefitsFromFormerEmployer = Some(additionalIncomeBreakdownModel),
    authorisedPaymentsFromOverseasPensionScheme = Some(additionalIncomeBreakdownModel),
    taxableAnnualPayments = Some(additionalIncomeBreakdownModel),
    miscellaneousIncome = Some(additionalIncomeBreakdownModel)
  )

  val additionalIncomeModel: AdditionalIncome = AdditionalIncome(
    totalAdditionalIncome = Some(BigDecimal(5000.99)),
    additionalIncomeDetail = Some(additionalIncomeDetailModel)
  )

  val benefitFromPreOwnedAssetsDetailModel: BenefitFromPreOwnedAssetsDetail = BenefitFromPreOwnedAssetsDetail(
    typeOfAsset = "Residential property",
    amountOfBenefit = BigDecimal(5000.99)
  )

  val benefitFromPreOwnedAssetsModel: BenefitFromPreOwnedAssets = BenefitFromPreOwnedAssets(
    totalBenefitFromPreOwnedAssets = Some(BigDecimal(5000.99)),
    benefitFromPreOwnedAssetsDetail = Some(benefitFromPreOwnedAssetsDetailModel)
  )

  val otherIncomeMinModel: OtherIncome = OtherIncome(
    totalOtherIncome = BigDecimal(0),
    postCessationIncome = None,
    additionalIncome = None,
    benefitFromPreOwnedAssets = None
  )

  val otherIncomeMaxModel: OtherIncome = OtherIncome(
    totalOtherIncome = BigDecimal(5000.99),
    postCessationIncome = Some(postCessationIncomeModel),
    additionalIncome = Some(additionalIncomeModel),
    benefitFromPreOwnedAssets = Some(benefitFromPreOwnedAssetsModel)
  )

  "reads" should {
    "successfully read in a minimal model" in {
      otherIncomeMinJson.as[OtherIncome] shouldBe otherIncomeMinModel
    }

    "successfully read in a model with all fields" in {
      otherIncomeMaxJson.as[OtherIncome] shouldBe otherIncomeMaxModel
    }
  }

  "writes" should {
    "successfully write a model with only required fields to json" in {
      Json.toJson(otherIncomeMinModel) shouldBe otherIncomeMinJson
    }

    "successfully write a model with all fields to json" in {
      Json.toJson(otherIncomeMaxModel) shouldBe otherIncomeMaxJson
    }
  }

  "error when JSON is invalid" in {
    JsObject.empty.validate[OtherIncome] shouldBe a[JsError]
  }

}
