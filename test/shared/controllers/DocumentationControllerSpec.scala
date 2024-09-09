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

package shared.controllers

import com.typesafe.config.ConfigFactory
import controllers.{AssetsConfiguration, DefaultAssetsMetadata, RewriteableAssets}
import play.api.http.{DefaultFileMimeTypes, DefaultHttpErrorHandler, FileMimeTypesConfiguration, HttpConfiguration}
import play.api.mvc.Result
import play.api.{Configuration, Environment}
import shared.config.rewriters.DocumentationRewriters.CheckAndRewrite
import shared.config.rewriters._
import shared.config.{AppConfig, MockAppConfig, RealAppConfig}
import shared.definition._
import shared.routing.{Version, Versions}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DocumentationControllerSpec extends ControllerBaseSpec with MockAppConfig with RealAppConfig {

  private val apiVersionName = s"$latestEnabledApiVersion.0"

  override protected val apiVersion: Version =
    Versions
      .getFrom(apiVersionName)
      .getOrElse(fail(s"Matching Version object not found for $apiVersionName"))

  private val titleLineMatcher = """(.*title:.*)""".r
  private val titleMatcher     = """^(\s*title:\s*".*?\s*\[test\sonly]).*$""".r

  private val actualApplicationYaml: String = {
    val loader = Thread.currentThread().getContextClassLoader
    val stream = loader.getResourceAsStream(s"public/api/conf/${apiVersion.name}/application.yaml")
    new String(stream.readAllBytes).trim
  }

  "/file endpoint" should {
    "return a file" in new Test {
      MockedAppConfig.apiVersionReleasedInProduction(apiVersionName).anyNumberOfTimes() returns true
      MockedAppConfig.endpointsEnabled(apiVersionName).anyNumberOfTimes() returns true

      val response: Future[Result] = requestAsset("application.yaml")
      status(response) shouldBe OK
      await(response).body.contentLength.getOrElse(-99L) should be > 0L
    }

    "return a 404" when {
      "the requested asset doesn't exist" in new Test {
        MockedAppConfig.endpointReleasedInProduction(apiVersionName, "does-not-exist").anyNumberOfTimes() returns true

        val response: Future[Result] = requestAsset("does-not-exist.yaml")
        status(response) shouldBe NOT_FOUND
      }

      "the requested asset is a directory" in new Test {
        val response: Future[Result] = requestAsset("examples")
        status(response) shouldBe NOT_FOUND
      }

      "the requested asset doesn't form a canonical path" in new Test {
        MockedAppConfig.endpointReleasedInProduction(apiVersionName, "../does-not-exist").anyNumberOfTimes() returns true

        val response: Future[Result] = requestAsset("../does-not-exist.yaml")
        status(response) shouldBe NOT_FOUND
      }

    }

    "return a 400 response" when {
      "the requested asseet's URI encoding is wrong" in new Test {
        val badlyEncodedAssetName = "applica\n\ntion"
        MockedAppConfig.endpointReleasedInProduction(apiVersionName, badlyEncodedAssetName).anyNumberOfTimes() returns true
        MockedAppConfig.endpointsEnabled(apiVersionName).anyNumberOfTimes() returns true

        val response: Future[Result] = requestAsset(s"$badlyEncodedAssetName.yaml")
        status(response) shouldBe BAD_REQUEST
      }
    }
  }

  "rewrite()" when {
    "the API version is enabled" should {
      "return the yaml with the API title unchanged" in new Test {
        MockedAppConfig.apiVersionReleasedInProduction(apiVersionName).anyNumberOfTimes() returns true
        MockedAppConfig.endpointsEnabled(apiVersionName).anyNumberOfTimes() returns true

        val response: Future[Result] = requestAsset("application.yaml", accept = "text/plain")
        status(response) shouldBe OK

        private val result = contentAsString(response)

        result should include("""  title: """)
        numberOfTestOnlyOccurrences(result) shouldBe 0

        result should startWith(s"""openapi: "3.0.3"
                                   |
                                   |info:
                                   |  version: "$apiVersionName"""".stripMargin)
      }
    }

    "the API version is disabled" should {
      "return the yaml with [test only] in the API title" in new Test {
        MockedAppConfig.apiVersionReleasedInProduction(apiVersionName).anyNumberOfTimes() returns false
        MockedAppConfig.endpointsEnabled(apiVersionName).anyNumberOfTimes() returns true

        val response: Future[Result] = requestAsset("application.yaml")
        status(response) shouldBe OK

        private val result = contentAsString(response)

        private val titleLine =
          titleLineMatcher
            .findFirstIn(result)
            .getOrElse(fail("Couldn't match the API title line in application.yaml"))

        titleLine should fullyMatch regex titleMatcher

        withClue("Only the title should have [test only] appended:") {
          numberOfTestOnlyOccurrences(result) shouldBe 1
        }

        result should startWith(s"""openapi: "3.0.3"
                                  |
                                  |info:
                                  |  version: "$apiVersionName"""".stripMargin)
      }
    }

    "the API version is disabled but there are no registered rewriters" should {
      "return the yaml unmodified" in new Test {
        override protected val docRewriters: DocumentationRewriters =
          new DocumentationRewriters(null, null, null, null) {
            override lazy val rewriteables: Seq[CheckAndRewrite] = Nil
          }

        MockedAppConfig.apiVersionReleasedInProduction(apiVersionName).anyNumberOfTimes() returns false
        MockedAppConfig.endpointsEnabled(apiVersionName).anyNumberOfTimes() returns true

        actualApplicationYaml should not be empty

        val response: Future[Result] = requestAsset("application.yaml", accept = "text/plain")
        status(response) shouldBe OK

        val result: String = contentAsString(response)
        result shouldBe actualApplicationYaml
      }
    }
  }

  trait Test {
    val hc: HeaderCarrier = HeaderCarrier()

    protected def featureEnabled: Boolean = true

    protected def requestAsset(filename: String, accept: String = "text/yaml"): Future[Result] =
      controller.asset(apiVersionName, filename)(fakeGetRequest.withHeaders(ACCEPT -> accept))

    protected def numberOfTestOnlyOccurrences(str: String): Int = "\\[test only]".r.findAllIn(str).size

    MockedAppConfig.featureSwitchConfig returns Configuration("openApiFeatureTest.enabled" -> featureEnabled)

    private val apiFactory = new ApiDefinitionFactory {
      protected val appConfig: AppConfig = mockAppConfig

      val definition: Definition = Definition(
        APIDefinition(
          "test API definition",
          "description",
          "context",
          List("category"),
          List(APIVersion(apiVersion, APIStatus.BETA, endpointsEnabled = true)),
          None)
      )

    }

    private val config    = new Configuration(ConfigFactory.load())
    private val mimeTypes = HttpConfiguration.parseFileMimeTypes(config) ++ Map("yaml" -> "text/yaml", "md" -> "text/markdown")

    private val assetsMetadata =
      new DefaultAssetsMetadata(
        AssetsConfiguration(textContentTypes = Set("text/yaml", "text/markdown")),
        path => {
          Option(getClass.getResource(path))
        },
        new DefaultFileMimeTypes(FileMimeTypesConfiguration(mimeTypes))
      )

    private val errorHandler = new DefaultHttpErrorHandler()

    protected val docRewriters = new DocumentationRewriters(
      new ApiVersionTitleRewriter(mockAppConfig),
      new EndpointSummaryRewriter(mockAppConfig),
      new EndpointSummaryGroupRewriter(mockAppConfig),
      new OasFeatureRewriter()(mockAppConfig)
    )

    private val assets       = new RewriteableAssets(errorHandler, assetsMetadata, mock[Environment])
    protected def controller = new DocumentationController(apiFactory, docRewriters, assets, cc)
  }

}
