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

package v1.models.audit

import play.api.libs.json.{JsValue, Json}
import support.UnitSpec

class AuditResponseSpec extends UnitSpec {


  private val body: JsValue = Json.parse("""{ "aField" : "aValue" }""".stripMargin)
  private val auditErrors: Seq[AuditError] = Seq(AuditError("FORMAT_NINO"), AuditError("FORMAT_TAX_YEAR"))

  private val auditResponseWithBody: AuditResponse = AuditResponse(200, Right(Some(body)))
  private val auditResponseWithErrors: AuditResponse = AuditResponse(400, Left(auditErrors))

  val jsonWithErrors: JsValue = Json.parse(s"""
       |{
       |  "httpStatus": 400,
       |  "errors" : [
       |    {
       |      "errorCode" : "FORMAT_NINO"
       |    },
       |    {
       |      "errorCode" : "FORMAT_TAX_YEAR"
       |    }
       |  ]
       |}
    """.stripMargin)

  val jsonWithBody: JsValue = Json.parse(s"""
      |{
      |  "httpStatus": 200,
      |  "body" : {
      |     "aField" : "aValue"
      |   }
      |}
    """.stripMargin)

  "AuditResponse" when {
    "written to JSON with a body" should {
      "produce the expected JsObject" in {
        Json.toJson(auditResponseWithBody) shouldBe jsonWithBody
      }
    }

    "written to JSON with Audit Errors" should {
      "produce the expected JsObject" in {
        Json.toJson(auditResponseWithErrors) shouldBe jsonWithErrors
      }
    }
  }
}
