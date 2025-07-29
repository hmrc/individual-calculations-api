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

package common.utils.enums

import cats.Show
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.Inspectors
import play.api.libs.json._
import shared.utils.UnitSpec

enum Enum {
  case `enum-one`, `enum-two`, `enum-three`
}

object Enum {
  given Format[Enum] = Enums.format(values)
}

case class Foo[A](someField: A)

object Foo {
  implicit def fmts[A: Format]: Format[Foo[A]] = Json.format[Foo[A]]
}

class EnumsSpec extends UnitSpec with Inspectors {

  import Enum._

  implicit val arbitraryEnumValue: Arbitrary[Enum] = Arbitrary[Enum](Gen.oneOf(`enum-one`, `enum-two`, `enum-three`))

  "EnumJson" must {

    "check toString assumption" in {
      `enum-two`.toString.shouldBe("enum-two")
    }

    def json(value: Enum) = Json.parse(s"""
            |{
            | "someField": "$value"
            |}
          """.stripMargin)

    "generates reads" in {
      forAll(values.toList) { value =>
        json(value).as[Foo[Enum]].shouldBe(Foo(value))
      }
    }

    "generates writes" in {
      forAll(values.toList) { value =>
        Json.toJson(Foo(value)).shouldBe(json(value))
      }
    }

    "allow roundtrip" in {
      forAll(values.toList) { value =>
        val foo: Foo[Enum] = Foo(value)
        Json.toJson(foo).as[Foo[Enum]].shouldBe(foo)
      }
    }

    "allows external parse by name" in {
      Enums.parser(values).lift("enum-one").shouldBe(Some(`enum-one`))
      Enums.parser(values).lift("unknown").shouldBe(None)
    }

    "allows alternative names (specified by method)" in {

      enum Enum2(val altName: String) {
        case `enum-one`   extends Enum2("one")
        case `enum-two`   extends Enum2("two")
        case `enum-three` extends Enum2("three")
      }

      object Enum2 {
        given Show[Enum2] = Show.show[Enum2](_.altName)

        given Format[Enum2] = Enums.format(values)
      }

      import Enum2._

      val json: JsValue = Json.parse(
        """
          |{
          | "someField": "one"
          |}
              """.stripMargin
      )

      json.as[Foo[Enum2]] shouldBe Foo(`enum-one`)
      Json.toJson(Foo[Enum2](`enum-one`)).shouldBe(json)
    }

    "detects badly formatted values" in {
      val badJson = Json.parse(s"""
      |{
      | "someField": "unknown"
      |}
      |""".stripMargin)

      badJson.validate[Foo[Enum]].shouldBe(JsError(__ \ "someField", JsonValidationError("error.expected.Enum")))
    }

    "detects type errors" in {
      val badJson = Json.parse(s"""
                                  |{
                                  | "someField": 123
                                  |}
                                  |""".stripMargin)

      badJson.validate[Foo[Enum]].shouldBe(JsError(__ \ "someField", JsonValidationError("error.expected.jsstring")))
    }

    "allow custom reads (e.g. to allow conversion)" in {
      implicit val customReads: Reads[Enum] = Enums.readsUsing {
        case "1" => `enum-one`
        case "2" => `enum-two`
        case "3" => `enum-three`
      }

      forAll(List("1" -> `enum-one`, "2" -> `enum-two`, "3" -> `enum-three`)) { case (name, value) =>
        JsString(name).as[Enum](customReads).shouldBe(value)
      }
    }

    "allow restricted reads (e.g. when only a subset of values is permitted)" in {
      val customReads: Reads[Enum] = Enums.readsRestricted(`enum-one`, `enum-three`)

      forAll(List("enum-one" -> `enum-one`, "enum-three" -> `enum-three`)) { case (name, value) =>
        JsString(name).as[Enum](customReads).shouldBe(value)
      }

      JsString("enum-two").validate[Enum](customReads).shouldBe(JsError(__, JsonValidationError("error.expected.Enum")))

    }

    "only work for enums with singleton cases (no parameters)" in {
      assertTypeError(
        """
          |      enum NotEnum {
          |         case ObjectOne
          |         case CaseClassTwo(value: String)
          |      }
          |
          |      Enums.format(NotEnum.values)
           """.stripMargin
      )
    }
  }

}
