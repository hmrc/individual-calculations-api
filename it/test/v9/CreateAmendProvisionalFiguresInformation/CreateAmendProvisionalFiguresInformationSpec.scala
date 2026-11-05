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

package v9.CreateAmendProvisionalFiguresInformation

import api.models.errors.*
import api.services.{AuditStub, AuthStub, DownstreamStub, MtdIdLookupStub}
import api.support.IntegrationBaseSpec
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{WSRequest, WSResponse, writeableOf_JsValue}
import play.api.test.Helpers.*

class CreateAmendProvisionalFiguresInformationSpec extends IntegrationBaseSpec {

  "Calling the Create or Amend Provisional Figures Information endpoint" should {
    "return a 204 status code" when {
      "a valid request is made" in new Test {
        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          MtdIdLookupStub.ninoFound(nino)
          AuthStub.authorised()

          DownstreamStub.onSuccess(
            method = DownstreamStub.PUT,
            uri = downstreamUri,
            queryParams = downstreamQueryParams,
            status = NO_CONTENT,
            body = downstreamJson
          )
        }

        val response: WSResponse = await(request().put(mtdJson))
        response.status shouldBe NO_CONTENT
      }
    }

    "return error according to spec" when {
      "validation error" should {
        def validationErrorTest(requestNino: String, requestTaxYear: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
          s"validation fails with ${expectedBody.code} error" in new Test {

            override val nino: String    = requestNino
            override val taxYear: String = requestTaxYear

            override def setupStubs(): StubMapping = {
              AuditStub.audit()
              MtdIdLookupStub.ninoFound(nino)
              AuthStub.authorised()
            }

            val response: WSResponse = await(request().put(mtdJson))
            response.status shouldBe expectedStatus
          }
        }

        val input = List(
          ("AA1123A", "2026-27", BAD_REQUEST, NinoFormatError),
          ("AA123456A", "invalid", BAD_REQUEST, TaxYearFormatError),
          ("AA123456A", "2025-27", BAD_REQUEST, RuleTaxYearRangeInvalidError),
          ("AA123456A", "2025-26", BAD_REQUEST, RuleTaxYearNotSupportedError)
        )

        input.foreach(validationErrorTest.tupled)

        "return a tax year not ended error when temporal validation is enabled" in new Test {
          override def setupStubs(): StubMapping = {
            AuditStub.audit()
            MtdIdLookupStub.ninoFound(nino)
            AuthStub.authorised()
          }

          val response: WSResponse = await(request(suspendTemporalValidations = false).put(mtdJson))
          response.status shouldBe BAD_REQUEST
          response.json shouldBe Json.toJson(RuleTaxYearNotEndedError)
        }
      }

      "downstream service error" should {
        def serviceErrorTest(downstreamStatus: Int, downstreamCode: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
          s"downstream returns an $downstreamCode error and status $downstreamStatus" in new Test {
            override def setupStubs(): StubMapping = {
              AuditStub.audit()
              MtdIdLookupStub.ninoFound(nino)
              AuthStub.authorised()

              DownstreamStub.onError(
                method = DownstreamStub.PUT,
                uri = downstreamUri,
                queryParams = downstreamQueryParams,
                errorStatus = downstreamStatus,
                errorBody = errorBody(downstreamCode)
              )
            }

            val response: WSResponse = await(request().put(mtdJson))
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
          }
        }

        val errors = List(
          (BAD_REQUEST, "1215", BAD_REQUEST, NinoFormatError),
          (BAD_REQUEST, "1117", BAD_REQUEST, TaxYearFormatError),
          (BAD_REQUEST, "1216", INTERNAL_SERVER_ERROR, InternalError),
          (BAD_REQUEST, "1000", INTERNAL_SERVER_ERROR, InternalError),
          (UNPROCESSABLE_ENTITY, "1285", BAD_REQUEST, ruleExpectedDateNotInFutureError),
          (UNPROCESSABLE_ENTITY, "4200", BAD_REQUEST, RuleOutsideAmendmentWindowError)
        )

        errors.foreach(serviceErrorTest.tupled)
      }
    }
  }

  private trait Test {
    val nino: String    = "AA123456A"
    val taxYear: String = "2026-27"

    def setupStubs(): StubMapping

    def downstreamUri: String = s"/itsd/provisional-figures/$nino"

    def downstreamQueryParams: Map[String, String] = Map("taxYear" -> "26-27")

    def request(suspendTemporalValidations: Boolean = true): WSRequest = {
      AuthStub.resetAll()
      setupStubs()

      buildRequest(s"/$nino/provisional-figures/$taxYear")
        .withHttpHeaders(
          (ACCEPT, "application/vnd.hmrc.9.0+json"),
          ("suspend-temporal-validations", suspendTemporalValidations.toString),
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
        |  "pfIndicator": true,
        |  "dateExpected": "2027-10-05",
        |  "whiteSpace": "Provisional figures used pending final confirmation."
        |}
      """.stripMargin)

    val mtdJson: JsValue = Json.parse("""
        |{
        |  "provisionalFiguresIndicator": true,
        |  "expectedDate": "2027-10-05",
        |  "supplementaryInformation": "Provisional figures used pending final confirmation."
        |}
      """.stripMargin)

  }

}
