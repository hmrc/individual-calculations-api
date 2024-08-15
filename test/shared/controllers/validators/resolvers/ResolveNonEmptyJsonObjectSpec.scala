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
import shapeless.HNil
import shared.models.errors.RuleIncorrectOrEmptyBodyError
import shared.models.utils.JsonErrorValidators
import shared.utils.{EmptinessChecker, UnitSpec}

class ResolveNonEmptyJsonObjectSpec extends UnitSpec with ResolverSupport with JsonErrorValidators {

  case class Bar(field1: String, field2: String)
  case class Baz(field1: Option[String], field2: Option[String])
  case class Qux(mandatory: String, oneOf1: Option[String] = None, oneOf2: Option[String] = None)

  // at least one of oneOf1 and oneOf2 must be included:
  implicit val emptinessChecker: EmptinessChecker[Qux] = EmptinessChecker.use { o =>
    "oneOf1" -> o.oneOf1 :: "oneOf2" -> o.oneOf2 :: HNil
  }

  case class Foo(bar: Bar, bars: Option[Seq[Bar]] = None, baz: Option[Baz] = None, qux: Option[Qux] = None)

  implicit val barFormat: Reads[Bar] = Json.reads
  implicit val bazFormat: Reads[Baz] = Json.reads
  implicit val quxFormat: Reads[Qux] = Json.reads
  implicit val fooReads: Reads[Foo]  = Json.reads

  private def jsonObjectResolver(resolver: Resolver[JsValue, Foo]): Unit = {
    "return the parsed object" when {
      "given a valid JSON object" in {
        val json = Json.parse("""{ "bar": {"field1" : "field one", "field2" : "field two" }}""")

        resolver(json) shouldBe Valid(Foo(bar = Bar("field one", "field two")))
      }
    }

    "return the expected error " when {
      "a required field is missing" in {
        val json = Json.parse("""{ "bar": {"field1" : "field one" } }""")

        resolver(json) shouldBe Invalid(List(RuleIncorrectOrEmptyBodyError.withPath("/bar/field2")))
      }
    }
  }

  private def jsonObjectResolverWithEmptinessChecking(resolver: Resolver[JsValue, Foo]): Unit = {

    "given an empty JSON object at the top level" in {
      resolver(JsObject.empty) shouldBe Invalid(List(RuleIncorrectOrEmptyBodyError))
    }

    "detect empty objects" in {
      val json = Json.parse("""{ "bar": {"field1" : "field one", "field2" : "field two" }, "baz": {} }""")

      resolver(json) shouldBe Invalid(List(RuleIncorrectOrEmptyBodyError.withPath("/baz")))
    }

    "detect empty arrays" in {
      val json = Json.parse("""{ "bar": {"field1" : "field one", "field2" : "field two" }, "bars": [] }""")

      resolver(json) shouldBe Invalid(List(RuleIncorrectOrEmptyBodyError.withPath("/bars")))
    }

    "allow custom emptiness checker requiring at least one of a number of optional fields" when {
      "one of the optional fields is present" must {
        "return the object" in {
          val json = Json.parse("""{ "bar": {"field1" : "field one", "field2" : "field two" }, "qux": { "mandatory": "m", "oneOf1": "a1" } }""")

          resolver(json) shouldBe Valid(Foo(bar = Bar("field one", "field two"), qux = Some(Qux("m", oneOf1 = Some("a1")))))
        }
      }

      "none of the required optional fields is present" must {
        "detect this" in {
          val json = Json.parse("""{ "bar": {"field1" : "field one", "field2" : "field two" }, "qux": { "mandatory": "m" } }""")

          resolver(json) shouldBe Invalid(List(RuleIncorrectOrEmptyBodyError.withPath("/qux")))
        }
      }
    }
  }

  "ResolveNonEmptyJsonObject" when {

    "the default resolver is used" must {
      val resolver = ResolveNonEmptyJsonObject.resolver[Foo]

      behave like jsonObjectResolver(resolver)
      behave like jsonObjectResolverWithEmptinessChecking(resolver)

      "not detect extra fields in the input" in {
        val json = Json.parse("""{ "bar": {"field1" : "field one", "field2" : "field two", "extra": "x" }}""")

        resolver(json) shouldBe Valid(Foo(bar = Bar("field one", "field two")))
      }
    }

    "the strict resolver is used" must {
      val resolver = ResolveNonEmptyJsonObject.strictResolver[Foo]

      behave like jsonObjectResolver(resolver)
      behave like jsonObjectResolverWithEmptinessChecking(resolver)

      "detect extra fields in the input" in {
        val json = Json.parse("""{ "bar": {"field1" : "field one", "field2" : "field two", "extra": "x" }}""")

        resolver(json) shouldBe Invalid(List(RuleIncorrectOrEmptyBodyError.withPath("/bar/extra")))
      }

      "detect extra fields first (when object would otherwise be detected as empty)" in {
        val json = Json.parse("""{ "bar": {"field1" : "field one", "field2" : "field two" }, "baz": { "extra": "x" } }""")

        resolver(json) shouldBe Invalid(List(RuleIncorrectOrEmptyBodyError.withPath("/baz/extra")))
      }
    }
  }

}
