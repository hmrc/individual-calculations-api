/*
 * Copyright 2021 HM Revenue & Customs
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

package v1.endpoints

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.HeaderNames.ACCEPT
import play.api.http.Status._
import play.api.libs.json.Json
import play.api.libs.ws.{WSRequest, WSResponse}
import support.IntegrationBaseSpec
import v1.models.errors._
import v1.stubs.{AuditStub, AuthStub, BackendStub, MtdIdLookupStub}

class TriggerCalculationControllerISpec extends IntegrationBaseSpec {

  private trait Test {

    val nino = "AA123456A"
    val taxYear: String = "2018-19"
    val correlationId = "X-123"

    def uri: String = s"/$nino/self-assessment"

    def backendUrl: String = uri

    def setupStubs(): StubMapping

    def request: WSRequest = {

      setupStubs()
      buildRequest(uri)
        .withHttpHeaders((ACCEPT, "application/vnd.hmrc.1.0+json"))
    }
  }

  "Calling the triggerCalculation endpoint" should {

    "return a 202 status code" when {

      val successBody = Json.parse("""{
          | "id" : "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
          | "links":[
          |   {
          |   "href":"/individuals/calculations/AA123456A/self-assessment/f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
          |   "method":"GET",
          |   "rel":"self"
          |     }
          |   ]
          |}""".stripMargin)

      "a valid request is made" in new Test {
        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          BackendStub.onSuccess(BackendStub.POST, backendUrl, Map(), ACCEPTED, successBody)
        }

        val response: WSResponse = await(request.post(Json.parse("""{"taxYear" : "2018-19"}""")))

        response.status shouldBe ACCEPTED
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe successBody
      }
    }

    "return the correct error code" when {
      def validationErrorTest(requestNino: String, requestBody: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
        s"validation fails with ${expectedBody.code} error" in new Test {

          override val nino: String = requestNino

          override def setupStubs(): StubMapping = {
            AuditStub.audit()
            AuthStub.authorised()
            MtdIdLookupStub.ninoFound(requestNino)
          }

          val response: WSResponse = await(request.post(Json.parse(requestBody)))
          response.status shouldBe expectedStatus
          response.json shouldBe Json.toJson(expectedBody)
          response.header("Content-Type") shouldBe Some("application/json")
        }
      }

      val input = Seq(
        ("AA1123A", """{"taxYear" : "2017-18"}""", BAD_REQUEST, NinoFormatError),
        ("AA123456A", """{"taxYear" : "20177"}""", BAD_REQUEST, TaxYearFormatError),
        ("AA123456A", """{"taxYear" : "2015-16"}""", BAD_REQUEST, RuleTaxYearNotSupportedError),
        ("AA123456A", """{"taxYear" : "2020-22"}""", BAD_REQUEST, RuleTaxYearRangeInvalidError),
        ("AA123456A", """{"invalid" : "2017-18"}""", BAD_REQUEST, RuleIncorrectOrEmptyBodyError)
      )

      input.foreach(args => (validationErrorTest _).tupled(args))

      "validation fails due to an empty json body" in new Test {
        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
        }

        val response: WSResponse = await(request.post(Json.obj()))
        response.status shouldBe BAD_REQUEST
        response.json shouldBe Json.toJson(RuleIncorrectOrEmptyBodyError)
        response.header("Content-Type") shouldBe Some("application/json")
      }

      "the backend returns a service error" when {

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
              BackendStub.onError(BackendStub.POST, backendUrl, backendStatus, errorBody(backendCode))
            }

            val response: WSResponse = await(request.post(Json.parse("""{"taxYear" : "2018-19"}""")))
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
            response.header("Content-Type") shouldBe Some("application/json")
          }
        }

        val input = Seq(
          (BAD_REQUEST, "FORMAT_NINO", BAD_REQUEST, NinoFormatError),
          (BAD_REQUEST, "FORMAT_TAX_YEAR", BAD_REQUEST, TaxYearFormatError),
          (FORBIDDEN, "RULE_NO_INCOME_SUBMISSIONS_EXIST", FORBIDDEN, RuleNoIncomeSubmissionsExistError),
          (BAD_REQUEST, "RULE_TAX_YEAR_NOT_SUPPORTED", BAD_REQUEST, RuleTaxYearNotSupportedError),
          (BAD_REQUEST, "RULE_TAX_YEAR_RANGE_INVALID", BAD_REQUEST, RuleTaxYearRangeInvalidError),
          (BAD_REQUEST, "RULE_INCORRECT_OR_EMPTY_BODY_SUBMITTED", BAD_REQUEST, RuleIncorrectOrEmptyBodyError),
          (INTERNAL_SERVER_ERROR, "SERVER_ERROR", INTERNAL_SERVER_ERROR, DownstreamError),
          (SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", INTERNAL_SERVER_ERROR, DownstreamError)
        )

        input.foreach(args => (serviceErrorTest _).tupled(args))
      }
    }
  }
}
