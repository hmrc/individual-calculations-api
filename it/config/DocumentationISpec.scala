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
import org.scalatest.OptionValues
import play.api.http.Status
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.WSResponse
import support.IntegrationBaseSpec
import uk.gov.hmrc.auth.core.ConfidenceLevel

class DocumentationISpec extends IntegrationBaseSpec with OptionValues {

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
      |        "status":"ALPHA",
      |        "endpointsEnabled":true
      |      },
      |      {
      |        "version":"4.0",
      |        "status":"ALPHA",
      |        "endpointsEnabled":true
      |      }
      |    ]
      |  }
      |}
    """.stripMargin)

  "GET /api/definition" should {
    "return a 200 with the correct response body" in {

      val response: WSResponse = await(buildRequest("/api/definition").get())
      response.status shouldBe Status.OK
      Json.parse(response.body) shouldBe apiDefinitionJson
    }
  }

  "an OAS documentation request" must {
    "return the documentation that passes OAS V3 parser" in {
      val response: WSResponse = await(buildRequest("/api/conf/3.0/application.yaml").get())
      response.status shouldBe Status.OK

      val contents     = response.body[String]
      val parserResult = new OpenAPIV3Parser().readContents(contents)

      val openAPI = Option(parserResult.getOpenAPI)

      openAPI.value.getOpenapi shouldBe "3.0.3"
      openAPI.value.getInfo.getTitle shouldBe "Individual Calculations (MTD)"
      openAPI.value.getInfo.getVersion shouldBe "3.0"
    }
  }

}
