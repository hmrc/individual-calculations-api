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

package v7.retrieveCalculation.def2

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.HeaderNames.ACCEPT
import play.api.http.Status._
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.test.Helpers.AUTHORIZATION
import shared.models.errors._
import shared.services.{AuditStub, AuthStub, DownstreamStub, MtdIdLookupStub}
import shared.support.IntegrationBaseSpec
import v7.retrieveCalculation.def2.model.Def2_CalculationFixture

class Def2_RetrieveCalculationControllerISpec extends IntegrationBaseSpec with Def2_CalculationFixture {

  private trait Test {
    val nino: String              = "ZG903729C"
    val calculationId: String     = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"
    def taxYear: String           = "2024-25"
    def downstreamTaxYear: String = "24-25"

    def downstreamUri: String = s"/income-tax/view/calculations/liability/$downstreamTaxYear/$nino/$calculationId"

    def setupStubs(): StubMapping

    def request: WSRequest = {
      setupStubs()
      buildRequest(s"/$nino/self-assessment/$taxYear/$calculationId")
        .withHttpHeaders(
          (ACCEPT, "application/vnd.hmrc.7.0+json"),
          (AUTHORIZATION, "Bearer 123")
        )
    }

    val downstreamResponseBody: JsValue = calculationDownstreamJson

    val responseBody: JsValue = calculationMtdJson.as[JsObject]

    def errorBody(code: String): String =
      s"""
         |{
         |  "code": "$code",
         |  "message": "backend message"
         |}
           """.stripMargin

  }

  "Calling the retrieveCalculation endpoint" when {
    "successful" should {
      "return a 200 status code" when {
        "a valid request is made" in new Test {
          override def setupStubs(): StubMapping = {
            AuditStub.audit()
            AuthStub.authorised()
            MtdIdLookupStub.ninoFound(nino)
            DownstreamStub.onSuccess(DownstreamStub.GET, downstreamUri, Map(), OK, downstreamResponseBody)
          }

          val response: WSResponse = await(request.get())
          response.status shouldBe OK
          response.header("Content-Type") shouldBe Some("application/json")
          response.json shouldBe responseBody
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
          ("AA1123A", "2024-25", "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c", BAD_REQUEST, NinoFormatError),
          ("ZG903729C", "20244", "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c", BAD_REQUEST, TaxYearFormatError),
          ("ZG903729C", "2023-24", "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c", BAD_REQUEST, RuleTaxYearNotSupportedError),
          ("ZG903729C", "2024-26", "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c", BAD_REQUEST, RuleTaxYearRangeInvalidError),
          ("ZG903729C", "2024-25", "bad id", BAD_REQUEST, CalculationIdFormatError)
        )
        input.foreach(args => (validationErrorTest _).tupled(args))
        "downstream returns a service error" when {
          def serviceErrorTest(downstreamStatus: Int, downstreamCode: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
            s"backend returns an $downstreamCode error and status $downstreamStatus" in new Test {
              override def setupStubs(): StubMapping = {
                AuditStub.audit()
                AuthStub.authorised()
                MtdIdLookupStub.ninoFound(nino)
                DownstreamStub.onError(DownstreamStub.GET, downstreamUri, downstreamStatus, errorBody(downstreamCode))
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
}
