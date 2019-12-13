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

package v1.models.response.getTaxableIncome

import play.api.libs.json.Json
import support.UnitSpec
import v1.hateoas.HateoasFactory
import v1.mocks.MockAppConfig
import v1.models.hateoas.{HateoasWrapper, Link}
import v1.fixtures.getTaxableIncome.TaxableIncomeFixtures._
import v1.models.hateoas.Method.GET

class TaxableIncomeResponseSpec extends UnitSpec with MockAppConfig {

  "TaxableIncomeResponse" when {
    "read from valid JSON" should {
      "produce the expected TaxableIncomeResponse Object" in {
        taxableIncomeResponseTopLevelJson.as[TaxableIncomeResponse] shouldBe taxableIncomeResponseModel
      }
    }

    "written to JSON" should {
      "produce the expected JsObject" in {
        Json.toJson(taxableIncomeResponseModel) shouldBe taxableIncomeResponseJson
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

    "wrapping a TaxableIncomeResponse object" should {
      "expose the correct hateoas links" in new Test {
        hateoasFactory.wrap(taxableIncomeResponseModel, TaxableIncomeHateoasData(nino, calcId)) shouldBe
        HateoasWrapper(
          taxableIncomeResponseModel,
          Seq(
            Link(s"/individuals/calculations/$nino/self-assessment/$calcId", GET, "metadata"),
            Link(s"/individuals/calculations/$nino/self-assessment/$calcId/taxable-income", GET, "self")
          )
        )
      }
    }
  }
}