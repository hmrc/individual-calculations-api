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

  val triggerCalculationResponseModel = TriggerCalculationResponse("testId")

  val triggerCalculationResponseJson: JsValue = Json.parse(
    """
      |{
      | "id": "testId"
      |}
    """.stripMargin
  )

  "TriggerCalculationResponse" when {
    "read from valid JSON" should {
      "produce the expected TriggerCalculationResponse object" in {
        triggerCalculationResponseJson.as[TriggerCalculationResponse] shouldBe triggerCalculationResponseModel
      }
    }

    "written to JSON" should {
      "produce the expected JsObject" in {
        Json.toJson(triggerCalculationResponseModel) shouldBe triggerCalculationResponseJson
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
      "expose the correct hateoas links" in new Test {
        hateoasFactory.wrap(triggerCalculationResponseModel, TriggerCalculationHateoasData(nino, "calcId")) shouldBe
          HateoasWrapper(
            triggerCalculationResponseModel,
            Seq(Link(s"/individuals/calculations/$nino/self-assessment/calcId", GET, "self"))
          )
      }
    }
  }
}