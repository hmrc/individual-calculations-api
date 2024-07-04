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

package shared.models.audit

import play.api.http.Status.{BAD_REQUEST, OK}
import play.api.libs.json.{JsValue, Json}

object AuditResponseFixture {

  val auditErrors: Seq[AuditError] = List(AuditError(errorCode = "FORMAT_NINO"), AuditError(errorCode = "FORMAT_TAX_YEAR"))
  val body: JsValue                = Json.parse("""{ "aField" : "aValue" }""")

  val auditResponseModelWithBody: AuditResponse =
    AuditResponse(
      httpStatus = OK,
      response = Right(Some(body))
    )

  val auditResponseModelWithErrors: AuditResponse =
    AuditResponse(
      httpStatus = BAD_REQUEST,
      response = Left(auditErrors)
    )

  val auditResponseJsonWithBody: JsValue = Json.parse(
    s"""
       |{
       |  "httpStatus": $OK,
       |  "body" : $body
       |}
    """.stripMargin
  )

  val auditResponseJsonWithErrors: JsValue = Json.parse(
    s"""
       |{
       |  "httpStatus": $BAD_REQUEST,
       |  "errors" : [
       |    {
       |      "errorCode" : "FORMAT_NINO"
       |    },
       |    {
       |      "errorCode" : "FORMAT_TAX_YEAR"
       |    }
       |  ]
       |}
    """.stripMargin
  )

}
