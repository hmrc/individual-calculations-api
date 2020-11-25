/*
 * Copyright 2020 HM Revenue & Customs
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

package v1.models.response.getAllowancesDeductionsAndReliefs

import play.api.libs.json.{JsObject, Json}
import support.UnitSpec
import v1.fixtures.getAllowancesDeductionsAndReliefs.AllowancesDeductionsAndReliefsResponseFixture
import v1.hateoas.HateoasFactory
import v1.mocks.MockAppConfig
import v1.models.hateoas.Method.GET
import v1.models.hateoas.{HateoasWrapper, Link}
import v1.models.response.getAllowancesDeductionsAndReliefs.AllowancesDeductionsAndReliefsResponse.LinksFactory
import v1.models.utils.JsonErrorValidators

class AllowancesDeductionsAndReliefsResponseSpec extends UnitSpec with MockAppConfig with JsonErrorValidators {

  "AllowancesDeductionsAndReliefsResponse" when {

    "isEmpty" should {
      def responseWithSummary(summary: JsObject): JsObject =
        Json.obj("data" -> Json.obj("allowancesDeductionsAndReliefs" -> Json.obj("summary" -> summary)))


      "return true" when {
        "summary totalAllowancesAndDeductions AND totalReliefs are not present" in {
          val json = responseWithSummary(Json.obj())
          AllowancesDeductionsAndReliefsResponse.isEmpty(json) shouldBe true
        }

        "summary totalAllowancesAndDeductions AND totalReliefs are present and both less than 1" in {
          val json = responseWithSummary(Json.obj("totalAllowancesAndDeductions" -> 0, "totalReliefs" -> 0))
          AllowancesDeductionsAndReliefsResponse.isEmpty(json) shouldBe true
        }

        "either of summary totalAllowancesAndDeductions OR totalReliefs is present but less than 1" in {
          val json1 = responseWithSummary(Json.obj("totalAllowancesAndDeductions" -> 0))
          val json2 = responseWithSummary(Json.obj("totalReliefs" -> 0))
          AllowancesDeductionsAndReliefsResponse.isEmpty(json1) shouldBe true
          AllowancesDeductionsAndReliefsResponse.isEmpty(json2) shouldBe true
        }
      }

      "return false" when {
        "either of summary totalAllowancesAndDeductions AND totalReliefs is present and at least 1" in {
          val json1 = responseWithSummary(Json.obj("totalAllowancesAndDeductions" -> 1))
          val json2 = responseWithSummary(Json.obj("totalReliefs" -> 1))
          AllowancesDeductionsAndReliefsResponse.isEmpty(json1) shouldBe false
          AllowancesDeductionsAndReliefsResponse.isEmpty(json2) shouldBe false
        }

        "summary totalAllowancesAndDeductions AND totalReliefs are present both are at least one" in {
          val json = responseWithSummary(Json.obj("totalAllowancesAndDeductions" -> 1, "totalReliefs" -> 1))
          AllowancesDeductionsAndReliefsResponse.isEmpty(json) shouldBe false
        }
      }
    }
  }

  "LinksFactory" when {
    class Test extends MockAppConfig {
      val hateoasFactory = new HateoasFactory(mockAppConfig)
      val nino = "someNino"
      val calcId: String = "calcId"
      MockedAppConfig.apiGatewayContext.returns("individuals/calculations").anyNumberOfTimes
    }

    "wrapping a AllowancesDeductionsAndReliefsResponse object" should {
      "expose the correct hateoas links" in new Test {
        hateoasFactory.wrap(AllowancesDeductionsAndReliefsResponseFixture.allowancesDeductionsAndReliefsResponseJsonNonEmpty, AllowancesDeductionsAndReliefsHateoasData(nino, calcId)) shouldBe
          HateoasWrapper(
            AllowancesDeductionsAndReliefsResponseFixture.allowancesDeductionsAndReliefsResponseJsonNonEmpty,
            Seq(
              Link("/individuals/calculations/someNino/self-assessment/calcId", GET, "metadata"),
              Link("/individuals/calculations/someNino/self-assessment/calcId/allowances-deductions-reliefs", GET, "self")
            )
          )
      }
    }
  }
}
