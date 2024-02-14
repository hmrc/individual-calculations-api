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

import api.models.auth.UserDetails
import play.api.libs.json.{JsValue, Json}
import support.UnitSpec

class GenericAuditDetailOldSpec extends UnitSpec {

  private val nino: String                = "SOMENINO"
  private val calculationId: String       = "calcId"
  private val userType: String            = "Agent"
  private val agentReferenceNumber        = "ARN"
  private val params: Map[String, String] = Map("nino" -> nino)
  private val xCorrId: String             = "corr-id"
  private val httpStatusCode              = 123

  "GenericAuditDetail" when {
    "written to JSON" should {

      def genericAuditDetail(requestBody: Option[JsValue] = None, auditResponse: AuditResponse): GenericAuditDetailOld =
        GenericAuditDetailOld(
          userType = userType,
          agentReferenceNumber = Some(agentReferenceNumber),
          params = params,
          requestBody = requestBody,
          `X-CorrelationId` = xCorrId,
          versionNumber = "3.0",
          auditResponse
        )

      "work when there are no errors" in {
        Json.toJson(genericAuditDetail(auditResponse = AuditResponse(httpStatusCode, Right(None)))) shouldBe Json.parse(
          s"""
             |{
             |   "userType": "$userType",
             |   "agentReferenceNumber": "$agentReferenceNumber",
             |   "nino": "$nino",
             |   "X-CorrelationId": "$xCorrId",
             |   "httpStatusCode": $httpStatusCode,
             |   "versionNumber": "3.0",
             |   "response": "success"
             |}""".stripMargin
        )
      }

      "work when there are no errors and a response body" in {
        Json.toJson(genericAuditDetail(auditResponse =
          AuditResponse(httpStatusCode, Right(Some(Json.parse(s"""{ "calculationId": "$calculationId" }""")))))) shouldBe Json.parse(
          s"""
             |{
             |   "userType": "$userType",
             |   "agentReferenceNumber": "$agentReferenceNumber",
             |   "nino": "$nino",
             |   "calculationId": "$calculationId",
             |   "X-CorrelationId": "$xCorrId",
             |   "httpStatusCode": $httpStatusCode,
             |   "versionNumber": "3.0",
             |   "response": "success"
             |}""".stripMargin
        )
      }

      "work when there are errors" in {
        Json.toJson(genericAuditDetail(auditResponse = AuditResponse(httpStatusCode, Left(Seq(AuditError("CODE1"), AuditError("CODE2")))))) shouldBe
          Json.parse(
            s"""
               |{
               |   "userType": "$userType",
               |   "agentReferenceNumber": "$agentReferenceNumber",
               |   "nino": "$nino",
               |   "X-CorrelationId": "$xCorrId",
               |   "httpStatusCode": $httpStatusCode,
               |   "versionNumber": "3.0",
               |   "response": "error",
               |   "errorCodes": [
               |      "CODE1", "CODE2"
               |   ]
               |}""".stripMargin
          )
      }

      "work when there are no errors and a request body" in {
        Json.toJson(
          genericAuditDetail(
            requestBody = Some(Json.parse("""{ "field1": "value1" }""")),
            auditResponse = AuditResponse(httpStatusCode, Right(None)))) shouldBe Json.parse(
          s"""
             |{
             |   "userType": "$userType",
             |   "agentReferenceNumber": "$agentReferenceNumber",
             |   "nino": "$nino",
             |   "X-CorrelationId": "$xCorrId",
             |   "httpStatusCode": $httpStatusCode,
             |   "versionNumber": "3.0",
             |   "response": "success",
             |   "request": {
             |      "field1": "value1"
             |   }
             |}""".stripMargin
        )
      }
    }

    "constructed from the companion object" must {
      "create the correct instance" in {
        val auditResponse = AuditResponse(httpStatusCode, Right(Some(Json.parse(s"""{ "property": "value" }"""))))
        val requestBody   = Some(Json.parse(s"""{ "property": "value" }"""))

        GenericAuditDetailOld(
          userDetails = UserDetails("ignored", userType, Some(agentReferenceNumber)),
          params = params,
          requestBody = requestBody,
          `X-CorrelationId` = xCorrId,
          auditResponse = auditResponse
        ) shouldBe
          GenericAuditDetailOld(
            userType = userType,
            agentReferenceNumber = Some(agentReferenceNumber),
            params = params,
            requestBody = requestBody,
            `X-CorrelationId` = xCorrId,
            versionNumber = "3.0",
            auditResponse
          )
      }
    }
  }

}
