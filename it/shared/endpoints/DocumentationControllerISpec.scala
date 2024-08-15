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

package shared.endpoints

import io.swagger.v3.parser.OpenAPIV3Parser
import play.api.http.Status
import play.api.http.Status.OK
import play.api.libs.json.Json
import play.api.libs.ws.WSResponse
import shared.config.AppConfig
import shared.routing.{Version, Versions}
import shared.support.IntegrationBaseSpec

import scala.util.Try

class DocumentationControllerISpec extends IntegrationBaseSpec {

  private val config          = app.injector.instanceOf[AppConfig]
  private val confidenceLevel = config.confidenceLevelConfig.confidenceLevel

  private lazy val enabledVersions: Seq[Version] =
    (1 to 99).collect {
      case num if config.safeEndpointsEnabled(s"$num.0") =>
        Versions.getFrom(s"$num.0").toOption
    }.flatten

  "GET /api/definition" should {
    "return a 200 with the correct response body" in {
      val response: WSResponse = await(buildRequest("/api/definition").get())
      response.status shouldBe Status.OK

      val responseBody = response.body

      responseBody should include(""""api":{"name":""")
      responseBody should include(""""categories":["INCOME_TAX_MTD"]""")
      responseBody should include(s""""confidenceLevel":$confidenceLevel""")

      noException should be thrownBy Json.parse(responseBody)
    }
  }

  "an OAS documentation request" must {
    enabledVersions should not be empty

    enabledVersions.foreach { version =>
      s"return the documentation for $version" in {
        val response = get(s"/api/conf/$version/application.yaml")
        response.status shouldBe Status.OK

        val body         = response.body[String]
        val parserResult = Try(new OpenAPIV3Parser().readContents(body))
        parserResult.isSuccess shouldBe true

        val openAPI = Option(parserResult.get.getOpenAPI).getOrElse(fail("openAPI wasn't defined"))
        openAPI.getOpenapi shouldBe "3.0.3"
        openAPI.getInfo.getVersion shouldBe version.name

        if (config.apiVersionReleasedInProduction(version.name)) {
          openAPI.getInfo.getTitle.toLowerCase should not include "[test only]"
        } else {
          openAPI.getInfo.getTitle should include("[test only]")
        }
      }

      s"return the documentation with the correct accept header for version $version" in {
        val response = get(s"/api/conf/${version.name}/common/headers.yaml")
        response.status shouldBe Status.OK

        val body        = response.body[String]
        val headerRegex = """(?s).*?application/vnd\.hmrc\.(\d+\.\d+)\+json.*?""".r
        val header      = headerRegex.findFirstMatchIn(body).getOrElse(fail("Couldn't match the accept header in headers.yaml"))

        val versionFromHeader = header.group(1)
        versionFromHeader shouldBe version.name
      }
    }
  }

  private def get(path: String): WSResponse = {
    val response: WSResponse = await(buildRequest(path).get())
    response.status shouldBe OK
    response
  }

}
