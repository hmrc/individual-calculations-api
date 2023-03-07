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

package v3.endpoints

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.HeaderNames.ACCEPT
import play.api.http.Status._
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.test.Helpers.AUTHORIZATION
import support.V3IntegrationBaseSpec
import v3.models.errors._
import v3.stubs.{AuditStub, AuthStub, BackendStub, MtdIdLookupStub}

class RetrieveCalculationControllerISpec extends V3IntegrationBaseSpec {

  private trait Test {
    val nino: String          = "AA123456A"
    val calculationId: String = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"
    def taxYear: String
    def downstreamTaxYear: String

    def downstreamUri: String

    def setupStubs(): StubMapping

    def request: WSRequest = {
      setupStubs()
      buildRequest(uri)
        .withHttpHeaders(
          (ACCEPT, "application/vnd.hmrc.3.0+json"),
          (AUTHORIZATION, "Bearer 123")
        )
    }

    def uri: String = s"/$nino/self-assessment/$taxYear/$calculationId"

    def downstreamResponseBody(canBeFinalised: Boolean): JsValue = Json.parse(
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
         |  "calculation" : {
         |   "endOfYearEstimate": {
         |     "totalAllowancesAndDeductions": 100
         |   },
         |   "reliefs": {
         |       "basicRateExtension": {
         |       "totalBasicRateExtension": 2000.00
         |       }
         |    }
         |  },
         |  "messages" : {}
         |}
        """.stripMargin
    )

    def responseBody(canBeFinalised: Boolean): JsValue = Json.parse(
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
         |  "calculation" : {
         |   "endOfYearEstimate": {
         |     "totalAllowancesAndDeductions": 100
         |   },
         |   "reliefs": {
         |       "basicRateExtension": {
         |       "totalBasicRateExtension": 2000.00
         |       }
         |    }
         |  },
         |  "messages" : {},
         |  "links": [
         |    {
         |      "href": "/individuals/calculations/AA123456A/self-assessment/$taxYear",
         |      "rel": "trigger",
         |      "method": "POST"
         |    },
         |    {
         |      "href": "/individuals/calculations/AA123456A/self-assessment/$taxYear/f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
         |      "rel": "self",
         |      "method": "GET"
         |    }${if (canBeFinalised) {
          s"""
           |,
           |{
           |  "href": "/individuals/calculations/AA123456A/self-assessment/$taxYear/f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c/final-declaration",
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

    def errorBody(code: String): String =
      s"""
         |{
         |  "code": "$code",
         |  "message": "backend message"
         |}
           """.stripMargin

  }

  private trait NonTysTest extends Test {
    def taxYear: String                = "2018-19"
    def downstreamTaxYear: String      = "2018-19"
    override def downstreamUri: String = s"/income-tax/view/calculations/liability/$nino/$calculationId"
  }

  private trait TysIfsTest extends Test {
    def taxYear: String = "2023-24"

    override def downstreamUri: String = s"/income-tax/view/calculations/liability/$downstreamTaxYear/$nino/$calculationId"

    def downstreamTaxYear: String = "23-24"
  }

  "Calling the retrieveCalculation endpoint" should {
    "return a 200 status code" when {
      "a valid request is made" in new NonTysTest {
        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          BackendStub.onSuccess(BackendStub.GET, downstreamUri, Map(), OK, downstreamResponseBody(canBeFinalised = false))
        }
        val response: WSResponse = await(request.get())
        response.status shouldBe OK
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe responseBody(canBeFinalised = false)
      }

      "a valid request is made with a Tax Year Specific tax year" in new TysIfsTest {
        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          BackendStub.onSuccess(BackendStub.GET, downstreamUri, Map(), OK, downstreamResponseBody(canBeFinalised = false))
        }
        val response: WSResponse = await(request.get())
        response.status shouldBe OK
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe responseBody(canBeFinalised = false)
      }

      "a valid request is made and the response can be finalised" in new NonTysTest {
        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          BackendStub.onSuccess(BackendStub.GET, downstreamUri, Map(), OK, downstreamResponseBody(canBeFinalised = true))
        }
        val response: WSResponse = await(request.get())
        response.status shouldBe OK
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe responseBody(canBeFinalised = true)
      }

      "a valid request is made and the response can be finalised with a Tax Year Specific tax year" in new TysIfsTest {
        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          BackendStub.onSuccess(BackendStub.GET, downstreamUri, Map(), OK, downstreamResponseBody(canBeFinalised = true))
        }
        val response: WSResponse = await(request.get())
        response.status shouldBe OK
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe responseBody(canBeFinalised = true)
      }
    }

    "return the correct error code" when {
      def validationErrorTest(requestNino: String,
                              requestTaxYear: String,
                              requestCalculationId: String,
                              expectedStatus: Int,
                              expectedBody: MtdError): Unit = {
        s"validation fails with ${expectedBody.code} error" in new NonTysTest {
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
      "downstream returns a service error" when {
        def serviceErrorTest(downstreamStatus: Int, downstreamCode: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
          s"backend returns an $downstreamCode error and status $downstreamStatus" in new NonTysTest {
            override def setupStubs(): StubMapping = {
              AuditStub.audit()
              AuthStub.authorised()
              MtdIdLookupStub.ninoFound(nino)
              BackendStub.onError(BackendStub.GET, downstreamUri, downstreamStatus, errorBody(downstreamCode))
            }
            val response: WSResponse = await(request.get())
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
            response.header("Content-Type") shouldBe Some("application/json")
          }
        }
        val errors = Seq(
          (BAD_REQUEST, "INVALID_TAXABLE_ENTITY_ID", BAD_REQUEST, NinoFormatError),
          (BAD_REQUEST, "INVALID_CALCULATION_ID", BAD_REQUEST, CalculationIdFormatError),
          (BAD_REQUEST, "INVALID_CORRELATIONID", INTERNAL_SERVER_ERROR, InternalError),
          (BAD_REQUEST, "INVALID_CONSUMERID", INTERNAL_SERVER_ERROR, InternalError),
          (NOT_FOUND, "NO_DATA_FOUND", NOT_FOUND, NotFoundError),
          (INTERNAL_SERVER_ERROR, "SERVER_ERROR", INTERNAL_SERVER_ERROR, InternalError),
          (SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", INTERNAL_SERVER_ERROR, InternalError),
          (NOT_FOUND, "UNMATCHED_STUB_ERROR", BAD_REQUEST, RuleIncorrectGovTestScenarioError)
        )
        val extraTysErrors = Seq(
          (BAD_REQUEST, "INVALID_TAX_YEAR", BAD_REQUEST, TaxYearFormatError),
          (BAD_REQUEST, "INVALID_CORRELATION_ID", INTERNAL_SERVER_ERROR, InternalError),
          (BAD_REQUEST, "INVALID_CONSUMER_ID", INTERNAL_SERVER_ERROR, InternalError),
          (NOT_FOUND, "NOT_FOUND", NOT_FOUND, NotFoundError),
          (UNPROCESSABLE_ENTITY, "TAX_YEAR_NOT_SUPPORTED", BAD_REQUEST, RuleTaxYearNotSupportedError)
        )
        (errors ++ extraTysErrors).foreach(args => (serviceErrorTest _).tupled(args))
      }
    }
  }

}
