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

import play.api.libs.json.{JsObject, Json}
import v1.fixtures.audit.AuditResponseFixture._
import v1.models.audit.GenericAuditDetail

object GenericAuditDetailFixture {

  private val nino: String = "ZG903729C"
  private val calculationId: String = "calcId"

  val genericAuditDetailJsonSuccess: JsObject = Json.parse(s"""
      |{
      |    "userType": "Agent",
      |    "agentReferenceNumber":"012345678",
      |    "nino": "$nino",
      |    "calculationId" : "$calculationId",
      |    "response":{
      |      "httpStatus": 200,
      |      "body": $body
      |    },
      |    "X-CorrelationId": "a1e8057e-fbbc-47a8-a8b478d9f015c253"
      |}
    """.stripMargin).as[JsObject]

   val genericAuditDetailJsonError: JsObject = Json.parse(s"""
       |{
       |    "userType": "Agent",
       |    "agentReferenceNumber":"012345678",
       |    "nino": "$nino",
       |    "calculationId" : "$calculationId",
       |    "response": $auditResponseJsonWithErrors,
       |    "X-CorrelationId":"a1e8057e-fbbc-47a8-a8b478d9f015c253"
       |}
     """.stripMargin).as[JsObject]

   val genericAuditDetailModelSuccess: GenericAuditDetail = GenericAuditDetail(
    userType = "Agent",
    agentReferenceNumber = Some("012345678"),
    pathParams = Map("nino" -> nino, "calculationId" -> calculationId),
    requestBody = None,
    `X-CorrelationId` = "a1e8057e-fbbc-47a8-a8b478d9f015c253",
    auditResponse = auditResponseModelWithBody
   )

   val genericAuditDetailModelError: GenericAuditDetail = genericAuditDetailModelSuccess.copy(auditResponse = auditResponseModelWithErrors)
}
