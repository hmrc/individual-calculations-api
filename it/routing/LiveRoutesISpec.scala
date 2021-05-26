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

package routing

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.HeaderNames.ACCEPT
import play.api.http.Status._
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{WSRequest, WSResponse}
import support.IntegrationBaseSpec
import v1.stubs.{AuditStub, AuthStub, BackendStub, MtdIdLookupStub}

class LiveRoutesISpec extends IntegrationBaseSpec {

  override def servicesConfig: Map[String, Any] = Map(
    "microservice.services.des.host" -> mockHost,
    "microservice.services.des.port" -> mockPort,
    "microservice.services.individual-calculations.host" -> mockHost,
    "microservice.services.individual-calculations.port" -> mockPort,
    "microservice.services.mtd-id-lookup.host" -> mockHost,
    "microservice.services.mtd-id-lookup.port" -> mockPort,
    "microservice.services.auth.host" -> mockHost,
    "microservice.services.auth.port" -> mockPort,
    "auditing.consumer.baseUri.port" -> mockPort,
    "feature-switch.all-endpoints.enabled" -> false
  )

  private trait Test {

    val nino: String = "AA123456A"
    val taxYear: String = "2019-20"
    val calculationId: String = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"

    def uri: String = s"/$nino/self-assessment"

    def setupStubs(): StubMapping

    def request(): WSRequest = {
      setupStubs()
      buildRequest(uri)
        .withHttpHeaders((ACCEPT, "application/vnd.hmrc.1.0+json"))
    }
  }

  "Calling the trigger calculation endpoint (switched on in production)" should {

    "return a 202 status code" when {
      "the feature switch is turned off to point to live routes only" in new Test {

        def backendUrl: String = uri

        val successBody: JsValue = Json.parse(
          s"""
            |{
            |   "id": "$calculationId",
            |   "links":[
            |      {
            |         "href":"/individuals/calculations/$nino/self-assessment/$calculationId",
            |         "method":"GET",
            |         "rel":"self"
            |      }
            |   ]
            |}
          """.stripMargin
        )

        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          BackendStub.onSuccess(BackendStub.POST, backendUrl, Map(), ACCEPTED, successBody)
        }

        val response: WSResponse = await(request().post(Json.obj("taxYear" -> taxYear)))
        response.status shouldBe ACCEPTED
      }
    }
  }

  "Declaring crystallisation for a tax year (switched off in production)" should {

    "return a 404 status code" when {
      "the feature switch is turned off to point to live routes only" in new Test {

        override def uri: String = s"/crystallisation/$nino/$taxYear/crystallise"

        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
        }

        val response: WSResponse = await(request().post(Json.obj("calculationId" -> calculationId)))
        response.status shouldBe NOT_FOUND
      }
    }
  }
}
