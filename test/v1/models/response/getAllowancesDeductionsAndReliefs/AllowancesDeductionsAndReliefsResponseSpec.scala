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

package v1.models.response.getAllowancesDeductionsAndReliefs

import play.api.libs.json.Json
import support.UnitSpec
import v1.mocks.MockAppConfig
import v1.models.hateoas.Link
import v1.models.hateoas.Method.GET
import v1.models.response.getAllowancesDeductionsAndReliefs.detail.CalculationDetail
import v1.models.response.getAllowancesDeductionsAndReliefs.summary.CalculationSummary
import v1.models.utils.JsonErrorValidators
import v1.fixtures.getAllowancesAndDeductions.AllowancesDeductionsAndReliefsFixture._

class AllowancesDeductionsAndReliefsResponseSpec extends UnitSpec with MockAppConfig with JsonErrorValidators {

  val nino: String = "AA123456A"
  val calculationId: String = "calcId"

  testJsonProperties[AllowancesDeductionsAndReliefsResponse](json)(
    mandatoryProperties = Seq("allowancesDeductionsAndReliefsResponse"),
    optionalProperties = Seq()
  )

  "writing to Json" must {
    "work as per example in tech spec" in {
      Json.toJson(allowancesDeductionsAndReliefsModel) shouldBe json
    }
  }

  "isEmpty" should {
    def responseWithSummary(summary: CalculationSummary): AllowancesDeductionsAndReliefsResponse =
      AllowancesDeductionsAndReliefsResponse(summary = summary, detail = CalculationDetail(None, None))

    "return true" when {
      "summary totalAllowancesAndDeductions AND totalReliefs are not present" in {
        responseWithSummary(CalculationSummary(None, None)).isEmpty shouldBe true
      }

      "summary totalAllowancesAndDeductions AND totalReliefs are present and both less than 1" in {
        responseWithSummary(CalculationSummary(Some(0), Some(0))).isEmpty shouldBe true
      }

      "either of summary totalAllowancesAndDeductions OR totalReliefs is present but less than 1" in {
        responseWithSummary(CalculationSummary(Some(0), None)).isEmpty shouldBe true
        responseWithSummary(CalculationSummary(None, Some(0))).isEmpty shouldBe true
      }
    }

    "return false" when {
      "either of summary totalAllowancesAndDeductions AND totalReliefs is present and at least 1" in {
        responseWithSummary(CalculationSummary(Some(1), None)).isEmpty shouldBe false
        responseWithSummary(CalculationSummary(None, Some(1))).isEmpty shouldBe false
      }

      "summary totalAllowancesAndDeductions AND totalReliefs are present both are at least one" in {
        responseWithSummary(CalculationSummary(Some(1), Some(1))).isEmpty shouldBe false
      }
    }
  }

  "Links Factory" should {

    "expose the correct links for retrieve" in {
      MockedAppConfig.apiGatewayContext.returns("individuals/calculations").anyNumberOfTimes
      AllowancesDeductionsAndReliefsResponse.LinksFactory.links(mockAppConfig, AllowancesHateoasData(nino, calculationId)) shouldBe
        Seq(
          Link(s"/individuals/calculations/$nino/self-assessment/$calculationId", GET, "metadata"),
          Link(s"/individuals/calculations/$nino/self-assessment/$calculationId/allowances-deductions-reliefs", GET, "self")
        )
    }
  }
}
