/*
 * Copyright 2024 HM Revenue & Customs
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

import play.api.libs.json.{JsObject, Json}
import shared.controllers.validators.resolvers.UnexpectedJsonFieldsValidator.SchemaStructureSource
import shared.models.errors.RuleIncorrectOrEmptyBodyError
import shared.utils.UnitSpec

class UnexpectedJsonFieldsValidatorSpec extends UnitSpec {

  sealed trait SomeEnum

  object SumEnum {
    object X extends SomeEnum
    object Y extends SomeEnum
  }

  case class Bar(a: Option[String] = None, b: Option[String] = None, e: Option[SomeEnum] = None)

  case class Foo(bar: Bar, bars: Option[Seq[Bar]] = None, bar2: Option[Bar] = None)

  implicit val someEnumChecker: SchemaStructureSource[SomeEnum] = SchemaStructureSource.leaf

  val validator = new UnexpectedJsonFieldsValidator[Foo]

  private def errorWithPaths(paths: String*) = Some(Seq(RuleIncorrectOrEmptyBodyError.withPaths(paths)))

  "UnexpectedJsonFieldsValidator" when {
    "there are no extra fields" must {
      "validate successfully" in {
        val json = Json.parse("""{ "bar": {"a" : "v1", "b" : "v2" }, "bars": []}""").as[JsObject]
        val data = Foo(bar = Bar(Some("v1"), Some("v2")), Some(Nil))

        validator.validator((json, data)) shouldBe None
      }

      "validate successfully when object fields are in a different order" in {
        val json = Json.parse("""{ "bars": [], "bar2": {"b" : "v2" }, "bar": {"a" : "v1" }}""").as[JsObject]
        val data = Foo(bar = Bar(Some("v1"), None), Some(Nil), bar2 = Some(Bar(None, Some("v2"))))

        validator.validator((json, data)) shouldBe None
      }
    }

    "optional fields are missing" must {
      "validate successfully" in {
        val json = Json.parse("""{ "bar": {"a" : "v1", "b" : "v2" }}""").as[JsObject]
        val data = Foo(bar = Bar(Some("v1"), Some("v2")))

        validator.validator((json, data)) shouldBe None
      }
    }

    "an additional field is present" when {
      "a top level extra field is present" when {
        def bazWithValue(bazValue: String) =
          Json
            .parse(s"""{ "baz": $bazValue, "bar": {"a" : "v1",  "b" : "v2" }}""".stripMargin)
            .as[JsObject]

        val data = Foo(bar = Bar(Some("v1"), Some("v2")), bars = None)

        "the field is a string" must {
          "return an error with path to the extra field" in {
            validator.validator((bazWithValue(""""extra""""), data)) shouldBe errorWithPaths("/baz")
          }
        }

        "the field is a number" must {
          "return an error with path to the extra field" in {
            validator.validator((bazWithValue("123"), data)) shouldBe errorWithPaths("/baz")
          }
        }

        "the field is a boolean" must {
          "return an error with path to the extra field" in {
            validator.validator((bazWithValue("true"), data)) shouldBe errorWithPaths("/baz")
          }
        }

        "the field is a object" must {
          "return an error with path to the extra field" in {
            validator.validator((bazWithValue("""{"bazField": "value"}"""), data)) shouldBe errorWithPaths("/baz")
          }
        }

        "the field is a array" must {
          "return an error with path to the extra field" in {
            validator.validator((bazWithValue("""["value"]"""), data)) shouldBe errorWithPaths("/baz")
          }
        }
      }

      "locate extra fields when object fields are in a different order" in {
        val json = Json.parse("""{ "bar2": {"b" : "v2", "baz": 123 }, "bar": {"a" : "v1" }}""").as[JsObject]
        val data = Foo(bar = Bar(Some("v1"), None), bar2 = Some(Bar(None, Some("v2"))))

        validator.validator((json, data)) shouldBe errorWithPaths("/bar2/baz")
      }

      "a nested extra field is present" when {
        val data = Foo(bar = Bar(Some("v1"), Some("v2")), bars = Some(Seq(Bar(Some("v1"), Some("v2")), Bar(Some("v1"), Some("v2")))))

        "the field is nested in an object" must {
          "return an error with path to the extra field" in {
            val json = Json
              .parse("""{ "bar": {"a" : "v1", "baz": "extra", "b" : "v2" }, 
                   |  "bars": [
                   |    {"a" : "v1",  "b" : "v2" }, 
                   |    {"a" : "v1", "b" : "v2" }
                   |  ]
                   |}""".stripMargin)
              .as[JsObject]

            validator.validator((json, data)) shouldBe errorWithPaths("/bar/baz")
          }
        }

        "the field is nested in an object in an array" must {
          "return an error with path to the extra field" in {
            val json = Json
              .parse("""{
                   |  "bar": {"a" : "v1", "b" : "v2" },
                   |  "bars": [
                   |    {"a" : "v1",  "b" : "v2" }, 
                   |    {"a" : "v1", "baz": "extra", "b" : "v2" }
                   |  ]
                   |}""".stripMargin)
              .as[JsObject]

            validator.validator((json, data)) shouldBe errorWithPaths("/bars/1/baz")
          }
        }

        "multiple additional fields are present" must {
          "return an error with paths to the extra fields" in {
            val json = Json
              .parse("""{
                 |  "bar": {"a" : "v1", "b" : "v2" , "baz": "extra"},
                 |  "baz": "extra",
                 |  "bars": [
                 |    {"a" : "v1", "baz": "extra0", "b" : "v2" }, 
                 |    {"a" : "v1", "baz": "extra1", "b" : "v2" }
                 |  ]
                 |}""".stripMargin)
              .as[JsObject]

            validator.validator((json, data)) shouldBe errorWithPaths("/baz", "/bar/baz", "/bars/0/baz", "/bars/1/baz")
          }
        }

        // A robustness check that the implementation does not rely on the fields that _should_ be present or their values:
        "mandatory values in the input JSON are missing or have different values from the data" must {
          "ignore these and only flag up extra fields" in {
            val json = Json
              .parse("""{
                       |  "bar": {"a" : "wrongValueIgnored", "baz": "extra"},
                       |  "baz": "extra",
                       |  "bars": [
                       |    { "baz": "extra0" },
                       |    { "baz": "extra1" }
                       |  ]
                       |}""".stripMargin)
              .as[JsObject]

            validator.validator((json, data)) shouldBe errorWithPaths("/baz", "/bar/baz", "/bars/0/baz", "/bars/1/baz")
          }
        }

        // In case we need to use a sealed trait to represent 'one of' options for a field
        "must be able to work with co-product types (field of type A is actually A1 or A2 etc)" when {
          sealed trait A
          case class A1(a1: Int) extends A
          case class A2(a2: Int) extends A
          case class Foo2(a: A)

          val dataA1 = Foo2(A1(1))

          val extraPathCheckerA1 = SchemaStructureSource[A1]
          val extraPathCheckerA2 = SchemaStructureSource[A2]

          implicit val extraPathCheckerA: SchemaStructureSource[A] = SchemaStructureSource.instance {
            case a1: A1 => extraPathCheckerA1.schemaStructureOf(a1)
            case a2: A2 => extraPathCheckerA2.schemaStructureOf(a2)
          }

          val validator = new UnexpectedJsonFieldsValidator[Foo2]

          "correct fields are present" in {
            val json = Json.parse("""{ "a": { "a1": 1} }""").as[JsObject]

            validator.validator((json, dataA1)) shouldBe None
          }

          "extra fields are present" in {
            val json = Json.parse("""{ "a": { "a1": 1, "extra": 123 } }""").as[JsObject]

            validator.validator((json, dataA1)) shouldBe errorWithPaths("/a/extra")
          }

          // (This is likely to be a coding error in the JSON Reads)
          "the other type's fields are present" in {
            val json = Json.parse("""{ "a": { "a2": 2} }""").as[JsObject]

            validator.validator((json, dataA1)) shouldBe errorWithPaths("/a/a2")
          }
        }
      }
    }
  }

}
