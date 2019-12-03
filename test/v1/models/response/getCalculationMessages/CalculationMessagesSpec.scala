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

package v1.models.response.getCalculationMessages

import play.api.libs.json.Json
import support.UnitSpec
import v1.fixtures.getCalculationMessages.GetCalculationMessagesFixture._
import v1.fixtures.getCalculationMessages.{GetCalculationMessagesFixture, MessageFixture}
import v1.hateoas.HateoasFactory
import v1.mocks.MockAppConfig
import v1.models.hateoas.Method.GET
import v1.models.hateoas.{HateoasWrapper, Link}
import v1.models.utils.JsonErrorValidators

class CalculationMessagesSpec extends UnitSpec with JsonErrorValidators {

  "Message" when {
    testJsonProperties[Message](MessageFixture.mtdJson)(
      mandatoryProperties = Seq("id", "text"),
      optionalProperties = Seq()
    )
  }

  "GetCalculationMessages" when {

    testJsonProperties[CalculationMessages](GetCalculationMessagesFixture.mtdJson)(
      mandatoryProperties = Seq(),
      optionalProperties = Seq("info", "warnings", "errors")
    )

    "written to JSON" should {
      "take the full messages form" in {
        Json.toJson[CalculationMessages](response) shouldBe outputMessagesJson
      }
    }
  }

  "HateoasFactory" must {

    class Test extends MockAppConfig {
      val hateoasFactory = new HateoasFactory(mockAppConfig)
      val nino = "someNino"
      MockedAppConfig.apiGatewayContext.returns("individuals/calculations").anyNumberOfTimes
    }

    "expose the correct links" in new Test {
      val item1 = CalculationMessages(Some(Seq(Message("a", "message"))), None, None)
      hateoasFactory.wrap(item1, CalculationMessagesHateoasData(nino, "calcId")) shouldBe
        HateoasWrapper(
          item1,
          Seq(
            Link(s"/individuals/calculations/$nino/self-assessment/calcId", GET, "metadata"),
            Link(s"/individuals/calculations/$nino/self-assessment/calcId/messages", GET, "self")
          )
        )
    }
  }
}
