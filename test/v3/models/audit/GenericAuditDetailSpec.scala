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

package v3.models.audit

import play.api.libs.json.{JsValue, Json}
import support.UnitSpec
import v3.models.auth.UserDetails

class GenericAuditDetailSpec extends UnitSpec {

  val nino: String                    = "SOMENINO"
  val calculationId: String           = "calcId"
  val userType: String                = "Agent"
  val agentReferenceNumber            = "ARN"
  val userDetails: UserDetails        = UserDetails("ignoredMtdId", userType, Some(agentReferenceNumber))
  val pathParams: Map[String, String] = Map("nino" -> nino)
  val requestBody: Option[JsValue]    = None
  val xCorrId: String                 = "corr-id"
  val httpStatusCode                  = 123

  "GenericAuditDetail" when {
    "written to JSON" should {
      def genericAuditDetailModel(requestBody: Option[JsValue] = None, errorCodes: Option[Seq[String]] = None): GenericAuditDetail = {
        GenericAuditDetail(
          userType = userType,
          agentReferenceNumber = Some(agentReferenceNumber),
          params = pathParams,
          requestBody = requestBody,
          `X-CorrelationId` = xCorrId,
          versionNumber = "3.0",
          httpStatusCode = httpStatusCode,
          calculationId = Some(calculationId),
          errorCodes = errorCodes
        )
      }

      "work when there are no errors" in {
        Json.toJson(genericAuditDetailModel()) shouldBe Json.parse(
          s"""
             |{
             |   "userType": "$userType",
             |   "agentReferenceNumber": "$agentReferenceNumber",
             |   "calculationId": "$calculationId",
             |   "nino": "$nino",
             |   "X-CorrelationId": "$xCorrId",
             |   "httpStatusCode": $httpStatusCode,
             |   "versionNumber": "3.0",
             |   "response": "success"
             |}
     """.stripMargin
        )
      }

      "work when there are errors" in {
        Json.toJson(genericAuditDetailModel(errorCodes = Some(Seq("CODE1", "CODE2")))) shouldBe
          Json.parse(
            s"""
               |{
               |   "userType": "$userType",
               |   "agentReferenceNumber": "$agentReferenceNumber",
               |   "calculationId": "$calculationId",
               |   "nino": "$nino",
               |   "X-CorrelationId": "$xCorrId",
               |   "httpStatusCode": $httpStatusCode,
               |   "versionNumber": "3.0",
               |   "response": "error",
               |   "errorCodes": [
               |      "CODE1", "CODE2"
               |   ]
               |}
     """.stripMargin
          )
      }

      "work when there is a request body" in {
        Json.toJson(genericAuditDetailModel(requestBody = Some(Json.parse("""{ "field1": "value1" }""".stripMargin)))) shouldBe
          Json.parse(
            s"""
               |{
               |   "userType": "$userType",
               |   "agentReferenceNumber": "$agentReferenceNumber",
               |   "calculationId": "$calculationId",
               |   "nino": "$nino",
               |   "X-CorrelationId": "$xCorrId",
               |   "httpStatusCode": $httpStatusCode,
               |   "versionNumber": "3.0",
               |   "response": "success",
               |   "request": {
               |      "field1": "value1"
               |   }
               |}
     """.stripMargin
          )
      }
    }

    "constructed from companion object apply" when {
      "there are no errors in the auditResponse" when {
        "there is a calculationId in the response JSON" must {
          "include it in the model" in {
            GenericAuditDetail(
              userDetails = userDetails,
              params = pathParams,
              requestBody = requestBody,
              `X-CorrelationId` = xCorrId,
              auditResponse = AuditResponse(
                httpStatus = httpStatusCode,
                errors = None,
                body = Some(Json.parse(s"""{ "calculationId": "$calculationId" }""".stripMargin)))
            ) shouldBe
              GenericAuditDetail(
                userType = userType,
                agentReferenceNumber = Some(agentReferenceNumber),
                params = pathParams,
                requestBody = requestBody,
                `X-CorrelationId` = xCorrId,
                versionNumber = "3.0",
                httpStatusCode = httpStatusCode,
                calculationId = Some(calculationId),
                errorCodes = None
              )
          }
        }

        "there is other data in the response" must {
          "ignore it" in {
            GenericAuditDetail(
              userDetails = userDetails,
              params = pathParams,
              requestBody = requestBody,
              `X-CorrelationId` = xCorrId,
              auditResponse = AuditResponse(
                httpStatus = httpStatusCode,
                errors = None,
                body = Some(Json.parse(s"""{ "someOtherProperty": "ignoredValue" }""".stripMargin)))
            ) shouldBe
              GenericAuditDetail(
                userType = userType,
                agentReferenceNumber = Some(agentReferenceNumber),
                params = pathParams,
                requestBody = requestBody,
                `X-CorrelationId` = xCorrId,
                versionNumber = "3.0",
                httpStatusCode = httpStatusCode,
                calculationId = None,
                errorCodes = None
              )
          }
        }
      }

      "there are errors in the auditResponse" must {
        "include them in the model" in {
          GenericAuditDetail(
            userDetails = userDetails,
            params = pathParams,
            requestBody = requestBody,
            `X-CorrelationId` = xCorrId,
            auditResponse = AuditResponse(httpStatus = httpStatusCode, errors = Some(Seq(AuditError("CODE1"), AuditError("CODE2"))), body = None)
          ) shouldBe
            GenericAuditDetail(
              userType = userType,
              agentReferenceNumber = Some(agentReferenceNumber),
              params = pathParams,
              requestBody = requestBody,
              `X-CorrelationId` = xCorrId,
              versionNumber = "3.0",
              httpStatusCode = httpStatusCode,
              calculationId = None,
              errorCodes = Some(Seq("CODE1", "CODE2"))
            )
        }
      }

      "there is an empty list of errors in the auditResponse" must {
        "treat as success" in {
          GenericAuditDetail(
            userDetails = userDetails,
            params = pathParams,
            requestBody = requestBody,
            `X-CorrelationId` = xCorrId,
            auditResponse = AuditResponse(httpStatus = httpStatusCode, errors = Some(Nil), body = None)
          ) shouldBe
            GenericAuditDetail(
              userType = userType,
              agentReferenceNumber = Some(agentReferenceNumber),
              params = pathParams,
              requestBody = requestBody,
              `X-CorrelationId` = xCorrId,
              versionNumber = "3.0",
              httpStatusCode = httpStatusCode,
              calculationId = None,
              errorCodes = None
            )
        }
      }
    }

  }

}
