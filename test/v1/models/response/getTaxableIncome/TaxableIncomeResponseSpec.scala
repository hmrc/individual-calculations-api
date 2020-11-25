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

package v1.models.response.getTaxableIncome

import support.UnitSpec
import v1.fixtures.getTaxableIncome.TaxableIncomeResponseFixture
import v1.hateoas.HateoasFactory
import v1.mocks.MockAppConfig
import v1.models.hateoas.Method.GET
import v1.models.hateoas.{HateoasWrapper, Link}
import v1.models.response.getTaxableIncome.TaxableIncomeResponse.LinksFactory

class TaxableIncomeResponseSpec extends UnitSpec with MockAppConfig {

  "LinksFactory" when {
    class Test extends MockAppConfig {
      val hateoasFactory = new HateoasFactory(mockAppConfig)
      val nino = "someNino"
      val calcId = "someCalcId"
      MockedAppConfig.apiGatewayContext.returns("individuals/calculations").anyNumberOfTimes
    }

    "wrapping a TaxableIncomeResponse object" should {
      "expose the correct hateoas links" in new Test {
        hateoasFactory.wrap(TaxableIncomeResponseFixture.taxableIncomeResponseJson, TaxableIncomeHateoasData(nino, calcId)) shouldBe
        HateoasWrapper(
          TaxableIncomeResponseFixture.taxableIncomeResponseJson,
          Seq(
            Link(s"/individuals/calculations/$nino/self-assessment/$calcId", GET, "metadata"),
            Link(s"/individuals/calculations/$nino/self-assessment/$calcId/taxable-income", GET, "self")
          )
        )
      }
    }
  }
}
