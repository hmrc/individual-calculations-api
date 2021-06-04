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

package definition

import config.ConfidenceLevelConfig
import definition.APIStatus.{ALPHA, BETA}
import definition.Versions.{VERSION_1, VERSION_2}
import mocks.{MockAppConfig, MockHttpClient}
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
        MockAppConfig.featureSwitch returns None anyNumberOfTimes()
        MockAppConfig.apiStatus("1.0") returns "BETA"
        MockAppConfig.apiStatus("2.0") returns "ALPHA"
        MockAppConfig.endpointsEnabled("1") returns true anyNumberOfTimes()
        MockAppConfig.endpointsEnabled("2") returns true anyNumberOfTimes()
        MockAppConfig.confidenceLevelCheckEnabled returns ConfidenceLevelConfig(definitionEnabled = true, authValidationEnabled = true) anyNumberOfTimes()

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
                version = VERSION_1, access = None, status = APIStatus.BETA, endpointsEnabled = true),
              APIVersion(
                version = VERSION_2, access = None, status = APIStatus.ALPHA, endpointsEnabled = true)
            ),
            requiresTrust = None
          )
        )
      }
    }
  }

  "confidenceLevel" when {
    Seq(
      (true, ConfidenceLevel.L200),
      (false, ConfidenceLevel.L50)
    ).foreach {
      case (definitionEnabled, cl) =>
        s"confidence-level-check.definition.enabled is $definitionEnabled in config" should {
          s"return $cl" in new Test {
            MockAppConfig.confidenceLevelCheckEnabled returns ConfidenceLevelConfig(definitionEnabled = definitionEnabled, authValidationEnabled = true)
            apiDefinitionFactory.confidenceLevel shouldBe cl
          }
        }
    }
  }

  "buildAPIStatus" when {
    "the 'apiStatus' parameter is present and valid" should {
      "return the correct status" in new Test {
        MockAppConfig.apiStatus("1.0") returns "BETA"
        apiDefinitionFactory.buildAPIStatus(version = "1.0") shouldBe BETA
      }
    }

    "the 'apiStatus' parameter is present and invalid" should {
      "default to alpha" in new Test {
        MockAppConfig.apiStatus("1.0") returns "ALPHA"
        apiDefinitionFactory.buildAPIStatus(version = "1.0") shouldBe ALPHA
      }
    }
  }
}