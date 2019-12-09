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

package v1.models.response.triggerCalculation

import play.api.libs.json.{JsValue, Json}
import support.UnitSpec
import v1.hateoas.HateoasFactory
import v1.mocks.MockAppConfig
import v1.models.hateoas.Method.GET
import v1.models.hateoas.{HateoasWrapper, Link}

class TriggerCalculationResponseSpec extends UnitSpec {

  val json: JsValue = Json.parse(
    """
      |{
      | "id": "testId"
      |}
    """.stripMargin
  )

  val response = TriggerCalculationResponse("testId")

  "TriggerCalculationResponse" when {
    "read from valid JSON" should {
      "return the expected TriggerCalculationResponse object" in {
        json.as[TriggerCalculationResponse] shouldBe response
      }
    }

    "written to JSON" should {
      "produce the expected JsObject" in {
        Json.toJson(response) shouldBe json
      }
    }
  }

  "LinksFactory" when {
    class Test extends MockAppConfig {
      val hateoasFactory = new HateoasFactory(mockAppConfig)
      val nino = "someNino"
      MockedAppConfig.apiGatewayContext.returns("individuals/calculations").anyNumberOfTimes
    }

    "wrapping a TriggerCalculationResponse object" should {
      "expose the correct links" in new Test {
        val item1 = TriggerCalculationResponse("calcId")
        hateoasFactory.wrap(item1, TriggerCalculationHateoasData(nino, "calcId")) shouldBe
          HateoasWrapper(item1, Seq(Link(s"/individuals/calculations/$nino/self-assessment/calcId", GET, "self")))
      }
    }
  }
}