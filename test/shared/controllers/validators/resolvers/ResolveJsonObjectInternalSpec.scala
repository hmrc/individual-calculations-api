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

package shared.controllers.validators.resolvers

import cats.data.Validated.{Invalid, Valid}
import play.api.libs.json._
import shared.models.errors.RuleIncorrectOrEmptyBodyError
import shared.models.utils.JsonErrorValidators
import shared.utils.UnitSpec

class ResolveJsonObjectInternalSpec extends UnitSpec with JsonErrorValidators {

  case class Bar(field1: String, field2: String)

  case class Foo(bar: Bar, bars: Seq[Bar])

  implicit val barReads: Reads[Bar] = Json.reads
  implicit val fooReads: Reads[Foo] = Json.reads

  private val resolver = ResolveJsonObjectInternal.resolver[Foo]

  "ResolveJsonObjectInternal" when {
    "the JSON is valid" must {
      "return the JSON and output object" in {
        val json = Json.parse("""{ "bar": {"field1" : "field one", "field2" : "field two" }, "bars": []}""")

        resolver(json) shouldBe Valid((json, Foo(bar = Bar("field one", "field two"), Nil)))
      }
    }

    "an empty object is passed" must {
      "return an RuleIncorrectOrEmptyBodyError" in {
        resolver(JsObject.empty) shouldBe Invalid(List(RuleIncorrectOrEmptyBodyError))
      }
    }

    "a string is passed" must {
      "return an RuleIncorrectOrEmptyBodyError" in {
        resolver(JsString("someString")) shouldBe Invalid(List(RuleIncorrectOrEmptyBodyError))
      }
    }

    "a boolean is passed" must {
      "return an RuleIncorrectOrEmptyBodyError" in {
        resolver(JsBoolean(true)) shouldBe Invalid(List(RuleIncorrectOrEmptyBodyError))
      }
    }

    "an array is passed" must {
      "return an RuleIncorrectOrEmptyBodyError" in {
        resolver(JsArray(Seq.empty)) shouldBe Invalid(List(RuleIncorrectOrEmptyBodyError))
      }
    }

    "an Null is passed" must {
      "return an RuleIncorrectOrEmptyBodyError" in {
        resolver(JsNull) shouldBe Invalid(List(RuleIncorrectOrEmptyBodyError))
      }
    }

    "required fields are missing" when {
      "the field is at the top level" must {
        "return an error with path to the missing field" in {
          val json = Json.parse("""{ "bars": []}""")

          resolver(json) shouldBe Invalid(List(RuleIncorrectOrEmptyBodyError.withPath("/bar")))
        }
      }

      "the field is nested in an object" must {
        "return an error with path to the missing field" in {
          val json = Json.parse("""{ "bar": {"field1" : "field one" }, "bars": []}""")

          resolver(json) shouldBe Invalid(List(RuleIncorrectOrEmptyBodyError.withPath("/bar/field2")))
        }
      }

      "the field is nested in an object in an array" must {
        "return an error with path to the missing field" in {
          val json = Json.parse("""{ "bar": {"field1" : "field one", "field2" : "field two" }, "bars": [{"field1" : "field one" }]}""")

          resolver(json) shouldBe Invalid(List(RuleIncorrectOrEmptyBodyError.withPath("/bars/0/field2")))
        }
      }

      "multiple fields are missing" must {
        "return an error with paths to the missing fields" in {
          val json = Json.parse("""{ "bar": {}, "bars": []}""")

          resolver(json) shouldBe Invalid(List(RuleIncorrectOrEmptyBodyError.withPaths(Seq("/bar/field1", "/bar/field2"))))
        }
      }
    }

    "fields are the wrong type" must {
      "the field is at the top level" must {
        "return an error with path to the bad field" in {
          val json = Json.parse("""{ "bar": true, "bars": []}""")

          resolver(json) shouldBe Invalid(List(RuleIncorrectOrEmptyBodyError.withPath("/bar")))
        }
      }

      "the field is nested in an object" must {
        "return an error with path to the bad field" in {
          val json = Json.parse("""{ "bar": {"field1" : "field one", "field2": 2 }, "bars": []}""")

          resolver(json) shouldBe Invalid(List(RuleIncorrectOrEmptyBodyError.withPath("/bar/field2")))
        }
      }

      "the field is nested in an object in an array" must {
        "return an error with path to the bad field" in {
          val json =
            Json.parse("""{ "bar": {"field1" : "field one", "field2" : "field two" }, "bars": [{"field1" : "field one", "field2": 2 }]}""")

          resolver(json) shouldBe Invalid(List(RuleIncorrectOrEmptyBodyError.withPath("/bars/0/field2")))
        }
      }

      "multiple fields are the wrong type" must {
        "return an error with paths to the bad fields" in {
          val json = Json.parse("""{ "bar": {"field1" : "field one", "field2" : "field two" }, "bars": [{"field1" : 2, "field2": 2 }]}""")

          resolver(json) shouldBe Invalid(List(RuleIncorrectOrEmptyBodyError.withPaths(Seq("/bars/0/field1", "/bars/0/field2"))))
        }
      }
    }

    "multiple types of errors are present" must {
      "the field is at the top level" must {
        "return an error with paths to all problematic fields" in {
          val json = Json.parse("""{  "bars": [{"field1" : "field one", "field2": 2 }]}""")

          resolver(json) shouldBe Invalid(List(RuleIncorrectOrEmptyBodyError.withPaths(Seq("/bar", "/bars/0/field2"))))
        }
      }
    }

  }

}
