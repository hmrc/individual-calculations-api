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

package v1.models.hateoas

import play.api.libs.json.Json
import support.UnitSpec
import v1.fixtures.hateoas.HateoasWrapperFixture._

class HateoasWrapperSpec extends UnitSpec {

  "HateoasWrapper" should {
    "produce the expected JsObject" when {
      "passed a model with links" in {
        Json.toJson(hateoasWrapperModelWithLinks) shouldBe hateoasWrapperJsonWithLinks
      }
      "passed a model without links" in {
        Json.toJson(hateoasWrapperModelWithoutLinks) shouldBe hateoasWrapperJsonWithoutLinks
      }
      "passed a JsValue with links" in {
        Json.toJson(hateoasWrapperModelWithLinks.copy(payload = Json.toJson(exampleModel))) shouldBe hateoasWrapperJsonWithLinks
      }
      "passed a JsValue without links" in {
        Json.toJson(hateoasWrapperModelWithoutLinks.copy(payload = Json.toJson(exampleModel))) shouldBe hateoasWrapperJsonWithoutLinks
      }
    }
  }
}
