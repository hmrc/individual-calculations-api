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
import play.api.http.Status
import play.api.http.Status._
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{WSRequest, WSResponse}
import support.V3IntegrationBaseSpec
import v3.stubs.{AuditStub, AuthStub, BackendStub, MtdIdLookupStub}

class AuthISpec extends V3IntegrationBaseSpec {

  private trait Test {
    val nino: String          = "AA123456A"
    val taxYear: String       = "2017-18"
    val data: String          = "someData"
    val correlationId: String = "X-123"

    def uri: String        = s"/$nino/self-assessment"
    def backendUrl: String = uri

    val responseBody: JsValue = Json.parse(
      """
        |{
        | "id" : "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
        | "links":[
        |   {
        |   "href":"/individuals/calculations/AA123456A/self-assessment/f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
        |   "method":"GET",
        |   "rel":"self"
        |     }
        |   ]
        |}
       """.stripMargin
    )

    def setupStubs(): StubMapping

    def request(): WSRequest = {
      setupStubs()
      buildRequest(uri)
        .withHttpHeaders((ACCEPT, "application/vnd.hmrc.3.0+json"))
    }

  }

  "Calling the sample endpoint" when {

    "the NINO cannot be converted to a MTD ID" should {

      "return 500" in new Test {
        override val nino: String = "AA123456A"

        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          MtdIdLookupStub.internalServerError(nino)
        }

        val response: WSResponse = await(request().post(Json.parse("""{"taxYear" : "2018-19"}""")))
        response.status shouldBe Status.INTERNAL_SERVER_ERROR
      }
    }

    "an MTD ID is successfully retrieve from the NINO and the user is authorised" should {

      "return 200" in new Test {
        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          BackendStub.onSuccess(BackendStub.POST, backendUrl, Map(), ACCEPTED, responseBody)
        }

        val response: WSResponse = await(request().post(Json.parse("""{"taxYear" : "2018-19"}""")))
        response.status shouldBe Status.ACCEPTED
      }
    }

    "an MTD ID is successfully retrieve from the NINO and the user is NOT logged in" should {

      "return 403" in new Test {
        override val nino: String = "AA123456A"

        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          MtdIdLookupStub.ninoFound(nino)
          AuthStub.unauthorisedNotLoggedIn()
        }

        val response: WSResponse = await(request().post(Json.parse("""{"taxYear" : "2018-19"}""")))
        response.status shouldBe Status.FORBIDDEN
      }
    }

    "an MTD ID is successfully retrieve from the NINO and the user is NOT authorised" should {

      "return 403" in new Test {
        override val nino: String = "AA123456A"

        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          MtdIdLookupStub.ninoFound(nino)
          AuthStub.unauthorisedOther()
        }

        val response: WSResponse = await(request().post(Json.parse("""{"taxYear" : "2018-19"}""")))
        response.status shouldBe Status.FORBIDDEN
      }
    }

  }

}
