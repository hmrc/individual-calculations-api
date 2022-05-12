/*
 * Copyright 2022 HM Revenue & Customs
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

package v3.fixtures.audit

import play.api.libs.json.{JsValue, Json}
import v3.fixtures.audit.AuditResponseFixture._
import v3.models.audit.GenericAuditDetail

object GenericAuditDetailFixture {

  val nino: String                         = "ZG903729C"
  val calculationId: String                = "calcId"
  val userType: String                     = "Agent"
  val agentReferenceNumber: Option[String] = Some("012345678")
  val pathParams: Map[String, String]      = Map("nino" -> nino, "calculationId" -> calculationId)
  val requestBody: Option[JsValue]         = None
  val xCorrId: String                      = "a1e8057e-fbbc-47a8-a8b478d9f015c253"

  val genericAuditDetailModelSuccess: GenericAuditDetail = GenericAuditDetail("Individual", None, pathParams, None, xCorrId,
    versionNumber = "3.0", response = "success", httpStatusCode = 202, calculationId = Some(calculationId),
    errorCodes = None)


  val genericAuditDetailModelError: GenericAuditDetail =
    genericAuditDetailModelSuccess.copy(
      httpStatusCode = auditResponseModelWithErrors.httpStatus,
      response = "error",
      errorCodes = Some(Seq("FORMAT_NINO"))
    )

  val genericAuditDetailJsonSuccess: JsValue = Json.parse(
    s"""
       |{
       |   "userType":"Individual",
       |   "calculationId":"calcId",
       |   "nino":"ZG903729C",
       |   "X-CorrelationId":"a1e8057e-fbbc-47a8-a8b478d9f015c253",
       |   "versionNumber":"3.0",
       |   "response":"success",
       |   "httpStatusCode":202
       |}
     """.stripMargin
  )

  val genericAuditDetailJsonError: JsValue = Json.parse(
    s"""
       |{
       |   "userType":"Individual",
       |   "calculationId":"calcId",
       |   "nino":"ZG903729C",
       |   "X-CorrelationId":"a1e8057e-fbbc-47a8-a8b478d9f015c253",
       |   "versionNumber":"3.0",
       |   "response":"error",
       |   "httpStatusCode":400,
       |   "errorCodes":[
       |      "FORMAT_NINO"
       |   ]
       |}
     """.stripMargin
  )

}
