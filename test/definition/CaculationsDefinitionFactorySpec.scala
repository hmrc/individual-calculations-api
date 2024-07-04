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
import shared.definition.{APIDefinition, APIStatus, APIVersion, Definition, Scope}
import common.definition.CaculationsDefinitionFactory
import shared.config.ConfidenceLevelConfig
import shared.config.Deprecation.NotDeprecated
import mocks.MockCaculationsConfig
import shared.mocks.MockHttpClient
import play.api.Configuration
import shared.definition.APIStatus.{ALPHA, BETA}
import shared.routing.{Version4, Version5, Version6}
import shared.utils.UnitSpec
import uk.gov.hmrc.auth.core.ConfidenceLevel

class CaculationsDefinitionFactorySpec extends UnitSpec {

  private val confidenceLevel: ConfidenceLevel = ConfidenceLevel.L200

  class Test extends MockHttpClient with MockCaculationsConfig {
    val apiDefinitionFactory = new CaculationsDefinitionFactory(mockCalculationsConfig)
    MockCaculationsConfig.apiGatewayContext returns "api.gateway.context"
  }

  "definition" when {
    "called" should {
      "return a valid Definition case class" in new Test {
        List(Version4, Version5, Version6).foreach { version =>
          MockCaculationsConfig.apiStatus(version) returns "BETA"
          MockCaculationsConfig.endpointsEnabled(version).returns(true).anyNumberOfTimes()
          MockCaculationsConfig.deprecationFor(version).returns(NotDeprecated.valid).anyNumberOfTimes()
        }

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
          MockCaculationsConfig.confidenceLevelCheckEnabled returns ConfidenceLevelConfig(
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
        MockCaculationsConfig.apiStatus(Version6) returns "BETA"
        MockCaculationsConfig
          .deprecationFor(Version6)
          .returns(NotDeprecated.valid)
          .anyNumberOfTimes()
        apiDefinitionFactory.buildAPIStatus(Version6) shouldBe BETA
      }
    }

    "the 'apiStatus' parameter is present and invalid" should {
      "default to alpha" in new Test {
        MockCaculationsConfig.apiStatus(Version6) returns "ALPHO"
        MockCaculationsConfig
          .deprecationFor(Version6)
          .returns(NotDeprecated.valid)
          .anyNumberOfTimes()
        apiDefinitionFactory.buildAPIStatus(Version6) shouldBe ALPHA
      }
    }

    "the 'deprecatedOn' parameter is missing for a deprecated version" should {
      "throw exception" in new Test {
        MockCaculationsConfig.apiStatus(Version5) returns "DEPRECATED"
        MockCaculationsConfig
          .deprecationFor(Version5)
          .returns("deprecatedOn date is required for a deprecated version".invalid)
          .anyNumberOfTimes()

        val exception: Exception = intercept[Exception] {
          apiDefinitionFactory.buildAPIStatus(Version5)
        }

        val exceptionMessage: String = exception.getMessage
        exceptionMessage shouldBe "deprecatedOn date is required for a deprecated version"
      }
    }
  }

}
