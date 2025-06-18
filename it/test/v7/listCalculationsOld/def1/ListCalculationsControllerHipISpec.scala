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

package v7.listCalculationsOld.def1

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.HeaderNames.ACCEPT
import play.api.http.Status._
import play.api.libs.json.Json
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.test.Helpers.AUTHORIZATION
import shared.models.domain.TaxYear
import shared.models.domain.TaxYear.currentTaxYear
import shared.models.errors._
import v7.listCalculationsOld.def1.model.Def1_ListCalculationsFixture
import shared.services.{AuditStub, AuthStub, DownstreamStub, MtdIdLookupStub}
import shared.support.IntegrationBaseSpec

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

      def downstreamQueryParams: Seq[(String, String)] =
        Seq("taxYear" -> taxYear)
          .collect { case (k, Some(v)) => (k, v) }

      setupStubs()
      buildRequest(uri)
        .addQueryStringParameters(downstreamQueryParams: _*)
        .withHttpHeaders(
          (ACCEPT, "application/vnd.hmrc.7.0+json"),
          (AUTHORIZATION, "Bearer 123")
        )
    }

    def errorBody(code: String): String =
      s"""
        |[
        |    {
        |        "errorCode": "$code",
        |        "errorDescription": "error description"
        |    }
        |]
      """.stripMargin
  }

  private trait NonTysTest extends Test {
    def taxYear: Option[String] = Some("2018-19")

    override def downstreamUri: String = s"/itsd/calculations/liability/$nino"
  }

  private trait TysTest extends Test {
    def mtdTaxYear: String                = currentTaxYear.asMtd
    private val downstreamTaxYear: String = TaxYear.fromMtd(mtdTaxYear).asTysDownstream
    def taxYear: Option[String]           = Some(mtdTaxYear)

    override def downstreamUri: String = if (TaxYear.fromMtd(mtdTaxYear).year >= 2026) {
      s"/income-tax/$downstreamTaxYear/view/$nino/calculations-summary"
    } else {
      s"/income-tax/$downstreamTaxYear/view/calculations-summary/$nino"
    }
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

      "valid request is made without a tax year" in new TysTest {

        override def taxYear: Option[String] = None

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

      Seq("2024-25", "2025-26").foreach { tysTaxYear =>
        s"valid TYS request is made with tax year $tysTaxYear" in new TysTest {

          override def mtdTaxYear: String = tysTaxYear

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
    }

    "return error according to spec" when {
      "validation error" when {
        def validationErrorTest(requestNino: String, requestTaxYear: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
          s"validation fails with ${expectedBody.code} error" in new NonTysTest {
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

      "downstream returns a service error" when {
        def serviceErrorTest(downstreamStatus: Int, downstreamCode: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
          s"backend returns an $downstreamStatus error and status $downstreamCode" in new NonTysTest {

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
          (BAD_REQUEST, "1215", BAD_REQUEST, NinoFormatError),
          (BAD_REQUEST, "1117", BAD_REQUEST, TaxYearFormatError),
          (NOT_FOUND, "5010", NOT_FOUND, NotFoundError)
        )

        errors.foreach(args => (serviceErrorTest _).tupled(args))
      }
    }
  }

}
