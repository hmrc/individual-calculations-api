/*
 * Copyright 2021 HM Revenue & Customs
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

package v2.endpoints

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.HeaderNames.ACCEPT
import play.api.http.Status._
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{EmptyBody, WSRequest, WSResponse}
import support.IntegrationBaseSpec
import v2.models.errors._
import v2.stubs._

class IntentToCrystalliseControllerISpec extends IntegrationBaseSpec {

  private trait Test {

    val nino: String = "AA123456A"
    val mtdTaxYear: String = "2019-20"
    val desTaxYear: String = "2020"
    val calculationId: String = "4557ecb5-fd32-48cc-81f5-e6acd1099f3c"
    val correlationId: String = "a1e8057e-fbbc-47a8-a8b4-78d9f015c253"

    def uri: String = s"/crystallisation/$nino/$mtdTaxYear/intent-to-crystallise"
    def desUri: String = s"/income-tax/nino/$nino/taxYear/$desTaxYear/tax-calculation"

    val desQueryParams: Map[String, String] = Map("crystallise" -> "true")

    val mtdResponseJson: JsValue = Json.parse(
      s"""
         |{
         |   "calculationId": "$calculationId",
         |   "links":[
         |      {
         |         "href": "/individuals/calculations/$nino/self-assessment/$calculationId",
         |         "method": "GET",
         |         "rel": "self"
         |      },
         |      {
         |         "href": "/individuals/calculations/crystallisation/$nino/$mtdTaxYear/crystallise",
         |         "method": "POST",
         |         "rel": "crystallise"
         |      }
         |   ]
         |}
    """.stripMargin
    )

    val desResponseJson: JsValue = Json.parse(
      s"""
         |{
         |   "id": "$calculationId"
         |}
    """.stripMargin
    )

    def setupStubs(): StubMapping

    def request(): WSRequest = {
      setupStubs()
      buildRequest(uri)
        .withHttpHeaders((ACCEPT, "application/vnd.hmrc.2.0+json"))
    }
  }

  "submitting an intent to crystallise" should {
    "return a 200 status code" when {
      "valid request is made" in new Test {

        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          DesStub.onSuccess(DesStub.POST, desUri, desQueryParams, OK, desResponseJson)
        }

        val response: WSResponse = await(request().post(EmptyBody))

        response.status shouldBe OK
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe mtdResponseJson
      }
    }

    "return error according to spec" when {
      "validation error" when {
        def validationErrorTest(requestNino: String, requestTaxYear: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
          s"validation fails with ${expectedBody.code} error" in new Test {

            override val nino: String = requestNino
            override val mtdTaxYear: String = requestTaxYear

            override def setupStubs(): StubMapping = {
              AuditStub.audit()
              AuthStub.authorised()
              MtdIdLookupStub.ninoFound(nino)
            }

            val response: WSResponse = await(request().post(EmptyBody))
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
      }

      "service error" when {
        def errorBody(code: String): String =
          s"""
             |{
             |  "code": "$code",
             |  "message": "backend message"
             |}
           """.stripMargin

        def serviceErrorTest(desStatus: Int, desCode: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
          s"backend returns an $desCode error and status $desStatus" in new Test {

            override def setupStubs(): StubMapping = {
              AuditStub.audit()
              AuthStub.authorised()
              MtdIdLookupStub.ninoFound(nino)
              DesStub.onError(DesStub.POST, desUri, desQueryParams, desStatus, errorBody(desCode))
            }

            val response: WSResponse = await(request().post(EmptyBody))
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
            response.header("Content-Type") shouldBe Some("application/json")
          }
        }

        val input = Seq(
          (BAD_REQUEST, "INVALID_NINO", BAD_REQUEST, NinoFormatError),
          (BAD_REQUEST, "INVALID_TAX_YEAR", BAD_REQUEST, TaxYearFormatError),
          (BAD_REQUEST, "INVALID_TAX_CRYSTALLISE", INTERNAL_SERVER_ERROR, DownstreamError),
          (BAD_REQUEST, "INVALID_REQUEST", INTERNAL_SERVER_ERROR, DownstreamError),
          (FORBIDDEN, "NO_SUBMISSION_EXIST", FORBIDDEN, RuleNoSubmissionsExistError),
          (CONFLICT, "CONFLICT", FORBIDDEN, RuleFinalDeclarationReceivedError),
          (INTERNAL_SERVER_ERROR, "SERVER_ERROR", INTERNAL_SERVER_ERROR, DownstreamError),
          (SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", INTERNAL_SERVER_ERROR, DownstreamError)
        )

        input.foreach(args => (serviceErrorTest _).tupled(args))
      }
    }
  }
}