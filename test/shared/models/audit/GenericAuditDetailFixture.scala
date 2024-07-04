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

import shared.models.audit.AuditResponseFixture.{auditResponseJsonWithErrors, auditResponseModelWithBody, auditResponseModelWithErrors}
import play.api.libs.json.{JsValue, Json}

object GenericAuditDetailFixture {

  val nino: String                         = "ZG903729C"
  val calculationId: String                = "calcId"
  val userType: String                     = "Agent"
  val agentReferenceNumber: Option[String] = Some("012345678")
  val pathParams: Map[String, String]      = Map("nino" -> nino, "calculationId" -> calculationId)
  val requestBody: Option[JsValue]         = Some(Json.obj("field1" -> "value1"))
  val xCorrId                              = "a1e8057e-fbbc-47a8-a8b478d9f015c253"
  val versionNumber: String                = "3.0"

  val genericAuditDetailModelSuccess: GenericAuditDetail =
    GenericAuditDetail(
      versionNumber = versionNumber,
      userType = userType,
      agentReferenceNumber = agentReferenceNumber,
      params = pathParams,
      requestBody = requestBody,
      `X-CorrelationId` = xCorrId,
      auditResponse = auditResponseModelWithBody
    )

  val genericAuditDetailModelError: GenericAuditDetail =
    genericAuditDetailModelSuccess.copy(
      auditResponse = auditResponseModelWithErrors
    )

  val genericAuditDetailJsonSuccess: JsValue = Json.parse(
    s"""
       |{
       |   "versionNumber": "$versionNumber",
       |   "userType" : "$userType",
       |   "agentReferenceNumber" : "${agentReferenceNumber.get}",
       |   "nino" : "$nino",
       |   "calculationId" : "$calculationId",
       |   "request":{
       |     "field1":"value1"
       |   },
       |   "response":{
       |     "httpStatus": ${auditResponseModelWithBody.httpStatus},
       |     "body": ${auditResponseModelWithBody.body.get}
       |   },
       |   "X-CorrelationId": "$xCorrId"
       |}
    """.stripMargin
  )

  val genericAuditDetailJsonError: JsValue = Json.parse(
    s"""
       |{
       |   "versionNumber": "$versionNumber",
       |   "userType" : "$userType",
       |   "agentReferenceNumber" : "${agentReferenceNumber.get}",
       |   "nino": "$nino",
       |   "calculationId" : "$calculationId",
       |   "request":{
       |     "field1":"value1"
       |   },
       |   "response": $auditResponseJsonWithErrors,
       |   "X-CorrelationId": "$xCorrId"
       |}
     """.stripMargin
  )

}
