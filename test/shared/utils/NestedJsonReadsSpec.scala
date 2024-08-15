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

package shared.utils

import play.api.libs.functional.syntax._
import play.api.libs.json._
import shared.utils.NestedJsonReads._

class NestedJsonReadsSpec extends UnitSpec {

  val firstOutput: JsValue = Json.parse("""{
      | "a" : {
      |   "b" : {
      |     "c" : "string"
      |   },
      |   "i" : {
      |     "e" : "example"
      |   }
      |  }
      |}""".stripMargin)

  val secondOutput: JsValue = Json.parse("""{
      | "a" : {
      |   "b" : {
      |     "c" : "string"
      |   },
      |   "c" : {
      |     "e" : "example"
      |   }
      |  }
      |}""".stripMargin)

  val thirdOutput: JsValue = Json.parse("""{
      | "a" : {
      |   "b" : {
      |     "c" : "string"
      |   },
      |   "c" : {
      |     "e" : 2
      |   }
      |  }
      |}""".stripMargin)

  val fourthOutput: JsValue = Json.parse("""{
      | "a" : {
      |   "b" : {
      |     "c" : "string"
      |   },
      |   "c" : {
      |   }
      |  }
      |}""".stripMargin)

  val jsonWithNull: JsValue = Json.parse("""{
      | "a" : {
      |   "b" : {
      |     "c" : "string"
      |   },
      |   "c" : {
      |     "e" : null
      |   }
      |  }
      |}""".stripMargin)

  "Valid Json" should {

    "return JsSuccess" in {
      firstOutput.validate[Test] shouldBe a[JsSuccess[_]]
    }
  }

  "A missing path" should {
    "return a None" in {
      firstOutput.as[Test] shouldBe Test("string", None)
    }
  }

  "With no missing sections" should {
    "return a full test as the second parameter" in {
      secondOutput.as[Test] shouldBe Test("string", Some("example"))
    }
  }

  "With no missing sections and a null value" should {
    "return a full test as the second parameter" in {
      jsonWithNull.as[Test] shouldBe Test("string", None)
    }
  }

  "Path with an invalid data type" should {
    "return a None " in {
      thirdOutput.validate[Test] shouldBe a[JsError]
    }
  }

  "Empty path" should {
    "return a None " in {
      fourthOutput.validate[Test] shouldBe a[JsSuccess[_]]
    }
  }

  case class Test(param: String, param3: Option[String])

  object Test {

    implicit val reads: Reads[Test] = (
      (JsPath \ "a" \ "b" \ "c").read[String] and
        (__ \ "a" \ "c" \ "e").readNestedNullable[String]
    )(Test.apply _)

  }

}
