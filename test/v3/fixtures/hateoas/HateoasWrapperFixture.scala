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

package v3.fixtures.hateoas

import play.api.libs.json.{JsValue, Json, OWrites}
import v3.models.hateoas.{HateoasWrapper, Link}
import v3.models.hateoas.Method.GET

object HateoasWrapperFixture {

  case class TestMtdResponse(field1: String, field2: Int)

  object TestMtdResponse {
    implicit val writes: OWrites[TestMtdResponse] = Json.writes[TestMtdResponse]
  }

  val hateoasWrapperModelWithLinks: HateoasWrapper[TestMtdResponse] =
    HateoasWrapper(
      payload = TestMtdResponse(field1 = "value1", field2 = 1),
      links = Seq(
        Link(
          href = "/some/resource",
          method = GET,
          rel = "thing"
        )
      )
    )

  val hateoasWrapperModelWithoutLinks: HateoasWrapper[TestMtdResponse] = hateoasWrapperModelWithLinks.copy(links = Nil)

  val hateoasWrapperJsonWithLinks: JsValue = Json.parse(
    """
      |{
      |  "field1": "value1",
      |  "field2": 1,
      |  "links" : [
      |    {
      |      "href": "/some/resource",
      |      "rel": "thing",
      |      "method": "GET"
      |     }
      |   ]
      |}
    """.stripMargin
  )

  val hateoasWrapperJsonWithoutLinks: JsValue = Json.parse(
    """
      |{
      |  "field1": "value1",
      |  "field2": 1
      |}
    """.stripMargin
  )

}
