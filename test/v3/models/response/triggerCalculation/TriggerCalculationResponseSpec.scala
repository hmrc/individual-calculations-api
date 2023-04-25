/*
 * Copyright 2023 HM Revenue & Customs
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

package v3.models.response.triggerCalculation

import mocks.MockAppConfig
import play.api.libs.json.{JsValue, Json}
import support.UnitSpec
import v3.hateoas.HateoasFactory
import v3.models.domain.TaxYear
import v3.models.hateoas.Method.GET
import v3.models.hateoas.{HateoasWrapper, Link}

class TriggerCalculationResponseSpec extends UnitSpec {

  private val calculationId = "testId"

  val triggerCalculationResponseModel: TriggerCalculationResponse = TriggerCalculationResponse(calculationId)

  val downstreamResponseJson: JsValue = Json.parse(
    s"""
       |{
       | "id": "$calculationId"
       |}
    """.stripMargin
  )

  val vendorResponseJson: JsValue = Json.parse(
    s"""
       |{
       | "calculationId": "$calculationId"
       |}
    """.stripMargin
  )

  "TriggerCalculationResponse" when {
    "read from valid JSON from the downstream response" should {
      "produce the expected TriggerCalculationResponse object" in {
        downstreamResponseJson.as[TriggerCalculationResponse] shouldBe triggerCalculationResponseModel
      }
    }

    "written to JSON" should {
      "produce the expected JsObject" in {
        Json.toJson(triggerCalculationResponseModel) shouldBe vendorResponseJson
      }
    }
  }

  "LinksFactory" when {
    class Test extends MockAppConfig {
      val hateoasFactory = new HateoasFactory(mockAppConfig)
      val nino           = "someNino"
      val taxYear        = TaxYear.fromMtd("2020-21")
      MockAppConfig.apiGatewayContext.returns("individuals/calculations").anyNumberOfTimes()
    }

    "wrapping a TriggerCalculationResponse object" should {
      "expose the correct hateoas links" in new Test {
        hateoasFactory.wrap(
          triggerCalculationResponseModel,
          TriggerCalculationHateoasData(nino, taxYear, finalDeclaration = true, calculationId)) shouldBe
          HateoasWrapper(
            triggerCalculationResponseModel,
            Seq(
              Link(s"/individuals/calculations/$nino/self-assessment?taxYear=2020-21", GET, "list"),
              Link(s"/individuals/calculations/$nino/self-assessment/2020-21/$calculationId", GET, "self")
            )
          )
      }
    }
  }

}
