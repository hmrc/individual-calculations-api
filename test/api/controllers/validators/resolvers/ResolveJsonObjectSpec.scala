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

package api.controllers.validators.resolvers

import api.models.errors.RuleIncorrectOrEmptyBodyError
import api.models.utils.JsonErrorValidators
import cats.data.Validated.{Invalid, Valid}
import play.api.libs.json.{Json, Reads}
import support.UnitSpec

class ResolveJsonObjectSpec extends UnitSpec with JsonErrorValidators {

  case class TestDataObject(fieldOne: String, fieldTwo: String)

  implicit val testDataObjectReads: Reads[TestDataObject] = Json.reads[TestDataObject]

  private val resolve = new ResolveJsonObject[TestDataObject]

  "ResolveJsonObject" should {
    "return the parsed object" when {
      "given a valid JSON object" in {
        val json = Json.parse("""{ "fieldOne" : "field one", "fieldTwo" : "field two" }""")

        val result = resolve(json)
        result shouldBe Valid(TestDataObject("field one", "field two"))
      }
    }

    "return an error " when {
      "a required field is missing" in {
        val json = Json.parse("""{ "fieldOne" : "field one" }""")

        val result = resolve(json)
        result shouldBe Invalid(List(RuleIncorrectOrEmptyBodyError.withPath("/fieldTwo")))
      }

    }

  }

}
