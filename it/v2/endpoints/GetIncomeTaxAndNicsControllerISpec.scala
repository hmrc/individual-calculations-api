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

package v2.endpoints

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.HeaderNames.ACCEPT
import play.api.http.Status._
import play.api.libs.json.{JsObject, Json}
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.test.Helpers.AUTHORIZATION
import support.V2IntegrationBaseSpec
import v2.fixtures.getIncomeTaxAndNics.IncomeTaxAndNicsResponseFixture._
import v2.models.errors._
import v2.stubs.{AuditStub, AuthStub, BackendStub, MtdIdLookupStub}

class GetIncomeTaxAndNicsControllerISpec extends V2IntegrationBaseSpec {

  private trait Test {

    val nino: String          = "AA123456A"
    val correlationId: String = "X-123"
    val calcId: String        = fixtureCalculationId

    val linksJson: JsObject = Json
      .parse(
        s"""
         |{
         |   "links":[
         |      {
         |         "href":"/individuals/calculations/$nino/self-assessment/$calcId",
         |         "method":"GET",
         |         "rel":"metadata"
         |      },
         |      {
         |         "href":"/individuals/calculations/$nino/self-assessment/$calcId/income-tax-nics-calculated",
         |         "method":"GET",
         |         "rel":"self"
         |      }
         |   ]
         |}
        """.stripMargin
      )
      .as[JsObject]

    def uri: String = s"/$nino/self-assessment/$calcId/income-tax-nics-calculated"

    def backendUrl: String = s"/$nino/self-assessment/$calcId"

    def setupStubs(): StubMapping

    def request: WSRequest = {
      setupStubs()
      buildRequest(uri)
        .withHttpHeaders(
          (ACCEPT, "application/vnd.hmrc.2.0+json"),
          (AUTHORIZATION, "Bearer 123")
        )
    }

  }

  "Calling the get income tax calculation endpoint" should {
    "return a 200 status code" when {
      "valid request is made" in new Test {
        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          BackendStub.onSuccess(BackendStub.GET, backendUrl, OK, incomeTaxAndNicsResponseTopLevelJson)
        }

        val response: WSResponse = await(request.get)

        response.status shouldBe OK
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe incomeTaxNicsResponseJson.as[JsObject].deepMerge(linksJson)
      }
    }

    "return 403" when {
      "errors exist" in new Test {
        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          BackendStub.onSuccess(BackendStub.GET, backendUrl, OK, errorResponseTopLevelJson)
        }

        val response: WSResponse = await(request.get)

        response.status shouldBe FORBIDDEN
        response.json shouldBe Json.toJson(RuleCalculationErrorMessagesExist)
        response.header("Content-Type") shouldBe Some("application/json")
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
            response.header("Content-Type") shouldBe Some("application/json")
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
          s"""
             |{
             |  "code": "$code",
             |  "message": "backend message"
             |}
           """.stripMargin

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
            response.header("Content-Type") shouldBe Some("application/json")
          }
        }

        val input = Seq(
          (BAD_REQUEST, "FORMAT_NINO", BAD_REQUEST, NinoFormatError),
          (BAD_REQUEST, "FORMAT_CALC_ID", BAD_REQUEST, CalculationIdFormatError),
          (NOT_FOUND, "MATCHING_RESOURCE_NOT_FOUND", NOT_FOUND, NotFoundError),
          (INTERNAL_SERVER_ERROR, "SERVER_ERROR", INTERNAL_SERVER_ERROR, DownstreamError),
          (SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", INTERNAL_SERVER_ERROR, DownstreamError),
          (BAD_REQUEST, "RULE_INCORRECT_GOV_TEST_SCENARIO", BAD_REQUEST, RuleIncorrectGovTestScenarioError)
        )

        input.foreach(args => (serviceErrorTest _).tupled(args))
      }
    }
  }

}
