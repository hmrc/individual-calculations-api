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

package v5.models.response.retrieveCalculation.calculation.employmentAndPensionsIncome

import api.models.utils.JsonErrorValidators
import play.api.libs.json.{JsValue, Json}
import support.UnitSpec

class BenefitsInKindDetailSpec extends UnitSpec with JsonErrorValidators {

  val model: BenefitsInKindDetail = BenefitsInKindDetail(
    Some(5000.99),
    Some(5000.99),
    Some(5000.99),
    Some(5000.99),
    Some(5000.99),
    Some(5000.99),
    Some(5000.99),
    Some(5000.99),
    Some(5000.99),
    Some(5000.99),
    Some(5000.99),
    Some(5000.99),
    Some(5000.99),
    Some(5000.99),
    Some(5000.99),
    Some(5000.99),
    Some(5000.99),
    Some(5000.99),
    Some(5000.99),
    Some(5000.99),
    Some(5000.99),
    Some(5000.99),
    Some(5000.99),
    Some(5000.99),
    Some(5000.99),
    Some(5000.99),
    Some(5000.99),
    Some(5000.99)
  )

  val downstreamJson: JsValue = Json.parse(
    """
      |{
      |  "apportionedAccommodation": 5000.99,
      |  "apportionedAssets": 5000.99,
      |  "apportionedAssetTransfer": 5000.99,
      |  "apportionedBeneficialLoan": 5000.99,
      |  "apportionedCar": 5000.99,
      |  "apportionedCarFuel": 5000.99,
      |  "apportionedEducationalServices": 5000.99,
      |  "apportionedEntertaining": 5000.99,
      |  "apportionedExpenses": 5000.99,
      |  "apportionedMedicalInsurance": 5000.99,
      |  "apportionedTelephone": 5000.99,
      |  "apportionedService": 5000.99,
      |  "apportionedTaxableExpenses": 5000.99,
      |  "apportionedVan": 5000.99,
      |  "apportionedVanFuel": 5000.99,
      |  "apportionedMileage": 5000.99,
      |  "apportionedNonQualifyingRelocationExpenses": 5000.99,
      |  "apportionedNurseryPlaces": 5000.99,
      |  "apportionedOtherItems": 5000.99,
      |  "apportionedPaymentsOnEmployeesBehalf": 5000.99,
      |  "apportionedPersonalIncidentalExpenses": 5000.99,
      |  "apportionedQualifyingRelocationExpenses": 5000.99,
      |  "apportionedEmployerProvidedProfessionalSubscriptions": 5000.99,
      |  "apportionedEmployerProvidedServices": 5000.99,
      |  "apportionedIncomeTaxPaidByDirector": 5000.99,
      |  "apportionedTravelAndSubsistence": 5000.99,
      |  "apportionedVouchersAndCreditCards": 5000.99,
      |  "apportionedNonCash": 5000.99
      |}
      |""".stripMargin
  )

  val mtdJson: JsValue = Json.parse(
    """
      |{
      |  "apportionedAccommodation": 5000.99,
      |  "apportionedAssets": 5000.99,
      |  "apportionedAssetTransfer": 5000.99,
      |  "apportionedBeneficialLoan": 5000.99,
      |  "apportionedCar": 5000.99,
      |  "apportionedCarFuel": 5000.99,
      |  "apportionedEducationalServices": 5000.99,
      |  "apportionedEntertaining": 5000.99,
      |  "apportionedExpenses": 5000.99,
      |  "apportionedMedicalInsurance": 5000.99,
      |  "apportionedTelephone": 5000.99,
      |  "apportionedService": 5000.99,
      |  "apportionedTaxableExpenses": 5000.99,
      |  "apportionedVan": 5000.99,
      |  "apportionedVanFuel": 5000.99,
      |  "apportionedMileage": 5000.99,
      |  "apportionedNonQualifyingRelocationExpenses": 5000.99,
      |  "apportionedNurseryPlaces": 5000.99,
      |  "apportionedOtherItems": 5000.99,
      |  "apportionedPaymentsOnEmployeesBehalf": 5000.99,
      |  "apportionedPersonalIncidentalExpenses": 5000.99,
      |  "apportionedQualifyingRelocationExpenses": 5000.99,
      |  "apportionedEmployerProvidedProfessionalSubscriptions": 5000.99,
      |  "apportionedEmployerProvidedServices": 5000.99,
      |  "apportionedIncomeTaxPaidByDirector": 5000.99,
      |  "apportionedTravelAndSubsistence": 5000.99,
      |  "apportionedVouchersAndCreditCards": 5000.99,
      |  "apportionedNonCash": 5000.99
      |}
      |""".stripMargin
  )

  "reads" when {
    "passed valid JSON" should {
      "return a valid model" in {
        downstreamJson.as[BenefitsInKindDetail] shouldBe model
      }
    }
  }

  "writes" when {
    "passed valid model" should {
      "return valid JSON" in {
        Json.toJson(model) shouldBe mtdJson
      }
    }
  }

}
