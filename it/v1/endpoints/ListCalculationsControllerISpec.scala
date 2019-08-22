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

class ListCalculationsControllerISpec extends IntegrationBaseSpec {

  private trait Test {

    val nino                    = "AA123456A"
    val taxYear: Option[String] = None
    val correlationId           = "X-123"

    def uri: String = s"/$nino/self-assessment"

    def backendUrl: String = uri

    def setupStubs(): StubMapping

    def request: WSRequest = {
      val queryParams: Seq[(String, String)] =
        Seq("taxYear" -> taxYear)
          .collect { case (k, Some(v)) => (k, v) }

      setupStubs()
      buildRequest(uri)
        .addQueryStringParameters(queryParams: _*)
        .withHttpHeaders((ACCEPT, "application/vnd.hmrc.1.0+json"))
    }
  }

  "Calling the list calculations endpoint" should {
    "return a 200 status code" when {

      val successBody = Json.parse("""{
                                     |  "calculations": [
                                     |    {
                                     |      "id": "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
                                     |      "calculationTimestamp": "2019-03-17T09:22:59Z",
                                     |      "type": "inYear",
                                     |      "requestedBy": "hmrc"
                                     |    }
                                     |  ]
                                     |}""".stripMargin)

      "valid request is made with a tax year" in new Test {
        override val taxYear = Some("2018-19")

        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          BackendStub.onSuccess(BackendStub.GET, backendUrl, Map("taxYear" -> "2018-19"), OK, successBody)
        }

        val response: WSResponse = await(request.get)

        response.status shouldBe OK
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe successBody
      }

      "valid request is made without a tax year" in new Test {

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
        def validationErrorTest(requestNino: String, requestTaxYear: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
          s"validation fails with ${expectedBody.code} error" in new Test {

            override val nino: String            = requestNino
            override val taxYear: Option[String] = Some(requestTaxYear)

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
          ("AA1123A", "2017-18", BAD_REQUEST, NinoFormatError),
          ("AA123456A", "20177", BAD_REQUEST, TaxYearFormatError),
          ("AA123456A", "2015-16", BAD_REQUEST, RuleTaxYearNotSupportedError),
          ("AA123456A", "2020-22", BAD_REQUEST, RuleTaxYearRangeExceededError)
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
          (BAD_REQUEST, "FORMAT_TAX_YEAR", BAD_REQUEST, TaxYearFormatError),
          (BAD_REQUEST, "RULE_TAX_YEAR_NOT_SUPPORTED", BAD_REQUEST, RuleTaxYearNotSupportedError),
          (BAD_REQUEST, "RULE_TAX_YEAR_RANGE_EXCEEDED", BAD_REQUEST, RuleTaxYearRangeExceededError),
          (INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", INTERNAL_SERVER_ERROR, DownstreamError),
          (NOT_FOUND, "MATCHING_RESOURCE_NOT_FOUND", NOT_FOUND, NotFoundError)
        )

        input.foreach(args => (serviceErrorTest _).tupled(args))
      }
    }
  }
}
