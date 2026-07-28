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
import play.api.libs.json.*

class BenefitFromPreOwnedAssetsDetailSpec extends UnitSpec {

  val json: JsValue = Json.parse("""
      |{
      |  "typeOfAsset": "Residential property",
      |  "amountOfBenefit": 5000.99
      |}
      |""".stripMargin)

  val model: BenefitFromPreOwnedAssetsDetail = BenefitFromPreOwnedAssetsDetail(
    typeOfAsset = "Residential property",
    amountOfBenefit = BigDecimal(5000.99)
  )

  "reads" should {
    "successfully read in a model" in {
      json.as[BenefitFromPreOwnedAssetsDetail] shouldBe model
    }
  }

  "writes" should {
    "successfully write a model to json" in {
      Json.toJson(model) shouldBe json
    }
  }

  "error when JSON is invalid" in {
    JsObject.empty.validate[BenefitFromPreOwnedAssetsDetail] shouldBe a[JsError]
  }

}
