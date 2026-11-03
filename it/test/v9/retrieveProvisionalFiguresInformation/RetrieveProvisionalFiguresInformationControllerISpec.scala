/*
 * Copyright 2026 HM Revenue & Customs
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

package v9.retrieveProvisionalFiguresInformation

import api.models.errors.*
import api.services.{AuditStub, AuthStub, DownstreamStub, MtdIdLookupStub}
import api.support.IntegrationBaseSpec
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.HeaderNames.ACCEPT
import play.api.http.Status.*
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.DefaultBodyReadables.readableAsString
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.test.Helpers.AUTHORIZATION

class RetrieveProvisionalFiguresInformationControllerISpec extends IntegrationBaseSpec {

  "Calling the 'Retrieve Provisional Figures Information' endpoint" should {
    "return a 200 status code" when {
      "a valid request is made" in new Test {
        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          MtdIdLookupStub.ninoFound(nino)
          AuthStub.authorised()

          DownstreamStub.onSuccess(
            method = DownstreamStub.GET,
            uri = downstreamUri,
            queryParams = downstreamQueryParams,
            status = OK,
            body = downstreamJson
          )
        }

        val response: WSResponse = await(request().get())
        response.status shouldBe OK
        response.body shouldBe mtdJson.toString()
        response.header("Content-Type") shouldBe Some("application/json")
        response.header("X-CorrelationId").nonEmpty shouldBe true
      }
    }

    "return error according to spec" when {
      "validation error" when {
        def validationErrorTest(requestNino: String, requestTaxYear: String, expectedStatus: Int, expectedBody: MtdError): Unit =
          s"validation fails with ${expectedBody.code} error" in new Test {
            override val nino: String    = requestNino
            override val taxYear: String = requestTaxYear

            override def setupStubs(): StubMapping = {
              AuditStub.audit()
              MtdIdLookupStub.ninoFound(nino)
              AuthStub.authorised()
            }

            val response: WSResponse = await(request().get())
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
            response.header("Content-Type") shouldBe Some("application/json")
          }

        val input = List(
          ("AA1123A", "2026-27", BAD_REQUEST, NinoFormatError),
          ("AA123456A", "2026", BAD_REQUEST, TaxYearFormatError),
          ("AA123456A", "2026-28", BAD_REQUEST, RuleTaxYearRangeInvalidError),
          ("AA123456A", "2025-26", BAD_REQUEST, RuleTaxYearNotSupportedError)
        )

        input.foreach(validationErrorTest.tupled)
      }

      "downstream service error" when {
        def serviceErrorTest(downstreamStatus: Int, downstreamCode: String, expectedStatus: Int, expectedBody: MtdError): Unit =
          s"downstream returns a code $downstreamCode error and status $downstreamStatus" in new Test {
            override def setupStubs(): StubMapping = {
              AuditStub.audit()
              MtdIdLookupStub.ninoFound(nino)
              AuthStub.authorised()

              DownstreamStub.onError(
                method = DownstreamStub.GET,
                uri = downstreamUri,
                queryParams = downstreamQueryParams,
                errorStatus = downstreamStatus,
                errorBody = errorBody(downstreamCode)
              )
            }

            val response: WSResponse = await(request().get())
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
            response.header("Content-Type") shouldBe Some("application/json")
          }

        val errors = List(
          (BAD_REQUEST, "1215", BAD_REQUEST, NinoFormatError),
          (BAD_REQUEST, "1117", BAD_REQUEST, TaxYearFormatError),
          (BAD_REQUEST, "1216", INTERNAL_SERVER_ERROR, InternalError),
          (NOT_FOUND, "5010", NOT_FOUND, NotFoundError)
        )

        errors.foreach(serviceErrorTest.tupled)
      }
    }
  }

  private trait Test {
    val nino: String    = "AA123456A"
    val taxYear: String = "2026-27"

    def downstreamUri: String = s"/itsd/provisional-figures/$nino"

    def downstreamQueryParams: Map[String, String] = Map("taxYear" -> "26-27")

    def setupStubs(): StubMapping

    def request(): WSRequest = {
      AuthStub.resetAll()
      setupStubs()

      buildRequest(s"/$nino/provisional-figures/$taxYear")
        .withHttpHeaders(
          (ACCEPT, "application/vnd.hmrc.9.0+json"),
          (AUTHORIZATION, "Bearer 123")
        )
    }

    def errorBody(code: String): String =
      s"""
         |[
         |  {
         |    "errorCode": "$code",
         |    "errorDescription": "error description"
         |  }
         |]
       """.stripMargin

    val downstreamJson: JsValue = Json.parse("""
        |{
        |  "submittedOn": "2027-08-24T14:15:22.544Z",
        |  "pfIndicator": true,
        |  "dateExpected": "2027-10-05",
        |  "whiteSpace": "Provisional figures used pending final confirmation."
        |}
      """.stripMargin)

    val mtdJson: JsValue = Json.parse("""
        |{
        |  "submittedOn": "2027-08-24T14:15:22.544Z",
        |  "provisionalFiguresIndicator": true,
        |  "expectedDate": "2027-10-05",
        |  "supplementaryInformation": "Provisional figures used pending final confirmation."
        |}
      """.stripMargin)

  }

}
