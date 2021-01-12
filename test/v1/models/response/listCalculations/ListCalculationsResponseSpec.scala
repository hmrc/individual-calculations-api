/*
 * Copyright 2021 HM Revenue & Customs
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
import v1.fixtures.listCalculations.CalculationListItemFixture.{calculationListItemJson, calculationListItemModel}
import v1.fixtures.listCalculations.ListCalculationsFixture._
import v1.hateoas.HateoasFactory
import v1.mocks.MockAppConfig
import v1.models.hateoas.Method.{GET, POST}
import v1.models.hateoas.{HateoasWrapper, Link}

class ListCalculationsResponseSpec extends UnitSpec {

  "CalculationListItem" when {
    "read from valid JSON" should {
      "produce the expected CalculationListItem" in {
        calculationListItemJson.as[CalculationListItem] shouldBe calculationListItemModel
      }
    }

    "written to JSON" must {
      "produce the expected JsObject" in {
        Json.toJson(calculationListItemModel) shouldBe calculationListItemJson
      }
    }
  }

  "ListCalculationsResponse" when {
    "read from valid JSON" should {
      "produce the expected ListCalculationsResponse object" in {
        listCalculationsResponseJson.as[ListCalculationsResponse[CalculationListItem]] shouldBe listCalculationsResponseModel
      }
    }

    "written to JSON" should {
      "produce the expected JsObject" in {
        Json.toJson(listCalculationsResponseModel) shouldBe listCalculationsResponseJson
      }
    }
  }

  "LinksFactory" when {
    class Test extends MockAppConfig {
      val hateoasFactory = new HateoasFactory(mockAppConfig)
      val nino = "someNino"
      val calcId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"
      MockedAppConfig.apiGatewayContext.returns("individuals/calculations").anyNumberOfTimes
    }

    "wrapping a ListCalculationsResponse object" should {
      "expose the correct hateoas links" in new Test {
        hateoasFactory.wrapList(ListCalculationsResponse(Seq(calculationListItemModel)), ListCalculationsHateoasData(nino)) shouldBe
          HateoasWrapper(
            ListCalculationsResponse(
              Seq(
                HateoasWrapper(
                  calculationListItemModel,
                  Seq(Link(s"/individuals/calculations/$nino/self-assessment/$calcId", GET, "self"))
                )
              )
            ),
            Seq(
              Link(s"/individuals/calculations/$nino/self-assessment", GET, "self"),
              Link(s"/individuals/calculations/$nino/self-assessment", POST, "trigger")
            )
          )
      }
    }
  }
}
