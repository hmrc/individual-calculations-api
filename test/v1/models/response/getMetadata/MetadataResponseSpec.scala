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

package v1.models.response.getMetadata

import play.api.libs.json.Json
import support.UnitSpec
import v1.fixtures.getMetadata.MetadataResponseFixture
import v1.fixtures.getMetadata.MetadataResponseFixture._
import v1.hateoas.HateoasFactory
import v1.mocks.MockAppConfig
import v1.models.hateoas.Method.GET
import v1.models.hateoas.{HateoasWrapper, Link}
import v1.models.utils.JsonErrorValidators

class MetadataResponseSpec extends UnitSpec with JsonErrorValidators {

  "MetadataResponse" when {
    "read from valid JSON" should {
      "produce the expected MetadataResponseObject" in {
        metadataResponseTopLevelJsonWithMessages.as[MetadataResponse] shouldBe metadataResponseModel
      }
    }

    "written to JSON" should {
      "produce the expected JsObject" in {
        Json.toJson[MetadataResponse](metadataResponseModel) shouldBe metadataResponseJson
      }
    }
  }

  "LinksFactory" when {
    class Test extends MockAppConfig {
      val hateoasFactory = new HateoasFactory(mockAppConfig)
      val nino = "someNino"
      val calcId = "someCalcId"
      MockedAppConfig.apiGatewayContext.returns("individuals/calculations").anyNumberOfTimes
    }

    "wrapping a MetadataResponse object" should {
      "expose the correct hateoas links when errors are not present" in new Test {
        hateoasFactory.wrap(metadataResponseModel, MetadataHateoasData(nino, calcId, None)) shouldBe
          HateoasWrapper(
            metadataResponseModel,
            Seq(
              Link("/individuals/calculations/someNino/self-assessment/someCalcId", GET, "self"),
              Link("/individuals/calculations/someNino/self-assessment/someCalcId/income-tax-nics-calculated", GET, "income-tax-and-nics-calculated"),
              Link("/individuals/calculations/someNino/self-assessment/someCalcId/taxable-income", GET, "taxable-income"),
              Link("/individuals/calculations/someNino/self-assessment/someCalcId/allowances-deductions-reliefs", GET, "allowances-deductions-reliefs"),
              Link("/individuals/calculations/someNino/self-assessment/someCalcId/end-of-year-estimate", GET, "end-of-year-estimate"),
              Link("/individuals/calculations/someNino/self-assessment/someCalcId/messages", GET, "messages")
            )
          )
      }

      "expose the correct hateoas links when errors are present" in new Test {
        hateoasFactory.wrap(metadataResponseModel, MetadataHateoasData(nino, calcId, Some(1))) shouldBe
          HateoasWrapper(
            metadataResponseModel,
            Seq(
              Link("/individuals/calculations/someNino/self-assessment/someCalcId", GET, "self"),
              Link("/individuals/calculations/someNino/self-assessment/someCalcId/messages", GET, "messages")
            )
          )
      }
    }
  }
}