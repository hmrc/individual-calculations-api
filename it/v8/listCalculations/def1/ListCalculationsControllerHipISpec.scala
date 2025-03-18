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

package v8.listCalculations.def1

import api.errors.{FormatCalculationTypeError, RuleCalculationTypeNotAllowed}
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.HeaderNames.ACCEPT
import play.api.http.Status._
import play.api.libs.json.Json
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.test.Helpers.AUTHORIZATION
import shared.models.errors._
import shared.services.{AuditStub, AuthStub, DownstreamStub, MtdIdLookupStub}
import shared.support.IntegrationBaseSpec
import v8.listCalculations.def1.model.Def1_ListCalculationsFixture

class ListCalculationsControllerHipISpec extends IntegrationBaseSpec with Def1_ListCalculationsFixture {

  private trait Test {
    val nino: String = "ZG903729C"

    val calculationType: Option[String] = None

    def taxYearString: String = "2018-19"

    private def uri: String = s"/$nino/self-assessment/$taxYearString"

    def downstreamUri: String = s"/itsd/calculations/liability/$nino"

    def setupStubs(): StubMapping

    def request: WSRequest = {
      AuthStub.authorised()
      MtdIdLookupStub.ninoFound(nino)

      def requestQueryParams: Seq[(String, String)] = {
        calculationType.fold(Seq.empty[(String, String)]){ ct =>
          Seq("calculationType" -> ct).collect { case (k, v) => (k, v) }
        }
      }

      setupStubs()
      buildRequest(uri)
        .addQueryStringParameters(requestQueryParams: _*)
        .withHttpHeaders(
          (ACCEPT, "application/vnd.hmrc.8.0+json"),
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

  "Calling the list calculations endpoint for tax years pre 23-24" should {
    "return a 200 status code" when {
      "valid request is made with a tax year" in new Test {

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
    }

    "return error according to spec" when {
      "validation error" when {
        def validationErrorTest(
                                 requestNino: String,
                                 requestTaxYear: String,
                                 requestCalculationType: Option[String],
                                 expectedStatus: Int, expectedBody: MtdError): Unit = {
          s"validation fails with ${expectedBody.code} error" in new Test {
            override val nino: String            = requestNino
            override val taxYearString: String = requestTaxYear
            override val calculationType: Option[String] = requestCalculationType

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
          ("AA1123A", "2017-18", None, BAD_REQUEST, NinoFormatError),
          ("ZG903729C", "20177", None, BAD_REQUEST, TaxYearFormatError),
          ("ZG903729C", "2015-16", None, BAD_REQUEST, RuleTaxYearNotSupportedError),
          ("ZG903729C", "2020-22", None, BAD_REQUEST, RuleTaxYearRangeInvalidError),
          ("ZG903729C", "2017-18", Some("invalid-calc-type"), BAD_REQUEST, FormatCalculationTypeError),
          ("ZG903729C", "2017-18", Some("in-year"), BAD_REQUEST, RuleCalculationTypeNotAllowed)
        )

        input.foreach(args => (validationErrorTest _).tupled(args))
      }

      "downstream returns a service error" when {
        def serviceErrorTest(downstreamStatus: Int, downstreamCode: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
          s"backend returns a code $downstreamStatus error and status $downstreamCode" in new Test {

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
