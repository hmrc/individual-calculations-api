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

package v7.triggerCalculation

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

  "Calling the triggerCalculation endpoint for a pre 23-24 tax year " should {

    "return a 202 status code" when {

      "a valid request is made with an 'in-year' calculation type" in new Pre2324Test {

        val calculationType: String = "in-year"

        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)

          DownstreamStub.onSuccess(DownstreamStub.POST, downstreamUri, Map("crystallise" -> "false"), OK, downstreamSuccessBody)
        }

        val response: WSResponse = await(request(nino, mtdTaxYear, calculationType).post(EmptyBody))

        response.status shouldBe ACCEPTED
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe successBody
      }

      "a valid request is made with an 'intent-to-finalise' calculation type" in new Pre2324Test {

        val calculationType: String = "intent-to-finalise"

        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)

          DownstreamStub.onSuccess(DownstreamStub.POST, downstreamUri, Map("crystallise" -> "true"), OK, downstreamSuccessBody)
        }

        val response: WSResponse = await(request(nino, mtdTaxYear, calculationType).post(EmptyBody))

        response.status shouldBe ACCEPTED
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe successBody
      }
    }

    "return the correct error code" when {
      def validationErrorTest(
                               requestNino: String,
                               requestTaxYear: String,
                               requestCalcType: String,
                               expectedStatus: Int,
                               expectedBody: MtdError): Unit = {
        s"validation fails with ${expectedBody.code} error" in new Pre2324Test {

          override val calculationType: String = requestCalcType

          override val nino: String = requestNino

          override def setupStubs(): StubMapping = {
            AuditStub.audit()
            AuthStub.authorised()
            MtdIdLookupStub.ninoFound(requestNino)
          }

          val response: WSResponse = await(request(nino, requestTaxYear, requestCalcType).post(EmptyBody))
          response.status shouldBe expectedStatus
          response.json shouldBe Json.toJson(expectedBody)
          response.header("Content-Type") shouldBe Some("application/json")
        }
      }

      val input = Seq(
        ("AA1123A", "2017-18", "in-year", BAD_REQUEST, NinoFormatError),
        ("ZG903729C", "20177", "in-year", BAD_REQUEST, TaxYearFormatError),
        ("ZG903729C", "2015-16", "in-year", BAD_REQUEST, RuleTaxYearNotSupportedError),
        ("ZG903729C", "2017-18", "intent-to-amend", BAD_REQUEST, RuleCalculationTypeNotAllowed),
        ("ZG903729C", "2020-22", "in-year", BAD_REQUEST, RuleTaxYearRangeInvalidError)
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
          s"backend returns $backendCode with status $backendStatus" in new Pre2324Test {

            override val calculationType: String = "in-year"

            override def setupStubs(): StubMapping = {
              AuditStub.audit()
              AuthStub.authorised()
              MtdIdLookupStub.ninoFound(nino)
              DownstreamStub.onError(
                DownstreamStub.POST,
                downstreamUri,
                Map("crystallise" -> "false"),
                backendStatus,
                errorBody(backendCode))
            }

            val response: WSResponse = await(request(nino, mtdTaxYear, calculationType).post(EmptyBody))
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
            response.header("Content-Type") shouldBe Some("application/json")
          }
        }

        (api1426errors).foreach(args => (serviceErrorTest _).tupled(args))
      }
    }
  }

  "Calling the triggerCalculation endpoint for a 2024-25 or 2025-26 tax year " should {

    "return a 202 status code" when {

      "a valid request is made with an 'in-year' calculation type" in new Tys2425Test {

        val calculationType: String = "in-year"

        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)

          DownstreamStub.onSuccess(DownstreamStub.POST, downstreamUri, Map("crystallise" -> "false"), ACCEPTED, downstreamSuccessBody)
        }

        val response: WSResponse = await(request(nino, mtdTaxYear, calculationType).post(EmptyBody))

        response.status shouldBe ACCEPTED
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe successBody
      }

      "a valid request is made with an 'intent-to-finalise' calculation type" in new Tys2425Test {

        val calculationType: String = "intent-to-finalise"

        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)

          DownstreamStub.onSuccess(DownstreamStub.POST, downstreamUri, Map("crystallise" -> "true"), ACCEPTED, downstreamSuccessBody)
        }

        val response: WSResponse = await(request(nino, mtdTaxYear, calculationType).post(EmptyBody))

        response.status shouldBe ACCEPTED
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe successBody
      }
    }

    "return the correct error code" when {
      def validationErrorTest(requestNino: String,
                              requestTaxYear: String,
                              requestCalcType: String,
                              expectedStatus: Int,
                              expectedBody: MtdError): Unit = {
        s"validation fails with ${expectedBody.code} error" in new Tys2425Test {

          override val calculationType: String = "in-year"

          override val nino: String = requestNino

          override def setupStubs(): StubMapping = {
            AuditStub.audit()
            AuthStub.authorised()
            MtdIdLookupStub.ninoFound(requestNino)
          }

          val response: WSResponse = await(request(nino, requestTaxYear, requestCalcType).post(EmptyBody))
          response.status shouldBe expectedStatus
          response.json shouldBe Json.toJson(expectedBody)
          response.header("Content-Type") shouldBe Some("application/json")
        }
      }

      val input = Seq(
        ("AA1123A", "2017-18", "in-year", BAD_REQUEST, NinoFormatError),
        ("ZG903729C", "20177", "in-year", BAD_REQUEST, TaxYearFormatError),
        ("ZG903729C", "2015-16", "in-year", BAD_REQUEST, RuleTaxYearNotSupportedError),
        ("ZG903729C", "2024-25", "intent-to-amend", BAD_REQUEST, RuleCalculationTypeNotAllowed),
        ("ZG903729C", "2020-22", "in-year", BAD_REQUEST, RuleTaxYearRangeInvalidError)
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
          s"backend returns $backendCode with status $backendStatus" in new Tys2425Test {

            override val calculationType: String = "in-year"

            override def setupStubs(): StubMapping = {
              AuditStub.audit()
              AuthStub.authorised()
              MtdIdLookupStub.ninoFound(nino)
              DownstreamStub.onError(
                DownstreamStub.POST,
                downstreamUri,
                Map("crystallise" -> "false"),
                backendStatus,
                errorBody(backendCode))
            }

            val response: WSResponse = await(request(nino, mtdTaxYear, calculationType).post(EmptyBody))
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
            response.header("Content-Type") shouldBe Some("application/json")
          }
        }

        (api1897errors).foreach(args => (serviceErrorTest _).tupled(args))
      }
    }
  }

  "Calling the triggerCalculation endpoint for a post 2025-26 tax year " should {

    "return a 202 status code" when {

      "a valid request is made with an 'in-year' calculation type" in new TysPost2526Test {

        val calculationType: String = "in-year"
        val downstreamCalcType: String = "IY"

        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)

          DownstreamStub.onSuccess(DownstreamStub.POST, downstreamUri, ACCEPTED, downstreamSuccessBody)
        }

        val response: WSResponse = await(request(nino, mtdTaxYear, calculationType).post(EmptyBody))

        response.status shouldBe ACCEPTED
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe successBody
      }

      "a valid request is made with an 'intent-to-finalise' calculation type" in new TysPost2526Test {

        val calculationType: String = "intent-to-finalise"
        val downstreamCalcType: String = "IF"

        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)

          DownstreamStub.onSuccess(DownstreamStub.POST, downstreamUri, ACCEPTED, downstreamSuccessBody)
        }

        val response: WSResponse = await(request(nino, mtdTaxYear, calculationType).post(EmptyBody))

        response.status shouldBe ACCEPTED
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe successBody
      }

      "a valid request is made with an 'intent-to-amend' calculation type" in new TysPost2526Test {

        val calculationType: String = "intent-to-amend"
        val downstreamCalcType: String = "IA"

        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)

          DownstreamStub.onSuccess(DownstreamStub.POST, downstreamUri, ACCEPTED, downstreamSuccessBody)
        }

        val response: WSResponse = await(request(nino, mtdTaxYear, calculationType).post(EmptyBody))

        response.status shouldBe ACCEPTED
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe successBody
      }
    }

    "return the correct error code" when {
      def validationErrorTest(requestNino: String, requestTaxYear: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
        s"validation fails with ${expectedBody.code} error" in new TysPost2526Test {

          override val calculationType: String = "in-year"
          override val downstreamCalcType: String = "IY"

          override val nino: String = requestNino

          override def setupStubs(): StubMapping = {
            AuditStub.audit()
            AuthStub.authorised()
            MtdIdLookupStub.ninoFound(requestNino)
          }

          val response: WSResponse = await(request(nino, requestTaxYear, calculationType).post(EmptyBody))
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
          s"backend returns $backendCode with status $backendStatus" in new TysPost2526Test {

            override val calculationType: String = "in-year"
            override val downstreamCalcType: String = "IY"

            override def setupStubs(): StubMapping = {
              AuditStub.audit()
              AuthStub.authorised()
              MtdIdLookupStub.ninoFound(nino)
              DownstreamStub.onError(
                DownstreamStub.POST,
                downstreamUri,
                backendStatus,
                errorBody(backendCode))
            }

            val response: WSResponse = await(request(nino, mtdTaxYear, calculationType).post(EmptyBody))
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
            response.header("Content-Type") shouldBe Some("application/json")
          }
        }

        (api2081errors).foreach(args => (serviceErrorTest _).tupled(args))
      }
    }
  }

  lazy val api1426errors = List(
    (BAD_REQUEST, "INVALID_NINO", BAD_REQUEST, NinoFormatError),
    (BAD_REQUEST, "INVALID_TAX_YEAR", BAD_REQUEST, TaxYearFormatError),
    (FORBIDDEN, "INVALID_TAX_CRYSTALLISE", BAD_REQUEST, FinalDeclarationFormatError),
    (BAD_REQUEST, "INVALID_REQUEST", INTERNAL_SERVER_ERROR, InternalError),
    (BAD_REQUEST, "INVALID_CORRELATIONID", INTERNAL_SERVER_ERROR, InternalError),
    (BAD_REQUEST, "INVALID_CORRELATION_ID", INTERNAL_SERVER_ERROR, InternalError),
    (FORBIDDEN, "NO_SUBMISSION_EXIST", BAD_REQUEST, RuleNoIncomeSubmissionsExistError),
    (CONFLICT, "CONFLICT", BAD_REQUEST, RuleFinalDeclarationReceivedError),
    (INTERNAL_SERVER_ERROR, "SERVER_ERROR", INTERNAL_SERVER_ERROR, InternalError),
    (SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", INTERNAL_SERVER_ERROR, InternalError),
    (NOT_FOUND, "UNMATCHED_STUB_ERROR", BAD_REQUEST, RuleIncorrectGovTestScenarioError)
  )

  lazy val api1897errors = List(
    (BAD_REQUEST, "INVALID_TAXABLE_ENTITY_ID", BAD_REQUEST, NinoFormatError),
    (BAD_REQUEST, "INVALID_TAX_YEAR", BAD_REQUEST, TaxYearFormatError),
    (BAD_REQUEST, "INVALID_CRYSTALLISE", BAD_REQUEST, FinalDeclarationFormatError),
    (BAD_REQUEST, "INVALID_CORRELATIONID", INTERNAL_SERVER_ERROR, InternalError),
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
    (UNPROCESSABLE_ENTITY, "TAX_YEAR_NOT_SUPPORTED", BAD_REQUEST, RuleTaxYearNotSupportedError),
    (INTERNAL_SERVER_ERROR, "SERVER_ERROR", INTERNAL_SERVER_ERROR, InternalError),
    (SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", INTERNAL_SERVER_ERROR, InternalError),
    (NOT_FOUND, "UNMATCHED_STUB_ERROR", BAD_REQUEST, RuleIncorrectGovTestScenarioError)
  )

  lazy val api2081errors = List(
    (BAD_REQUEST, "INVALID_TAXABLE_ENTITY_ID", BAD_REQUEST, NinoFormatError),
    (BAD_REQUEST, "INVALID_TAX_YEAR", BAD_REQUEST, TaxYearFormatError),
    (BAD_REQUEST, "INVALID_CALCULATION_TYPE", INTERNAL_SERVER_ERROR, InternalError),
    (BAD_REQUEST, "INVALID_CORRELATIONID", INTERNAL_SERVER_ERROR, InternalError),
    (UNPROCESSABLE_ENTITY, "INVALID_CALCULATION_ID", INTERNAL_SERVER_ERROR, InternalError),
    (UNPROCESSABLE_ENTITY, "NO_VALID_INCOME_SOURCES", INTERNAL_SERVER_ERROR, InternalError),
    (UNPROCESSABLE_ENTITY, "NO_SUBMISSIONS_EXIST", BAD_REQUEST, RuleNoIncomeSubmissionsExistError),
    (UNPROCESSABLE_ENTITY, "ALREADY_DECLARED", BAD_REQUEST, RuleFinalDeclarationReceivedError),
    //new
    (UNPROCESSABLE_ENTITY, "PREMATURE_FINALISATION", BAD_REQUEST, RulePrematureFinalisationError),
    //new
    (UNPROCESSABLE_ENTITY, "DECLARATION_NOT_RECEIVED", BAD_REQUEST, RuleDeclarationNotReceivedError),
    //new
    (UNPROCESSABLE_ENTITY, "OUTSIDE_AMENDMENT_WINDOW", BAD_REQUEST, RuleOutsideAmendmentWindowError),
    (UNPROCESSABLE_ENTITY, "CALCULATION_EXISTS", BAD_REQUEST, RuleCalculationInProgressError),
    (UNPROCESSABLE_ENTITY, "TAX_YEAR_NOT_SUPPORTED", BAD_REQUEST, RuleTaxYearNotSupportedError),
    (UNPROCESSABLE_ENTITY, "BVR_FAILURE", BAD_REQUEST, RuleBusinessValidationFailureError),
    (INTERNAL_SERVER_ERROR, "SERVER_ERROR", INTERNAL_SERVER_ERROR, InternalError),
    (SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", INTERNAL_SERVER_ERROR, InternalError),
    (NOT_FOUND, "UNMATCHED_STUB_ERROR", BAD_REQUEST, RuleIncorrectGovTestScenarioError)
  )

  private trait Test {

    val nino             = "ZG903729C"
    val calculationType: String

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

    def request(nino: String, taxYear: String, calculationType: String): WSRequest = {
      val uri    = s"/$nino/self-assessment/$taxYear/trigger/$calculationType"

      setupStubs()
      buildRequest(uri)
        .withHttpHeaders(
          (ACCEPT, "application/vnd.hmrc.7.0+json"),
          (AUTHORIZATION, "Bearer 123")
        )
    }

  }

  private trait Pre2324Test extends Test {
    def mtdTaxYear: String    = "2018-19"
    def downstreamUri: String = s"/income-tax/nino/$nino/taxYear/2019/tax-calculation"
  }

  private trait Tys2425Test extends Test {
    def mtdTaxYear: String = "2024-25"

    def downstreamUri: String = s"/income-tax/calculation/24-25/$nino"
  }

  private trait TysPost2526Test extends Test {
    def mtdTaxYear: String = "2025-26"

    val downstreamCalcType: String

    def downstreamUri: String = s"/income-tax/25-26/calculation/$nino/$downstreamCalcType"
  }

}
