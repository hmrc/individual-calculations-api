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

package v2.models.response.getMessages

import mocks.MockAppConfig
import play.api.libs.json.Json
import support.UnitSpec
import v2.fixtures.getMessages.MessagesResponseFixture._
import v2.fixtures.getMessages.MessageFixture._
import v2.hateoas.HateoasFactory
import v2.models.hateoas.{HateoasWrapper, Link}
import v2.models.hateoas.Method.GET
import v2.models.utils.JsonErrorValidators

class MessagesResponseSpec extends UnitSpec with JsonErrorValidators {

  "Message" when {
    testJsonProperties[Message](messageJson)(
      mandatoryProperties = Seq(
        "id",
        "text"
      ),
      optionalProperties = Seq()
    )
  }

  "MessagesResponse" when {
    "read from valid JSON" should {
      "produce the expected MessagesResponse object" in {
        messagesResponseTopLevelJson.as[MessagesResponse] shouldBe messagesResponseModel
      }
    }

    "written to JSON" should {
      "produce the expected JsObject" in {
        Json.toJson[MessagesResponse](messagesResponseModel) shouldBe messagesResponseJson
      }
    }
  }

  "LinksFactory" when {
    class Test extends MockAppConfig {
      val hateoasFactory = new HateoasFactory(mockAppConfig)
      val nino: String = "someNino"
      val calcId: String = "id"
      MockAppConfig.apiGatewayContext.returns("individuals/calculations").anyNumberOfTimes
    }

    "wrapping a MessagesResponse object" should {
      "expose the correct hateoas links" in new Test {
        hateoasFactory.wrap(messagesResponseModel, MessagesHateoasData(nino, calcId)) shouldBe
          HateoasWrapper(
            messagesResponseModel,
            Seq(
              Link(s"/individuals/calculations/$nino/self-assessment/$calcId", GET, "metadata"),
              Link(s"/individuals/calculations/$nino/self-assessment/$calcId/messages", GET, "self")
            )
          )
      }
    }
  }
}