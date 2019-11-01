/*
 * Copyright 2019 HM Revenue & Customs
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
package utils.enums

import play.api.libs.functional.syntax._
import play.api.libs.json.{Reads, _}
import support.UnitSpec
import utils.NestedJsonReads._


class NestedJsonReadsSpec extends UnitSpec {

  val jsonOutput: JsValue = Json.parse(
    """{
      | "a" : {
      |   "b" : {
      |     "c" : "string",
      |     "d" : 2
      |   },
      |   "c" : {
      |     "e" : "d"
      |   },
      |   "d" : [
      |   {"f": 0},
      |   {"f": 0}
      |   ]
      | }
      |}""".stripMargin)

  case class Test(param: String, param2: Int, param3: Option[String], param4: Option[Seq[Int]])

  object Test {
    implicit val reads: Reads[Test] = (
      (JsPath \ "a" \ "b" \ "c").read[String] and
        (__ \ "a" \ "b" \ "d").read[Int] and
        (__ \ "a" \ "c" \ "e").readNestedNullable[String] and
        (__ \ "a" \ "d" \\ "f" ).readNestedNullable[Seq[Int]]
      ) (Test.apply _)
  }

  "Valid Json" should {

    "return JsSuccess" in {
      jsonOutput.validate[Test] shouldBe a[JsSuccess[_]]
    }
  }

}
