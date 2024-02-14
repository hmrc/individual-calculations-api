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

package api.models.audit

import play.api.http.Status.{BAD_REQUEST, OK}
import play.api.libs.json.{JsValue, Json}
import support.UnitSpec

class GenericAuditDetailSpec extends UnitSpec {

  val auditErrors: Seq[AuditError] = Seq(AuditError(errorCode = "FORMAT_NINO"), AuditError(errorCode = "FORMAT_TAX_YEAR"))
  val body: JsValue                = Json.parse("""{ "aField" : "aValue" }""")

  val nino: String                         = "ZG903729C"
  val taxYear: String                      = "2021-22"
  val userType: String                     = "Agent"
  val agentReferenceNumber: Option[String] = Some("012345678")
  val versionNumber                        = "3.0"
  val pathParams: Map[String, String]      = Map("nino" -> nino, "taxYear" -> taxYear)
  val requestBody: Option[JsValue]         = None
  val xCorrId                              = "a1e8057e-fbbc-47a8-a8b478d9f015c253"

  val auditResponseModelWithBody: AuditResponse =
    AuditResponse(
      httpStatus = OK,
      response = Right(Some(body))
    )

  val auditDetailModelSuccess: GenericAuditDetail =
    GenericAuditDetail(
      userType = userType,
      agentReferenceNumber = agentReferenceNumber,
      versionNumber = versionNumber,
      params = pathParams,
      requestBody = requestBody,
      `X-CorrelationId` = xCorrId,
      auditResponse = auditResponseModelWithBody
    )

  val auditResponseModelWithErrors: AuditResponse =
    AuditResponse(
      httpStatus = BAD_REQUEST,
      response = Left(auditErrors)
    )

  val auditDetailModelError: GenericAuditDetail =
    auditDetailModelSuccess.copy(
      auditResponse = auditResponseModelWithErrors
    )

  val auditDetailJsonSuccess: JsValue = Json.parse(
    s"""
       |{
       |   "userType" : "$userType",
       |   "agentReferenceNumber" : "${agentReferenceNumber.get}",
       |   "versionNumber":"$versionNumber",
       |   "nino" : "$nino",
       |   "taxYear" : "$taxYear",
       |   "response":{
       |     "httpStatus": ${auditResponseModelWithBody.httpStatus},
       |     "body": ${auditResponseModelWithBody.body.get}
       |   },
       |   "X-CorrelationId": "$xCorrId"
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

  val auditDetailJsonError: JsValue = Json.parse(
    s"""
       |{
       |   "userType" : "$userType",
       |   "agentReferenceNumber" : "${agentReferenceNumber.get}",
       |   "versionNumber":"$versionNumber",
       |   "nino": "$nino",
       |   "taxYear" : "$taxYear",
       |   "response": $auditResponseJsonWithErrors,
       |   "X-CorrelationId": "$xCorrId"
       |}
     """.stripMargin
  )

  "GenericAuditDetail" when {
    "written to JSON (success)" should {
      "produce the expected JsObject" in {
        Json.toJson(auditDetailModelSuccess) shouldBe auditDetailJsonSuccess
      }
    }

    "written to JSON (error)" should {
      "produce the expected JsObject" in {
        Json.toJson(auditDetailModelError) shouldBe auditDetailJsonError
      }
    }
  }

}
