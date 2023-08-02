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

package config

import io.swagger.v3.parser.OpenAPIV3Parser
import play.api.http.Status.OK
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.WSResponse
import routing.{Version2, Version3, Version4}
import support.IntegrationBaseSpec
import uk.gov.hmrc.auth.core.ConfidenceLevel

import scala.util.Try

class DocumentationControllerISpec extends IntegrationBaseSpec {

  val config: AppConfig                = app.injector.instanceOf[AppConfig]
  val confidenceLevel: ConfidenceLevel = config.confidenceLevelConfig.confidenceLevel

  val apiDefinitionJson: JsValue = Json.parse(s"""
      |{
      |  "scopes":[
      |    {
      |      "key":"read:self-assessment",
      |      "name":"View your Self Assessment information",
      |      "description":"Allow read access to self assessment data",
      |      "confidenceLevel": $confidenceLevel
      |    },
      |    {
      |      "key":"write:self-assessment",
      |      "name":"Change your Self Assessment information",
      |      "description":"Allow write access to self assessment data",
      |      "confidenceLevel": $confidenceLevel
      |    }
      |  ],
      |  "api":{
      |    "name":"Individual Calculations (MTD)",
      |    "description":"An API for providing individual calculations data",
      |    "context":"individuals/calculations",
      |    "categories": [
      |       "INCOME_TAX_MTD"
      |     ],
      |    "versions":[
      |      {
      |        "version":"2.0",
      |        "status":"DEPRECATED",
      |        "endpointsEnabled":true
      |      },
      |      {
      |        "version":"3.0",
      |        "status":"DEPRECATED",
      |        "endpointsEnabled":true
      |      },
      |      {
      |        "version":"4.0",
      |        "status":"BETA",
      |        "endpointsEnabled":true
      |      }
      |    ]
      |  }
      |}
    """.stripMargin)

  "GET /api/definition" should {
    "return a 200 with the correct response body" in {

      val response: WSResponse = await(buildRequest("/api/definition").get())
      response.status shouldBe OK
      Json.parse(response.body) shouldBe apiDefinitionJson
    }
  }

  "an OAS documentation request" must {
    Seq(Version2, Version3, Version4).foreach { version =>
      s"return the documentation for $version" in {
        val response = get(s"/api/conf/${version.name}/application.yaml")

        val body         = response.body[String]
        val parserResult = Try(new OpenAPIV3Parser().readContents(body))
        parserResult.isSuccess shouldBe true

        val openAPI = Option(parserResult.get.getOpenAPI).getOrElse(fail("openAPI wasn't defined"))
        openAPI.getOpenapi shouldBe "3.0.3"
        withClue(s"If v${version.name} endpoints are enabled in application.conf, remove the [test only] from this test: ") {
          openAPI.getInfo.getTitle shouldBe "Individual Calculations (MTD)"
        }
        openAPI.getInfo.getVersion shouldBe version.toString
      }
    }
  }

  private def get(path: String): WSResponse = {
    val response: WSResponse = await(buildRequest(path).get())
    response.status shouldBe OK
    response
  }

}
