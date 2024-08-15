/*
 * Copyright 2024 HM Revenue & Customs
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

import play.api.libs.json.{JsValue, Json}
import shared.models.audit.AuditResponseFixture.{auditResponseModelWithBody, auditResponseModelWithErrors}
import shared.models.audit.FlattenedGenericAuditDetail._
import shared.models.auth.UserDetails
import shared.utils.UnitSpec

class FlattenedGenericAuditDetailSpec extends UnitSpec {

  private val nino: String                         = "ZG903729C"
  private val calculationId: String                = "calcId"
  private val userType: String                     = "Agent"
  private val agentReferenceNumber: Option[String] = Some("012345678")
  private val userDetails: UserDetails             = UserDetails(calculationId, userType, agentReferenceNumber)
  private val pathParams: Map[String, String]      = Map("nino" -> nino, "calculationId" -> calculationId)
  private val itsaStatuses: Option[JsValue]        = Some(Json.obj("field1" -> "value1"))
  private val xCorrId                              = "a1e8057e-fbbc-47a8-a8b478d9f015c253"
  private val versionNumber: String                = "3.0"

  private val flattenedGenericAuditDetailSuccess: FlattenedGenericAuditDetail =
    FlattenedGenericAuditDetail(
      versionNumber = Some(versionNumber),
      userDetails = userDetails,
      params = pathParams,
      futureYears = None,
      history = None,
      itsaStatuses = itsaStatuses,
      `X-CorrelationId` = xCorrId,
      auditResponse = auditResponseModelWithBody
    )

  private val flattenedGenericAuditDetailJsonSuccess: JsValue = Json.parse(
    s"""
       |{
       |   "versionNumber": "$versionNumber",
       |   "userType" : "$userType",
       |   "agentReferenceNumber" : "${agentReferenceNumber.get}",
       |   "nino": "$nino",
       |   "calculationId" : "$calculationId",
       |   "field1":"value1",
       |   "X-CorrelationId": "$xCorrId",
       |   "outcome": "success",
       |   "httpStatusCode": 200
       |}
     """.stripMargin
  )

  private val flattenedGenericAuditDetailErrors: FlattenedGenericAuditDetail =
    FlattenedGenericAuditDetail(
      versionNumber = Some(versionNumber),
      userDetails = userDetails,
      params = pathParams,
      futureYears = None,
      history = None,
      itsaStatuses = itsaStatuses,
      `X-CorrelationId` = xCorrId,
      auditResponse = auditResponseModelWithErrors
    )

  private val flattenedGenericAuditDetailJsonErrors: JsValue = Json.parse(
    s"""
       |{
       |   "versionNumber": "$versionNumber",
       |   "userType" : "$userType",
       |   "agentReferenceNumber" : "${agentReferenceNumber.get}",
       |   "nino": "$nino",
       |   "calculationId" : "$calculationId",
       |   "field1":"value1",
       |   "X-CorrelationId": "$xCorrId",
       |   "outcome": "error",
       |   "httpStatusCode": 400,
       |   "errorCodes": [
       |      "FORMAT_NINO",
       |      "FORMAT_TAX_YEAR"
       |   ]
       |}
     """.stripMargin
  )

  "FlattenedGenericAuditDetailSpec" when {
    "written to JSON" should {
      "produce the expected JsObject" in {
        val result = Json.toJson(flattenedGenericAuditDetailSuccess)
        result shouldBe flattenedGenericAuditDetailJsonSuccess
      }
    }
    "written to JSON (error)" should {
      "produce the expected JsObject" in {
        val result = Json.toJson(flattenedGenericAuditDetailErrors)
        result shouldBe flattenedGenericAuditDetailJsonErrors
      }
    }

  }

}
