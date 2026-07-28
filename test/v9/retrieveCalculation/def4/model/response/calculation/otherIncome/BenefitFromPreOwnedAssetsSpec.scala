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

class BenefitFromPreOwnedAssetsSpec extends UnitSpec {

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

  val benefitFromPreOwnedAssetsDetailModel: BenefitFromPreOwnedAssetsDetail = BenefitFromPreOwnedAssetsDetail(
    typeOfAsset = "Residential property",
    amountOfBenefit = BigDecimal(5000.99)
  )

  val benefitFromPreOwnedAssetsMinModel: BenefitFromPreOwnedAssets = BenefitFromPreOwnedAssets(
    totalBenefitFromPreOwnedAssets = None,
    benefitFromPreOwnedAssetsDetail = None
  )

  val benefitFromPreOwnedAssetsMaxModel: BenefitFromPreOwnedAssets = BenefitFromPreOwnedAssets(
    totalBenefitFromPreOwnedAssets = Some(BigDecimal(5000.99)),
    benefitFromPreOwnedAssetsDetail = Some(benefitFromPreOwnedAssetsDetailModel)
  )

  "reads" should {
    "successfully read in a model with all fields as None" in {
      JsObject.empty.as[BenefitFromPreOwnedAssets] shouldBe benefitFromPreOwnedAssetsMinModel
    }

    "successfully read in a model with all fields" in {
      benefitFromPreOwnedAssetsJson.as[BenefitFromPreOwnedAssets] shouldBe benefitFromPreOwnedAssetsMaxModel
    }
  }

  "writes" should {
    "successfully write a model with all fields as None to json" in {
      Json.toJson(benefitFromPreOwnedAssetsMinModel) shouldBe JsObject.empty
    }

    "successfully write a model with all fields to json" in {
      Json.toJson(benefitFromPreOwnedAssetsMaxModel) shouldBe benefitFromPreOwnedAssetsJson
    }
  }

}
