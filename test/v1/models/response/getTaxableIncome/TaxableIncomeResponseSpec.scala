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

import support.UnitSpec
import v1.mocks.MockAppConfig
import v1.models.hateoas.Link
import v1.models.hateoas.Method.GET

class TaxableIncomeResponseSpec extends UnitSpec with MockAppConfig {

  val nino: String = "AA123456A"
  val calculationId: String = "calcId"

  "Links Factory" should {

    "expose the correct links for retrieve" in {
      MockedAppConfig.apiGatewayContext.returns("individuals/calculations").anyNumberOfTimes
      TaxableIncomeResponse.LinksFactory.links(mockAppConfig, TaxableIncomeHateoasData(nino, calculationId)) shouldBe
        Seq(
          Link(s"/individuals/calculations/$nino/self-assessment/$calculationId", GET, "metadata"),
          Link(s"/individuals/calculations/$nino/self-assessment/$calculationId/taxable-income", GET, "self")
        )
    }
  }
}