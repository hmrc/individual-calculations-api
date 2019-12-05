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

package v1.fixtures.audit

import play.api.http.Status.{BAD_REQUEST, OK}
import play.api.libs.json.{JsObject, JsValue, Json}
import v1.models.audit.{AuditError, AuditResponse}

object AuditResponseFixture {

  val auditErrors: Seq[AuditError] = Seq(AuditError("FORMAT_NINO"), AuditError("FORMAT_TAX_YEAR"))
  val body: JsValue = Json.parse("""{ "aField" : "aValue" }""".stripMargin)

  val auditResponseModelWithBody: AuditResponse = AuditResponse(OK, Right(Some(body)))
  val auditResponseModelWithErrors: AuditResponse = AuditResponse(BAD_REQUEST, Left(auditErrors))

  val auditResponseJsonWithBody: JsObject = Json.parse(
    s"""
       |{
       |  "httpStatus": 200,
       |  "body" : $body
       |}
    """.stripMargin).as[JsObject]

  val auditResponseJsonWithErrors: JsObject = Json.parse(
    s"""
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
   """.stripMargin).as[JsObject]
}
