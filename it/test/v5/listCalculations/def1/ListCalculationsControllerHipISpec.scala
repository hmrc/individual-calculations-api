/*
 * Copyright 2025 HM Revenue & Customs
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

package v5.listCalculations.def1

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.libs.json.Json
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.test.Helpers._
import shared.models.domain.TaxYear
import shared.models.errors._
import shared.services.{AuditStub, AuthStub, DownstreamStub, MtdIdLookupStub}
import shared.support.IntegrationBaseSpec
import v5.listCalculations.def1.model.Def1_ListCalculationsFixture

class ListCalculationsControllerHipISpec extends IntegrationBaseSpec with Def1_ListCalculationsFixture {

  private trait Test {
    val nino: String = "ZG903729C"

    def taxYear: Option[String]

    private def uri: String = s"/$nino/self-assessment"

    def downstreamUri: String

    def setupStubs(): StubMapping

    def request: WSRequest = {
      AuthStub.authorised()
      MtdIdLookupStub.ninoFound(nino)

      def queryParams: Seq[(String, String)] =
        Seq("taxYear" -> taxYear)
          .collect { case (k, Some(v)) => (k, v) }

      setupStubs()
      buildRequest(uri)
        .addQueryStringParameters(queryParams: _*)
        .withHttpHeaders(
          (ACCEPT, "application/vnd.hmrc.5.0+json"),
          (AUTHORIZATION, "Bearer 123")
        )
    }

    def errorBody(`type`: String): String =
      s"""
        |{
        |    "origin": "HIP",
        |    "response": {
        |        "failures": [
        |            {
        |                "type": "${`type`}",
        |                "reason": "downstream message"
        |            }
        |        ]
        |    }
        |}
      """.stripMargin
  }

  private trait NonTysTest extends Test {
    def taxYear: Option[String] = Some("2018-19")

    override def downstreamUri: String = s"/itsd/calculations/liability/$nino"
  }

  private trait TysTest extends Test {
    private val mtdTaxYear: String        = TaxYear.fromMtd("2023-24").asMtd
    private val downstreamTaxYear: String = TaxYear.fromMtd("2023-24").asTysDownstream
    def taxYear: Option[String]           = Some(mtdTaxYear)

    override def downstreamUri: String = s"/itsa/income-tax/v1/$downstreamTaxYear/view/calculations/liability/$nino"
  }

  "Calling the list calculations endpoint" should {
    "return a 200 status code" when {
      "valid Non-TYS request is made with a tax year" in new NonTysTest {
        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          DownstreamStub.onSuccess(DownstreamStub.GET, downstreamUri, Map("taxYear" -> "2019"), OK, listCalculationsDownstreamJson)
        }

        val response: WSResponse = await(request.get())
        response.status shouldBe OK
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe listCalculationsMtdJson
      }

      "valid TYS request is made with a tax year" in new TysTest {
        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          DownstreamStub.onSuccess(DownstreamStub.GET, downstreamUri, OK, listCalculationsDownstreamJson)
        }

        val response: WSResponse = await(request.get())
        response.status shouldBe OK
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe listCalculationsMtdJson
      }
    }

    "return a RuleTaxYearNotSupportedForVersionError" when {
      "valid request is made without a tax year" in new TysTest {
        override def taxYear: Option[String] = None

        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          DownstreamStub.onSuccess(DownstreamStub.GET, downstreamUri, OK, listCalculationsDownstreamJson)
        }

        val response: WSResponse = await(request.get())
        response.status shouldBe BAD_REQUEST
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe Json.toJson(RuleTaxYearForVersionNotSupportedError)
      }
    }

    "return error according to spec" when {
      "validation error" when {
        def validationErrorTest(requestNino: String, requestTaxYear: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
          s"validation fails with ${expectedBody.code} error" in new TysTest {
            override val nino: String            = requestNino
            override val taxYear: Option[String] = Some(requestTaxYear)

            override def setupStubs(): StubMapping = {
              AuditStub.audit()
              AuthStub.authorised()
              MtdIdLookupStub.ninoFound(nino)
            }

            val response: WSResponse = await(request.get())
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
      }

      "downstream returns a service error" that {
        def serviceErrorTest(downstreamStatus: Int, downstreamCode: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
          s"has a code $downstreamCode error and status $downstreamStatus" in new TysTest {
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

        val commonError = Seq((NOT_FOUND, "UNMATCHED_STUB_ERROR", BAD_REQUEST, RuleIncorrectGovTestScenarioError))

        val nonTysErrors = Seq(
          (BAD_REQUEST, "1215", BAD_REQUEST, NinoFormatError),
          (BAD_REQUEST, "1117", BAD_REQUEST, TaxYearFormatError),
          (NOT_FOUND, "5010", NOT_FOUND, NotFoundError)
        )

        val tysErrors = Seq(
          (BAD_REQUEST, "INVALID_TAXABLE_ENTITY_ID", BAD_REQUEST, NinoFormatError),
          (BAD_REQUEST, "INVALID_TAX_YEAR", BAD_REQUEST, TaxYearFormatError),
          (BAD_REQUEST, "INVALID_CORRELATION_ID", INTERNAL_SERVER_ERROR, InternalError),
          (NOT_FOUND, "NOT_FOUND", NOT_FOUND, NotFoundError),
          (UNPROCESSABLE_ENTITY, "TAX_YEAR_NOT_SUPPORTED", BAD_REQUEST, RuleTaxYearNotSupportedError),
          (INTERNAL_SERVER_ERROR, "SERVER_ERROR", INTERNAL_SERVER_ERROR, InternalError),
          (SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", INTERNAL_SERVER_ERROR, InternalError)
        )

        (commonError ++ nonTysErrors ++ tysErrors).foreach(args => (serviceErrorTest _).tupled(args))
      }
    }
  }

}
