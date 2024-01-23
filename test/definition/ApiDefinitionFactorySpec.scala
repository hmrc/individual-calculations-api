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

package definition

import cats.implicits.catsSyntaxValidatedId
import config.ConfidenceLevelConfig
import config.Deprecation.NotDeprecated
import definition.APIStatus.{ALPHA, BETA}
import mocks.{MockAppConfig, MockHttpClient}
import play.api.Configuration
import routing.{Version3, Version4, Version5}
import support.UnitSpec
import uk.gov.hmrc.auth.core.ConfidenceLevel

class ApiDefinitionFactorySpec extends UnitSpec {

  class Test extends MockHttpClient with MockAppConfig {
    val apiDefinitionFactory = new ApiDefinitionFactory(mockAppConfig)
    MockAppConfig.apiGatewayContext returns "api.gateway.context"
  }

  private val confidenceLevel: ConfidenceLevel = ConfidenceLevel.L200

  "definition" when {
    "called" should {
      "return a valid Definition case class" in new Test {
        Seq(Version3, Version4, Version5)
          .foreach { version =>
            MockAppConfig.apiStatus(version).returns("BETA")
            MockAppConfig.endpointsEnabled(version).returns(true).anyNumberOfTimes()
            MockAppConfig.deprecationFor(version).returns(NotDeprecated.valid).anyNumberOfTimes()
          }
        MockAppConfig.featureSwitches.returns(Configuration.empty).anyNumberOfTimes()
        MockAppConfig.confidenceLevelCheckEnabled
          .returns(ConfidenceLevelConfig(confidenceLevel = confidenceLevel, definitionEnabled = true, authValidationEnabled = true))
          .anyNumberOfTimes()

        apiDefinitionFactory.definition shouldBe Definition(
          scopes = Seq(
            Scope(
              key = "read:self-assessment",
              name = "View your Self Assessment information",
              description = "Allow read access to self assessment data",
              confidenceLevel
            ),
            Scope(
              key = "write:self-assessment",
              name = "Change your Self Assessment information",
              description = "Allow write access to self assessment data",
              confidenceLevel
            )
          ),
          api = APIDefinition(
            name = "Individual Calculations (MTD)",
            description = "An API for providing individual calculations data",
            context = "api.gateway.context",
            categories = Seq("INCOME_TAX_MTD"),
            versions = Seq(
              APIVersion(
                version = Version3,
                status = APIStatus.BETA,
                endpointsEnabled = true
              ),
              APIVersion(
                version = Version4,
                status = APIStatus.BETA,
                endpointsEnabled = true
              ),
              APIVersion(
                version = Version5,
                status = APIStatus.BETA,
                endpointsEnabled = true
              )
            ),
            requiresTrust = None
          )
        )
      }
    }
  }

  "confidenceLevel" when {
    Seq(
      (true, ConfidenceLevel.L250, ConfidenceLevel.L250),
      (true, ConfidenceLevel.L200, ConfidenceLevel.L200),
      (false, ConfidenceLevel.L200, ConfidenceLevel.L50)
    ).foreach { case (definitionEnabled, configCL, expectedDefinitionCL) =>
      s"confidence-level-check.definition.enabled is $definitionEnabled and confidence-level = $configCL" should {
        s"return confidence level $expectedDefinitionCL" in new Test {
          MockAppConfig.confidenceLevelCheckEnabled returns ConfidenceLevelConfig(
            confidenceLevel = configCL,
            definitionEnabled = definitionEnabled,
            authValidationEnabled = true)
          apiDefinitionFactory.confidenceLevel shouldBe expectedDefinitionCL
        }
      }
    }
  }

  "buildAPIStatus" when {
    "the 'apiStatus' parameter is present and valid" should {
      "return the correct status" in new Test {
        MockAppConfig.apiStatus(Version5) returns "BETA"
        MockAppConfig
          .deprecationFor(Version5)
          .returns(NotDeprecated.valid)
          .anyNumberOfTimes()
        apiDefinitionFactory.buildAPIStatus(Version5) shouldBe BETA
      }
    }

    "the 'apiStatus' parameter is present and invalid" should {
      "default to alpha" in new Test {
        MockAppConfig.apiStatus(Version5) returns "ALPHO"
        MockAppConfig
          .deprecationFor(Version5)
          .returns(NotDeprecated.valid)
          .anyNumberOfTimes()
        apiDefinitionFactory.buildAPIStatus(Version5) shouldBe ALPHA
      }
    }

    "the 'deprecatedOn' parameter is missing for a deprecated version" should {
      "throw exception" in new Test {
        MockAppConfig.apiStatus(Version3) returns "DEPRECATED"
        MockAppConfig
          .deprecationFor(Version3)
          .returns("deprecatedOn date is required for a deprecated version".invalid)
          .anyNumberOfTimes()

        val exception: Exception = intercept[Exception] {
          apiDefinitionFactory.buildAPIStatus(Version3)
        }

        val exceptionMessage: String = exception.getMessage
        exceptionMessage shouldBe "deprecatedOn date is required for a deprecated version"
      }
    }
  }

}
