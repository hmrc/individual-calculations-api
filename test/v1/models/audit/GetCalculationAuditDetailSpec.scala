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

import play.api.libs.json.Json
import support.UnitSpec
import v1.models.errors.{CalculationIdFormatError, NinoFormatError}

class GetCalculationAuditDetailSpec extends UnitSpec {

  val nino = "ZG903729C"
  val calculationId = "calcId"

  "writes" must {
    "work" when {
      "success response" in {
        val json = Json.parse(s"""{
                                 |    "userType": "Agent",
                                 |    "agentReferenceNumber":"012345678",
                                 |    "nino": "$nino",
                                 |    "calculationId" : "$calculationId",
                                 |    "response":{
                                 |      "httpStatus": 200,
                                 |      "body":{
                                 |        "foo": "value"
                                 |      }
                                 |    },
                                 |    "X-CorrelationId": "a1e8057e-fbbc-47a8-a8b478d9f015c253"
                                 |}""".stripMargin)

        Json.toJson(
          GetCalculationAuditDetail(
            userType = "Agent",
            agentReferenceNumber = Some("012345678"),
            nino = nino,
            calculationId = calculationId,
            `X-CorrelationId` = "a1e8057e-fbbc-47a8-a8b478d9f015c253",
            response = AuditResponse(
              200,
              errors = None,
              body = Some(Json.parse(s"""{
                                        |  "foo": "value"
                                        |}""".stripMargin))
            )
          )) shouldBe json
      }
    }
    "work" when {
      "error response" in {
        val json = Json.parse(s"""
                                 |{
                                 |    "userType": "Agent",
                                 |    "agentReferenceNumber":"012345678",
                                 |    "nino": "$nino",
                                 |    "calculationId" : "$calculationId",
                                 |    "response": {
                                 |      "httpStatus": 400,
                                 |      "errors": [
                                 |        {
                                 |          "errorCode":"FORMAT_CALC_ID"
                                 |        }
                                 |      ]
                                 |    },
                                 |    "X-CorrelationId":"a1e8057e-fbbc-47a8-a8b478d9f015c253"
                                 |  }
                                 |""".stripMargin)

        Json.toJson(
          GetCalculationAuditDetail(
            userType = "Agent",
            agentReferenceNumber = Some("012345678"),
            nino = nino,
            calculationId = calculationId,
            `X-CorrelationId` = "a1e8057e-fbbc-47a8-a8b478d9f015c253",
            response = AuditResponse(
              400,
              errors = Some(Seq(AuditError(CalculationIdFormatError.code))),
              body = None
            )
          )) shouldBe json
      }
    }
  }
}
