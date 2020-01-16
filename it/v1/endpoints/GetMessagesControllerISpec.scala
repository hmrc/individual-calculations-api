/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIED OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package v1.endpoints

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.HeaderNames.ACCEPT
import play.api.http.Status._
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.libs.ws.{WSRequest, WSResponse}
import support.IntegrationBaseSpec
import v1.fixtures.getMessages.MessagesResponseFixture
import v1.fixtures.getMessages.MessagesResponseFixture._
import v1.fixtures.getMetadata.MetadataResponseFixture._
import v1.models.errors._
import v1.stubs.{AuditStub, AuthStub, BackendStub, MtdIdLookupStub}

class GetMessagesControllerISpec extends IntegrationBaseSpec {

  private trait Test {

    val nino                      = "AA123456A"
    val correlationId             = "X-123"
    val calcId                    = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"

    def backendUrl: String = s"/$nino/self-assessment/$calcId"

    def setupStubs(): StubMapping

    def request: WSRequest = {
      setupStubs()
      buildRequest(uri)
        .withHttpHeaders((ACCEPT, "application/vnd.hmrc.1.0+json"))
    }

    def uri: String = s"/$nino/self-assessment/$calcId/messages"
  }

  "Calling the get calculation messages endpoint" should {
    "return a 200 status code" when {

      val successBody = Json.obj(
        "metadata" -> metadataResponseJson,
        "messages" -> MessagesResponseFixture.messagesResponseJson)

      val hateoasLinks: JsValue = Json.parse("""{
          |    "links":[
          |      {
          |         "href": "/individuals/calculations/AA123456A/self-assessment/f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
          |         "method": "GET",
          |         "rel": "metadata"
          |         },
          |      {
          |         "href": "/individuals/calculations/AA123456A/self-assessment/f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c/messages",
          |         "method": "GET",
          |         "rel": "self"
          |         }
          |      ]
          |}""".stripMargin)

      val successOutput = messagesResponseJson.as[JsObject].deepMerge(hateoasLinks.as[JsObject])

      "valid request is made with no filter" in new Test {
        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          BackendStub.onSuccess(BackendStub.GET, backendUrl, OK, successBody)
        }
        val response: WSResponse = await(request.get)

        response.status shouldBe OK
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe successOutput
      }

      "a valid request is made with a filter" in new Test {
        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          BackendStub.onSuccess(BackendStub.GET, backendUrl, OK, successBody)
        }

        val response: WSResponse = await(request.withQueryStringParameters(("type", "error")).get)

        response.status shouldBe OK
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe messagesResponseJsonErrors.as[JsObject].deepMerge(hateoasLinks.as[JsObject])
      }

      "valid request is made with multiple filters" in new Test {
        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          BackendStub.onSuccess(BackendStub.GET, backendUrl, OK, successBody)
        }

        val response: WSResponse = await(request
          .withQueryStringParameters("type" -> "error", "type" -> "warning", "type" -> "info").get)

        response.status shouldBe OK
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe successOutput
      }
    }

    "return a noMessagesExist error" when{
      "a calculation is returned without messages" in new Test{
        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          BackendStub.onSuccess(BackendStub.GET, backendUrl, OK, messagesResponseTopLevelJsonEmpty)
        }

        val response: WSResponse = await(request.withQueryStringParameters(("type", "error")).get)

        response.status shouldBe NOT_FOUND
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe Json.toJson(NoMessagesExistError)
      }

      "all returned messages are filtered out" in new Test{
        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          BackendStub.onSuccess(BackendStub.GET, backendUrl, OK, messagesResponseTopLevelJsonInfo)
        }

        val response: WSResponse = await(request.withQueryStringParameters(("type", "error")).get)

        response.status shouldBe NOT_FOUND
        response.header("Content-Type") shouldBe Some("application/json")
        response.json shouldBe Json.toJson(NoMessagesExistError)
      }
    }

    "return error according to spec" when {
      "validation error" when {
        def validationErrorTest(requestNino: String,
                                requestCalcId: String,
                                typeQuery: Option[String],
                                expectedStatus: Int,
                                expectedBody: MtdError): Unit = {
          s"validation fails with ${expectedBody.code} error" in new Test {

            override val nino: String   = requestNino
            override val calcId: String = requestCalcId

            override def setupStubs(): StubMapping = {
              AuditStub.audit()
              AuthStub.authorised()
              MtdIdLookupStub.ninoFound(nino)
            }

            val queryParams: Seq[(String, String)] =
              Seq("type" -> typeQuery.getOrElse("error"), "type" -> "info", "type" -> "warning")

            val response: WSResponse = await(request.withQueryStringParameters(queryParams: _*).get)
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
            response.header("Content-Type") shouldBe Some("application/json")
          }
        }

        val input = Seq(
          ("AA1123A", "12345678", None, BAD_REQUEST, NinoFormatError),
          ("AA123456A", "AAAAAAA",None,  BAD_REQUEST, CalculationIdFormatError),
          ("AA123456A", "12345678", Some("shmerrors"), BAD_REQUEST, TypeFormatError)
        )

        input.foreach(args => (validationErrorTest _).tupled(args))
      }

      "backend service error" when {

        def errorBody(code: String): String =
          s"""{
             |  "code": "$code",
             |  "message": "backend message"
             |}""".stripMargin

        def serviceErrorTest(backendStatus: Int, backendCode: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
          s"backend returns an $backendCode error and status $backendStatus" in new Test {

            override def setupStubs(): StubMapping = {
              AuditStub.audit()
              AuthStub.authorised()
              MtdIdLookupStub.ninoFound(nino)
              BackendStub.onError(BackendStub.GET, backendUrl, backendStatus, errorBody(backendCode))
            }

            val response: WSResponse = await(request.get)
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
            response.header("Content-Type") shouldBe Some("application/json")
          }
        }

        val input = Seq(
          (BAD_REQUEST, "FORMAT_NINO", BAD_REQUEST, NinoFormatError),
          (BAD_REQUEST, "FORMAT_CALC_ID", BAD_REQUEST, CalculationIdFormatError),
          (NOT_FOUND, "MATCHING_RESOURCE_NOT_FOUND", NOT_FOUND, NotFoundError),
          (INTERNAL_SERVER_ERROR, "SERVER_ERROR", INTERNAL_SERVER_ERROR, DownstreamError),
          (SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", INTERNAL_SERVER_ERROR, DownstreamError)
        )

        input.foreach(args => (serviceErrorTest _).tupled(args))
      }
    }
  }
}
