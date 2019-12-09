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

package v1.models.response.listCalculations

import play.api.libs.json.Json
import support.UnitSpec
import v1.fixtures.listCalculations.ListCalculationsFixture._
import v1.hateoas.HateoasFactory
import v1.mocks.MockAppConfig
import v1.models.hateoas.Method.{GET, POST}
import v1.models.hateoas.{HateoasWrapper, Link}
import v1.models.response.common.CalculationType

class ListCalculationsResponseSpec extends UnitSpec {

  "ListCalculationsResponse" when {
    "read from valid JSON" should {
      "produce the expected ListCalculationsResponse object" in {
        listCalculationsJson.as[ListCalculationsResponse[CalculationListItem]] shouldBe listCalculationsResponse
      }
    }

    "written to JSON" should {
      "produce the expected JsObject" in {
        Json.toJson(listCalculationsResponse) shouldBe listCalculationsJson
      }
    }
  }

  "LinksFactory" when {

    class Test extends MockAppConfig {
      val hateoasFactory = new HateoasFactory(mockAppConfig)
      val nino = "someNino"
      MockedAppConfig.apiGatewayContext.returns("individuals/calculations").anyNumberOfTimes
    }

    "wrapping a ListCalculationsResponse object" should {
      "expose the correct hateoas links" in new Test {
        val item1 = CalculationListItem("calcId", "timestamp", CalculationType.inYear, None)
        hateoasFactory.wrapList(ListCalculationsResponse(Seq(item1)), ListCalculationsHateoasData(nino)) shouldBe
          HateoasWrapper(
            ListCalculationsResponse(
              Seq(HateoasWrapper(item1, Seq(Link(s"/individuals/calculations/$nino/self-assessment/calcId", GET, "self"))))),
            Seq(
              Link(s"/individuals/calculations/$nino/self-assessment", GET, "self"),
              Link(s"/individuals/calculations/$nino/self-assessment", POST, "trigger")
            )
          )
      }
    }
  }
}
