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
import play.api.libs.json.Json
import play.api.libs.ws.{EmptyBody, WSRequest, WSResponse}
import support.V3IntegrationBaseSpec
import v3.models.errors._
import v3.stubs.{AuditStub, AuthStub, BackendStub, MtdIdLookupStub}

class TriggerCalculationControllerISpec extends V3IntegrationBaseSpec {

  private trait Test {

    val nino              = "AA123456A"
    val taxYear           = "2018-19"
    val downstreamTaxYear = "2019"
    val finalDeclaration  = true
    val backendUrl        = s"/income-tax/nino/$nino/taxYear/$downstreamTaxYear/tax-calculation"

    def setupStubs(): StubMapping

    def request(nino: String, taxYear: String, maybeFinalDeclaration: Option[Boolean]): WSRequest = {
      val suffix = maybeFinalDeclaration.map(d => s"?finalDeclaration=$d").getOrElse("")
      val uri    = s"/$nino/self-assessment/$taxYear/$suffix"

      setupStubs()
      buildRequest(uri)
        .withHttpHeaders((ACCEPT, "application/vnd.hmrc.3.0+json"))
    }

  }

  "Calling the triggerCalculation endpoint" should {

    "return a 202 status code" when {

      val downstreamSuccessBody = Json.parse("""
          |{
          |  "id" : "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"
          |}
          |""".stripMargin)

      val successBody = Json.parse(
        """
          |{
          | "calculationId" : "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
          | "links":[
          |   {
          |     "href":"/individuals/calculations/AA123456A/self-assessment",
          |     "method":"GET",
          |     "rel":"list"
          |   },
          |   {
          |     "href":"/individuals/calculations/AA123456A/self-assessment/2018-19/f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
          |     "method":"GET",
          |     "rel":"retrieve-tax-calculation"
          |   }
          | ]
          |}
        """.stripMargin
      )

      "a valid request is made" in new Test {
        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          BackendStub.onSuccess(BackendStub.POST, backendUrl, Map("crystallise" -> s"$finalDeclaration"), OK, downstreamSuccessBody)
        }

        val response: WSResponse = await(request(nino, taxYear, Some(finalDeclaration)).post(EmptyBody))

        response.status shouldBe ACCEPTED
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe successBody
      }
    }

    "return the correct error code" when {
      def validationErrorTest(requestNino: String, requestTaxYear: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
        s"validation fails with ${expectedBody.code} error" in new Test {

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
        ("AA123456A", "20177", BAD_REQUEST, TaxYearFormatError),
        ("AA123456A", "2015-16", BAD_REQUEST, RuleTaxYearNotSupportedError),
        ("AA123456A", "2020-22", BAD_REQUEST, RuleTaxYearRangeInvalidError)
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
          s"backend returns $backendCode with status $backendStatus" in new Test {

            override def setupStubs(): StubMapping = {
              AuditStub.audit()
              AuthStub.authorised()
              MtdIdLookupStub.ninoFound(nino)
              BackendStub.onError(BackendStub.POST, backendUrl, Map("crystallise" -> s"$finalDeclaration"), backendStatus, errorBody(backendCode))
            }

            val response: WSResponse = await(request(nino, taxYear, Some(finalDeclaration)).post(EmptyBody))
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
            response.header("Content-Type") shouldBe Some("application/json")
          }
        }

        val input = Seq(
          (BAD_REQUEST, "INVALID_NINO", BAD_REQUEST, NinoFormatError),
          (BAD_REQUEST, "INVALID_TAX_YEAR", BAD_REQUEST, TaxYearFormatError),
          (FORBIDDEN, "INVALID_TAX_CRYSTALLISE", BAD_REQUEST, FinalDeclarationFormatError),
          (BAD_REQUEST, "INVALID_REQUEST", INTERNAL_SERVER_ERROR, DownstreamError),
          (FORBIDDEN, "NO_SUBMISSION_EXIST", FORBIDDEN, RuleNoIncomeSubmissionsExistError),
          (CONFLICT, "CONFLICT", FORBIDDEN, RuleFinalDeclarationReceivedError),
          (INTERNAL_SERVER_ERROR, "SERVER_ERROR", INTERNAL_SERVER_ERROR, DownstreamError),
          (SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", INTERNAL_SERVER_ERROR, DownstreamError)
        )

        input.foreach(args => (serviceErrorTest _).tupled(args))
      }
    }
  }

}
