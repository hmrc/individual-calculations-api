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

package v6.triggerCalculation

import api.errors._
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.HeaderNames.ACCEPT
import play.api.http.Status._
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{EmptyBody, WSRequest, WSResponse}
import play.api.test.Helpers.AUTHORIZATION
import shared.models.errors._
import shared.services.{AuditStub, AuthStub, DownstreamStub, MtdIdLookupStub}
import shared.support.IntegrationBaseSpec

class TriggerCalculationControllerISpec extends IntegrationBaseSpec {

  "Calling the triggerCalculation endpoint" should {

    "return a 202 status code" when {

      "a valid request is made" in new NonTysTest {

        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)

          DownstreamStub.onSuccess(DownstreamStub.POST, downstreamUri, Map("crystallise" -> s"$finalDeclaration"), OK, downstreamSuccessBody)
        }

        val response: WSResponse = await(request(nino, mtdTaxYear, Some(finalDeclaration)).post(EmptyBody))

        response.status shouldBe ACCEPTED
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe successBody
      }

      "a valid request is made without a final declaration" in new NonTysTest {
        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          DownstreamStub.onSuccess(DownstreamStub.POST, downstreamUri, Map("crystallise" -> s"false"), OK, downstreamSuccessBody)
        }

        val response: WSResponse = await(request(nino, mtdTaxYear, None).post(EmptyBody))

        response.status shouldBe ACCEPTED
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe successBody
      }

      "a valid request is made for a Tax Year Specific (TYS) tax year" in new TysTest {
        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          DownstreamStub.onSuccess(DownstreamStub.POST, downstreamUri, Map("crystallise" -> s"$finalDeclaration"), ACCEPTED, downstreamSuccessBody)
        }

        val response: WSResponse = await(request(nino, mtdTaxYear, Some(finalDeclaration)).post(EmptyBody))

        response.status shouldBe ACCEPTED
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe successBody
      }
    }

    "return the correct error code" when {
      def validationErrorTest(requestNino: String, requestTaxYear: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
        s"validation fails with ${expectedBody.code} error" in new NonTysTest {

          override val nino: String = requestNino

          override def setupStubs(): StubMapping = {
            AuditStub.audit()
            AuthStub.authorised()
            MtdIdLookupStub.ninoFound(requestNino)
          }

          val response: WSResponse = await(request(nino, requestTaxYear, Some(finalDeclaration)).post(EmptyBody))
          response.status shouldBe expectedStatus
          response.json shouldBe Json.toJson(expectedBody)
          response.header("Content-Type") shouldBe Some("application/json")
        }
      }

      val input = Seq(
        ("AA1123A", "2017-18", BAD_REQUEST, NinoFormatError),
        ("ZG903729C", "20177", BAD_REQUEST, TaxYearFormatError),
        ("ZG903729C", "2015-16", BAD_REQUEST, RuleTaxYearNotSupportedError),
        ("ZG903729C", "2020-22", BAD_REQUEST, RuleTaxYearRangeInvalidError)
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
          s"backend returns $backendCode with status $backendStatus" in new NonTysTest {

            override def setupStubs(): StubMapping = {
              AuditStub.audit()
              AuthStub.authorised()
              MtdIdLookupStub.ninoFound(nino)
              DownstreamStub.onError(
                DownstreamStub.POST,
                downstreamUri,
                Map("crystallise" -> s"$finalDeclaration"),
                backendStatus,
                errorBody(backendCode))
            }

            val response: WSResponse = await(request(nino, mtdTaxYear, Some(finalDeclaration)).post(EmptyBody))
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
            response.header("Content-Type") shouldBe Some("application/json")
          }
        }

        val errors = List(
          (BAD_REQUEST, "INVALID_NINO", BAD_REQUEST, NinoFormatError),
          (BAD_REQUEST, "INVALID_TAX_YEAR", BAD_REQUEST, TaxYearFormatError),
          (FORBIDDEN, "INVALID_TAX_CRYSTALLISE", BAD_REQUEST, FinalDeclarationFormatError),
          (BAD_REQUEST, "INVALID_REQUEST", INTERNAL_SERVER_ERROR, InternalError),
          (FORBIDDEN, "NO_SUBMISSION_EXIST", BAD_REQUEST, RuleNoIncomeSubmissionsExistError),
          (CONFLICT, "CONFLICT", BAD_REQUEST, RuleFinalDeclarationReceivedError),
          (INTERNAL_SERVER_ERROR, "SERVER_ERROR", INTERNAL_SERVER_ERROR, InternalError),
          (SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", INTERNAL_SERVER_ERROR, InternalError),
          (NOT_FOUND, "UNMATCHED_STUB_ERROR", BAD_REQUEST, RuleIncorrectGovTestScenarioError)
        )

        val extraTysErrors = List(
          (BAD_REQUEST, "INVALID_TAXABLE_ENTITY_ID", BAD_REQUEST, NinoFormatError),
          (BAD_REQUEST, "INVALID_CRYSTALLISE", BAD_REQUEST, FinalDeclarationFormatError),
          (UNPROCESSABLE_ENTITY, "INVALID_CALCULATION_ID", INTERNAL_SERVER_ERROR, InternalError),
          (UNPROCESSABLE_ENTITY, "NO_VALID_INCOME_SOURCES", INTERNAL_SERVER_ERROR, InternalError),
          (UNPROCESSABLE_ENTITY, "NO_SUBMISSIONS_EXIST", BAD_REQUEST, RuleNoIncomeSubmissionsExistError),
          (UNPROCESSABLE_ENTITY, "CHANGED_INCOME_SOURCES", BAD_REQUEST, RuleIncomeSourcesChangedError),
          (UNPROCESSABLE_ENTITY, "OUTDATED_SUBMISSION", BAD_REQUEST, RuleRecentSubmissionsExistError),
          (UNPROCESSABLE_ENTITY, "RESIDENCY_CHANGED", BAD_REQUEST, RuleResidencyChangedError),
          (UNPROCESSABLE_ENTITY, "ALREADY_DECLARED", BAD_REQUEST, RuleFinalDeclarationReceivedError),
          (UNPROCESSABLE_ENTITY, "PREMATURE_CRYSTALLISATION", BAD_REQUEST, RuleTaxYearNotEndedError),
          (UNPROCESSABLE_ENTITY, "CALCULATION_EXISTS", BAD_REQUEST, RuleCalculationInProgressError),
          (UNPROCESSABLE_ENTITY, "BVR_FAILURE", BAD_REQUEST, RuleBusinessValidationFailureError),
          (UNPROCESSABLE_ENTITY, "TAX_YEAR_NOT_SUPPORTED", BAD_REQUEST, RuleTaxYearNotSupportedError)
        )

        (errors ++ extraTysErrors).foreach(args => (serviceErrorTest _).tupled(args))
      }
    }
  }

  private trait Test {

    val nino             = "ZG903729C"
    val finalDeclaration = true

    val downstreamSuccessBody: JsValue = Json.parse("""
        |{
        |  "id" : "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"
        |}
        |""".stripMargin)

    val successBody: JsValue = Json.parse(
      s"""
        |{
        | "calculationId" : "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"
        |}
      """.stripMargin
    )

    def mtdTaxYear: String

    def downstreamUri: String

    def setupStubs(): StubMapping

    def request(nino: String, taxYear: String, maybeFinalDeclaration: Option[Boolean]): WSRequest = {
      val suffix = maybeFinalDeclaration.map(d => s"?finalDeclaration=$d").getOrElse("")
      val uri    = s"/$nino/self-assessment/$taxYear/$suffix"

      setupStubs()
      buildRequest(uri)
        .withHttpHeaders(
          (ACCEPT, "application/vnd.hmrc.6.0+json"),
          (AUTHORIZATION, "Bearer 123")
        )
    }

  }

  private trait TysTest extends Test {
    def mtdTaxYear: String    = "2023-24"
    def downstreamUri: String = s"/income-tax/calculation/23-24/$nino"

  }

  private trait NonTysTest extends Test {
    def mtdTaxYear: String    = "2018-19"
    def downstreamUri: String = s"/income-tax/nino/$nino/taxYear/2019/tax-calculation"

  }

}
