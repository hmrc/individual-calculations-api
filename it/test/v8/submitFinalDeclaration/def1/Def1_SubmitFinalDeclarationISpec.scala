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

package v8.submitFinalDeclaration.def1

import api.errors._
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.Status._
import play.api.libs.json._
import play.api.libs.ws._
import play.api.test.Helpers.{ACCEPT, AUTHORIZATION}
import shared.models.domain.TaxYear
import shared.models.errors._
import shared.services._
import shared.support.IntegrationBaseSpec
import play.api.libs.ws.WSBodyWritables.writeableOf_WsBody

class Def1_SubmitFinalDeclarationISpec extends IntegrationBaseSpec {

  "Calling the submit final declaration endpoint" should {
    "return a 204 status code" when {
      "a valid request is made pre tax year 2025-26" in new TestFinalDeclaration {

        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          DownstreamStub.onSuccess(DownstreamStub.POST, downstreamUri, NO_CONTENT, Json.obj())
        }

        val response: WSResponse = await(request.post(EmptyBody))

        response.status shouldBe NO_CONTENT
      }
      "a valid request is made for Final Declaration post tax year 2025-26" in new TestFinalDeclaration {
        override val taxYear: String       = "2025-26"
        override def downstreamUri: String = s"/income-tax/$downstreamTaxYear/calculation/$nino/$calculationId/DF/confirm"

        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          DownstreamStub.onSuccess(DownstreamStub.POST, downstreamUri, NO_CONTENT, Json.obj())
        }

        val response: WSResponse = await(request.post(EmptyBody))

        response.status shouldBe NO_CONTENT
      }
      "a valid request is made for Confirm Amendment post tax year 2025-26" in new TestConfirmAmendment {
        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          DownstreamStub.onSuccess(DownstreamStub.POST, downstreamUri, NO_CONTENT, Json.obj())
        }

        val response: WSResponse = await(request.post(EmptyBody))

        response.status shouldBe NO_CONTENT
      }

    }

    "return the correct error code" when {
      def validationErrorTest(requestNino: String,
                              requestTaxYear: String,
                              requestCalculationId: String,
                              requestCalculationType: String,
                              expectedStatus: Int,
                              expectedBody: MtdError): Unit = {
        s"validation fails with ${expectedBody.code} error" in new TestFinalDeclaration {

          override val nino: String            = requestNino
          override val taxYear: String         = requestTaxYear
          override val calculationId: String   = requestCalculationId
          override val calculationType: String = requestCalculationType

          override def setupStubs(): StubMapping = {
            AuditStub.audit()
            AuthStub.authorised()
            MtdIdLookupStub.ninoFound(requestNino)
          }

          val response: WSResponse = await(request.post(EmptyBody))
          response.status shouldBe expectedStatus
          response.json shouldBe Json.toJson(expectedBody)
          response.header("Content-Type") shouldBe Some("application/json")
        }
      }

      val input = List(
        ("AA1123A", "2018-19", "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c", "final-declaration", BAD_REQUEST, NinoFormatError),
        ("ZG903729C", "201number77", "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c", "final-declaration", BAD_REQUEST, TaxYearFormatError),
        ("ZG903729C", "2020-22", "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c", "final-declaration", BAD_REQUEST, RuleTaxYearRangeInvalidError),
        ("ZG903729C", "2017-18", "bad id", "final-declaration", BAD_REQUEST, CalculationIdFormatError),
        ("ZG903729C", "2017-18", "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c", "invalid-calculation-type", BAD_REQUEST, FormatCalculationTypeError),
        ("ZG903729C", "2017-18", "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c", "confirm-amendment", BAD_REQUEST, RuleSubmissionFailedError)
      )

      input.foreach(args => validationErrorTest.tupled(args))

      "the backend returns a service error" when {

        def errorBody(code: String): String =
          s"""
             |{
             |  "code": "$code",
             |  "message": "backend message"
             |}
           """.stripMargin

        def serviceErrorTest(backendStatus: Int, backendCode: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
          s"backend returns an $backendCode error and status $backendStatus" in new TestFinalDeclaration {

            override def setupStubs(): StubMapping = {
              AuditStub.audit()
              AuthStub.authorised()
              MtdIdLookupStub.ninoFound(nino)
              DownstreamStub.onError(DownstreamStub.POST, downstreamUri, backendStatus, errorBody(backendCode))
            }

            val response: WSResponse = await(request.post(EmptyBody))
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
            response.header("Content-Type") shouldBe Some("application/json")
          }
        }

        val errors = Seq(
          (BAD_REQUEST, "INVALID_TAXABLE_ENTITY_ID", BAD_REQUEST, NinoFormatError),
          (BAD_REQUEST, "INVALID_TAX_YEAR", BAD_REQUEST, TaxYearFormatError),
          (BAD_REQUEST, "INVALID_CALCULATION_ID", BAD_REQUEST, CalculationIdFormatError),
          (BAD_REQUEST, "INVALID_CALCULATION_TYPE", INTERNAL_SERVER_ERROR, InternalError),
          (BAD_REQUEST, "INVALID_CORRELATION_ID", INTERNAL_SERVER_ERROR, InternalError),
          (NOT_FOUND, "NOT_FOUND", NOT_FOUND, NotFoundError),
          (CONFLICT, "INCOME_SOURCES_CHANGED", BAD_REQUEST, RuleIncomeSourcesChangedError),
          (CONFLICT, "RECENT_SUBMISSIONS_EXIST", BAD_REQUEST, RuleRecentSubmissionsExistError),
          (CONFLICT, "RESIDENCY_CHANGED", BAD_REQUEST, RuleResidencyChangedError),
          (CONFLICT, "FINAL_DECLARATION_RECEIVED", BAD_REQUEST, RuleFinalDeclarationReceivedError),
          (UNPROCESSABLE_ENTITY, "OUTSIDE_AMENDMENT_WINDOW", BAD_REQUEST, RuleOutsideAmendmentWindowError),
          (UNPROCESSABLE_ENTITY, "INVALID_INCOME_SOURCES", BAD_REQUEST, RuleIncomeSourcesInvalidError),
          (UNPROCESSABLE_ENTITY, "INCOME_SUBMISSIONS_NOT_EXIST", BAD_REQUEST, RuleNoIncomeSubmissionsExistError),
          (UNPROCESSABLE_ENTITY, "BUSINESS_VALIDATION", BAD_REQUEST, RuleSubmissionFailedError),
          (UNPROCESSABLE_ENTITY, "CRYSTALLISATION_TAX_YEAR_ERROR", BAD_REQUEST, RuleFinalDeclarationTaxYearError),
          (UNPROCESSABLE_ENTITY, "FINALISATION_BEFORE_TAX_YEAR_ENDS_ERROR", BAD_REQUEST, RuleFinalDeclarationTaxYearError),
          (UNPROCESSABLE_ENTITY, "CRYSTALLISATION_IN_PROGRESS", BAD_REQUEST, RuleFinalDeclarationInProgressError),
          (UNPROCESSABLE_ENTITY, "FINALISATION_IN_PROGRESS", BAD_REQUEST, RuleFinalDeclarationInProgressError),
          (UNPROCESSABLE_ENTITY, "TAX_YEAR_NOT_SUPPORTED", BAD_REQUEST, RuleTaxYearNotSupportedError),
          (INTERNAL_SERVER_ERROR, "SERVER_ERROR", INTERNAL_SERVER_ERROR, InternalError),
          (SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", INTERNAL_SERVER_ERROR, InternalError),
          (NOT_FOUND, "UNMATCHED_STUB_ERROR", BAD_REQUEST, RuleIncorrectGovTestScenarioError)
        )

        val extraDesErrors = Seq(
          (BAD_REQUEST, "INVALID_IDTYPE", INTERNAL_SERVER_ERROR, InternalError),
          (BAD_REQUEST, "INVALID_IDVALUE", BAD_REQUEST, NinoFormatError),
          (BAD_REQUEST, "INVALID_TAXYEAR", BAD_REQUEST, TaxYearFormatError)
        )

        (errors ++ extraDesErrors).foreach(args => serviceErrorTest.tupled(args))
      }
    }
  }

  private trait TestFinalDeclaration {

    val nino: String                   = "ZG903729C"
    val taxYear: String                = "2023-24"
    lazy val downstreamTaxYear: String = TaxYear.fromMtd(taxYear).asTysDownstream
    val calculationId: String          = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"
    val calculationType: String        = "final-declaration"

    def mtdUri: String = s"/$nino/self-assessment/$taxYear/$calculationId/$calculationType"

    def downstreamUri: String = s"/income-tax/$downstreamTaxYear/calculation/$nino/$calculationId/crystallise"

    def setupStubs(): StubMapping

    def request: WSRequest = {
      setupStubs()
      buildRequest(mtdUri)
        .withHttpHeaders(
          (ACCEPT, "application/vnd.hmrc.7.0+json"),
          (AUTHORIZATION, "Bearer 123")
        )
    }

  }

  private trait TestConfirmAmendment {

    val nino: String                   = "ZG903729C"
    val taxYear: String                = "2025-26"
    lazy val downstreamTaxYear: String = TaxYear.fromMtd(taxYear).asTysDownstream
    val calculationId: String          = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"

    def mtdUri: String = s"/$nino/self-assessment/$taxYear/$calculationId/confirm-amendment"

    def downstreamUri: String = s"/income-tax/$downstreamTaxYear/calculation/$nino/$calculationId/CA/confirm"

    def setupStubs(): StubMapping

    def request: WSRequest = {
      setupStubs()
      buildRequest(mtdUri)
        .withHttpHeaders(
          (ACCEPT, "application/vnd.hmrc.7.0+json"),
          (AUTHORIZATION, "Bearer 123")
        )
    }

  }

}
