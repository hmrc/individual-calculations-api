/*
 * Copyright 2022 HM Revenue & Customs
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

package v2.models.response.intentToCrystallise

import mocks.MockAppConfig
import play.api.libs.json.{JsError, JsValue, Json}
import support.UnitSpec
import v2.hateoas.HateoasFactory
import v2.models.hateoas.{HateoasWrapper, Link}
import v2.models.hateoas.Method.{GET, POST}

class IntentToCrystalliseResponseSpec extends UnitSpec {

  private val model: IntentToCrystalliseResponse = IntentToCrystalliseResponse("anId")

  "IntentToCrystalliseResponse" when {
    "read from valid JSON" should {
      "produce the expected IntentToCrystalliseResponse object" in {
        val downstreamJson: JsValue = Json.parse(
          """
            |{
            |  "id": "anId"
            |}
          """.stripMargin
        )

        downstreamJson.as[IntentToCrystalliseResponse] shouldBe model
      }
    }

    "read from invalid JSON" should {
      "produce the expected IntentToCrystalliseResponse object" in {
        val invalidDownstreamJson: JsValue = Json.parse(
          """
            |{
            |  "id": true
            |}
          """.stripMargin
        )

        invalidDownstreamJson.validate[IntentToCrystalliseResponse] shouldBe a[JsError]
      }
    }

    "written to JSON" should {
      "produce the expected JsObject" in {
        val mtdJson: JsValue = Json.parse(
          """
            |{
            |  "calculationId": "anId"
            |}
          """.stripMargin
        )

        Json.toJson(model) shouldBe mtdJson
      }
    }
  }

  "LinksFactory" when {
    class Test extends MockAppConfig {
      val hateoasFactory  = new HateoasFactory(mockAppConfig)
      val nino: String    = "someNino"
      val taxYear: String = "2018-19"
      val calcId: String  = "someCalcId"
      MockAppConfig.apiGatewayContext.returns("individuals/calculations").anyNumberOfTimes
    }

    "wrapping a IntentToCrystalliseResponse object" should {
      "expose the correct hateoas links" in new Test {
        hateoasFactory.wrap(model, IntentToCrystalliseHateaosData(nino, taxYear, calcId)) shouldBe
          HateoasWrapper(
            model,
            Seq(
              Link(s"/individuals/calculations/$nino/self-assessment/$calcId", GET, "self"),
              Link(s"/individuals/calculations/crystallisation/$nino/$taxYear/crystallise", POST, "crystallise")
            )
          )
      }
    }
  }

}
