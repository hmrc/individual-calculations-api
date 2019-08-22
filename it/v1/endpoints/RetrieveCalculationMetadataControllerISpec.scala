/*
 * Copyright 2018 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIED OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package v1.endpoints

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.HeaderNames.ACCEPT
import play.api.http.Status._
import play.api.libs.json.Json
import play.api.libs.ws.{ WSRequest, WSResponse }
import support.IntegrationBaseSpec
import v1.models.errors._
import v1.stubs.{ AuditStub, AuthStub, BackendStub, MtdIdLookupStub }

class RetrieveCalculationMetadataControllerISpec extends IntegrationBaseSpec {

  private trait Test {

    val nino          = "AA123456A"
    val correlationId = "X-123"
    val calcId        = "12345678"

    def uri: String = s"/$nino/self-assessment/$calcId"

    def backendUrl: String = uri

    def setupStubs(): StubMapping

    def request: WSRequest = {
      setupStubs()
      buildRequest(uri)
        .withHttpHeaders((ACCEPT, "application/vnd.hmrc.1.0+json"))
    }
  }

  "Calling the retrieve calculation metadata endpoint" should {
    "return a 200 status code" when {

      val successBody = Json.parse("""
                                     |{
                                     |  "metadata": {
                                     |    "id": "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
                                     |    "taxYear": "2018-19",
                                     |    "requestedBy": "customer",
                                     |    "calculationReason": "customerRequest",
                                     |    "calculationTimestamp": "2019-11-15T09:35:15.094Z",
                                     |    "calculationType": "crystallisation",
                                     |    "intentToCrystallise": true,
                                     |    "crystallised": false,
                                     |    "calculationErrorCount": 123
                                     |  }
                                     |}""".stripMargin)

      "valid request is made" in new Test {
        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          BackendStub.onSuccess(BackendStub.GET, backendUrl, OK, successBody)
        }

        val response: WSResponse = await(request.get)

        response.status shouldBe OK
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe successBody
      }
    }

    "return error according to spec" when {
      "validation error" when {
        def validationErrorTest(requestNino: String, requestCalcId: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
          s"validation fails with ${expectedBody.code} error" in new Test {

            override val nino: String   = requestNino
            override val calcId: String = requestCalcId

            override def setupStubs(): StubMapping = {
              AuditStub.audit()
              AuthStub.authorised()
              MtdIdLookupStub.ninoFound(nino)
            }

            val response: WSResponse = await(request.get)
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
          }
        }

        val input = Seq(
          ("AA1123A", "12345678", BAD_REQUEST, NinoFormatError),
          ("AA123456A", "AAAAAAA", BAD_REQUEST, CalculationIdFormatError)
        )

        input.foreach(args => (validationErrorTest _).tupled(args))
      }

      "backend service error" when {

        def errorBody(code: String): String =
          s"""{
             |  "code": "$code",
             |  "message": "backend message"
             |}""".stripMargin

        def serviceErrorTest(backendStatus: Int, backendCode: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
          s"backend returns an $backendCode error and status $backendStatus" in new Test {

            override def setupStubs(): StubMapping = {
              AuditStub.audit()
              AuthStub.authorised()
              MtdIdLookupStub.ninoFound(nino)
              BackendStub.onError(BackendStub.GET, backendUrl, backendStatus, errorBody(backendCode))
            }

            val response: WSResponse = await(request.get)
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
          }
        }

        val input = Seq(
          (BAD_REQUEST, "FORMAT_NINO", BAD_REQUEST, NinoFormatError),
          (BAD_REQUEST, "FORMAT_CALC_ID", BAD_REQUEST, CalculationIdFormatError),
          (NOT_FOUND, "MATCHING_RESOURCE_NOT_FOUND", NOT_FOUND, NotFoundError),
          (INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", INTERNAL_SERVER_ERROR, DownstreamError)
        )

        input.foreach(args => (serviceErrorTest _).tupled(args))
      }
    }
  }
}
