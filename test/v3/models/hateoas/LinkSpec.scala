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

package v3.models.hateoas

import play.api.libs.json.{JsValue, Json}
import support.UnitSpec
import v3.models.hateoas.Method.GET
import v3.models.hateoas.RelType._

class LinkSpec extends UnitSpec {

  val linkModel: Link = Link(href = "aRef", method = GET, rel = SELF)

  val linkJson: JsValue = Json.parse(
    """
      |{
      |  "href" : "aRef",
      |  "method" : "GET",
      |  "rel" : "self"
      |}
    """.stripMargin
  )

  "Link" when {
    "written to JSON" should {
      "produce the expected JsObject" in {
        Json.toJson(linkModel) shouldBe linkJson
      }
    }
  }

}
