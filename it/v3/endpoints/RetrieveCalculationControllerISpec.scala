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

package v3.endpoints

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.HeaderNames.ACCEPT
import play.api.http.Status._
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{WSRequest, WSResponse}
import support.V3IntegrationBaseSpec
import v3.models.errors._
import v3.stubs.{AuditStub, AuthStub, BackendStub, MtdIdLookupStub}

class RetrieveCalculationControllerISpec extends V3IntegrationBaseSpec {

  private trait Test {

    val nino: String          = "AA123456A"
    val taxYear: String       = "2018-19"
    val calculationId: String = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"

    def uri: String = s"/$nino/self-assessment/$taxYear/$calculationId"

    def backendUrl: String = s"/income-tax/view/calculations/liability/$nino/$calculationId"

    def setupStubs(): StubMapping

    def request: WSRequest = {

      setupStubs()
      buildRequest(uri)
        .withHttpHeaders((ACCEPT, "application/vnd.hmrc.3.0+json"))
    }

  }

  "Calling the retrieveCalculation endpoint" should {

    "return a 200 status code" when {
      def successBody(canBeFinalised: Boolean): JsValue = Json.parse(
        s"""
          |{
          |  "metadata" : {
          |    "calculationId": "",
          |    "taxYear": 2017,
          |    "requestedBy": "",
          |    "calculationReason": "",
          |    "calculationType": "inYear",
          |    ${if (canBeFinalised) """"intentToCrystallise": true,""" else ""}
          |    "periodFrom": "",
          |    "periodTo": ""
          |  },
          |  "inputs" : {
          |    "personalInformation": {
          |       "identifier": "",
          |       "taxRegime": "UK"
          |    },
          |    "incomeSources": {}
          |  },
          |  "calculation" : {},
          |  "messages" : {}
          |}
        """.stripMargin
      )

      def mtdBody(canBeFinalised: Boolean): JsValue = Json.parse(
        s"""
          |{
          |  "metadata" : {
          |    "calculationId": "",
          |    "taxYear": "2016-17",
          |    "requestedBy": "",
          |    "calculationReason": "",
          |    "calculationType": "inYear",
          |    "intentToSubmitFinalDeclaration": $canBeFinalised,
          |    "finalDeclaration": false,
          |    "periodFrom": "",
          |    "periodTo": ""
          |  },
          |  "inputs" : {
          |    "personalInformation": {
          |       "identifier": "",
          |       "taxRegime": "UK"
          |    },
          |    "incomeSources": {}
          |  },
          |  "calculation" : {},
          |  "messages" : {},
          |  "links": [
          |    {
          |      "href": "/individuals/calculations/AA123456A/self-assessment/2018-19",
          |      "rel": "trigger",
          |      "method": "POST"
          |    },
          |    {
          |      "href": "/individuals/calculations/AA123456A/self-assessment/2018-19/f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
          |      "rel": "self",
          |      "method": "GET"
          |    }${if (canBeFinalised) {
            s"""
             |,
             |{
             |  "href": "/individuals/calculations/AA123456A/self-assessment/2018-19/f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c/final-declaration",
             |  "rel": "submit-final-declaration",
             |  "method": "POST"
             |}
             |""".stripMargin
          } else {
            ""
          }}
          |  ]
          |}
        """.stripMargin
      )

      "a valid request is made" in new Test {
        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          BackendStub.onSuccess(BackendStub.GET, backendUrl, Map(), OK, successBody(canBeFinalised = false))
        }

        val response: WSResponse = await(request.get())

        response.status shouldBe OK
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe mtdBody(canBeFinalised = false)
      }

      "a valid request is made and the response can be finalised" in new Test {
        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          BackendStub.onSuccess(BackendStub.GET, backendUrl, Map(), OK, successBody(canBeFinalised = true))
        }

        val response: WSResponse = await(request.get())

        response.status shouldBe OK
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe mtdBody(canBeFinalised = true)
      }
    }

    "return the correct error code" when {
      def validationErrorTest(requestNino: String,
                              requestTaxYear: String,
                              requestCalculationId: String,
                              expectedStatus: Int,
                              expectedBody: MtdError): Unit = {
        s"validation fails with ${expectedBody.code} error" in new Test {

          override val nino: String          = requestNino
          override val taxYear: String       = requestTaxYear
          override val calculationId: String = requestCalculationId

          override def setupStubs(): StubMapping = {
            AuditStub.audit()
            AuthStub.authorised()
            MtdIdLookupStub.ninoFound(requestNino)
          }

          val response: WSResponse = await(request.get())
          response.status shouldBe expectedStatus
          response.json shouldBe Json.toJson(expectedBody)
          response.header("Content-Type") shouldBe Some("application/json")
        }
      }

      val input = Seq(
        ("AA1123A", "2018-19", "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c", BAD_REQUEST, NinoFormatError),
        ("AA123456A", "20177", "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c", BAD_REQUEST, TaxYearFormatError),
        ("AA123456A", "2016-17", "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c", BAD_REQUEST, RuleTaxYearNotSupportedError),
        ("AA123456A", "2020-22", "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c", BAD_REQUEST, RuleTaxYearRangeInvalidError),
        ("AA123456A", "2017-18", "bad id", BAD_REQUEST, CalculationIdFormatError)
      )

      input.foreach(args => (validationErrorTest _).tupled(args))

      "the backend returns a service error" when {

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

            val response: WSResponse = await(request.get())
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
            response.header("Content-Type") shouldBe Some("application/json")
          }
        }

        val input = Seq(
          (BAD_REQUEST, "INVALID_TAXABLE_ENTITY_ID", BAD_REQUEST, NinoFormatError),
          (BAD_REQUEST, "INVALID_CALCULATION_ID", BAD_REQUEST, CalculationIdFormatError),
          (BAD_REQUEST, "INVALID_CORRELATIONID", INTERNAL_SERVER_ERROR, DownstreamError),
          (BAD_REQUEST, "INVALID_CONSUMERID", INTERNAL_SERVER_ERROR, DownstreamError),
          (NOT_FOUND, "NO_DATA_FOUND", NOT_FOUND, NotFoundError),
          (INTERNAL_SERVER_ERROR, "SERVER_ERROR", INTERNAL_SERVER_ERROR, DownstreamError),
          (SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", INTERNAL_SERVER_ERROR, DownstreamError)
        )

        input.foreach(args => (serviceErrorTest _).tupled(args))
      }
    }
  }

}
