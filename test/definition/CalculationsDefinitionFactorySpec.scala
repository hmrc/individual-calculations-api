/*
 * Copyright 2026 HM Revenue & Customs
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

import api.config.Deprecation.NotDeprecated
import api.config.MockAppConfig
import api.definition.APIAccessType.{CONTROLLED, PUBLIC}
import api.definition.APIStatus.{ALPHA, BETA}
import api.definition.{APIDefinition, APIVersion, Definition}
import api.mocks.MockHttpClient
import api.routing.*
import api.utils.UnitSpec
import cats.implicits.catsSyntaxValidatedId
import common.definition.CalculationsDefinitionFactory

class CalculationsDefinitionFactorySpec extends UnitSpec with MockHttpClient with MockAppConfig {

  "CalculationsDefinitionFactory" when {

    "definition is called" should {
      "return a valid Definition case class when all versions are configured correctly" in {
        MockedAppConfig.apiGatewayContext returns "api.gateway.context"
        List(Version8, Version9).foreach { version =>
          MockedAppConfig.apiStatus(version) returns "BETA"
          MockedAppConfig.endpointsEnabled(version) returns true
          MockedAppConfig.controlledAccessEnabled returns false
          MockedAppConfig.deprecationFor(version).returns(NotDeprecated.valid).anyNumberOfTimes()
        }

        val factory = CalculationsDefinitionFactory(mockAppConfig)

        factory.definition shouldBe Definition(
          api = APIDefinition(
            name = "Individual Calculations (MTD)",
            description = "An API for providing individual calculations data",
            context = "api.gateway.context",
            categories = Seq("INCOME_TAX_MTD"),
            versions = Seq(
              APIVersion(
                version = Version8,
                status = BETA,
                access = PUBLIC,
                endpointsEnabled = true
              ),
              APIVersion(
                version = Version9,
                status = BETA,
                access = PUBLIC,
                endpointsEnabled = true
              )
            ),
            requiresTrust = None
          )
        )
      }

      "default to ALPHA status when an invalid apiStatus is configured" in {
        val versions = List(Version8, Version9)

        MockedAppConfig.apiGatewayContext returns "api.gateway.context"

        versions.foreach { version =>
          MockedAppConfig.apiStatus(version) returns "ALPHO"
          MockedAppConfig.endpointsEnabled(version) returns true
          MockedAppConfig.controlledAccessEnabled returns false
          MockedAppConfig.deprecationFor(version).returns(NotDeprecated.valid).anyNumberOfTimes()
        }

        val factory = CalculationsDefinitionFactory(mockAppConfig)

        val resultVersions = factory.definition.api.versions

        versions.foreach { version =>
          resultVersions.find(_.version == version).get.status shouldBe ALPHA
        }
      }
    }
  }

  "set the access level" when {
    "the controlled access flag is enabled" should {
      "to be CONTROLLED" in {

        val versions = List(Version8, Version9)
        MockedAppConfig.apiGatewayContext returns "api.gateway.context"
        versions.foreach { version =>
          MockedAppConfig.apiStatus(version) returns "BETA"
          MockedAppConfig.endpointsEnabled(version) returns true
          MockedAppConfig.controlledAccessEnabled returns true
          MockedAppConfig.deprecationFor(version).returns(NotDeprecated.valid).anyNumberOfTimes()
        }
        val factory = CalculationsDefinitionFactory(mockAppConfig)

        val resultVersions = factory.definition.api.versions

        versions.foreach { version =>
          resultVersions.find(_.version == version).get.access shouldBe CONTROLLED
        }
      }
    }

    "the controlled access flag is disabled" should {
      "return PUBLIC" in {
        val versions = List(Version8, Version9)
        MockedAppConfig.apiGatewayContext returns "api.gateway.context"
        versions.foreach { version =>
          MockedAppConfig.apiStatus(version) returns "BETA"
          MockedAppConfig.endpointsEnabled(version) returns true
          MockedAppConfig.controlledAccessEnabled returns false
          MockedAppConfig.deprecationFor(version).returns(NotDeprecated.valid).anyNumberOfTimes()
        }
        val factory = CalculationsDefinitionFactory(mockAppConfig)

        val resultVersions = factory.definition.api.versions

        versions.foreach { version =>
          resultVersions.find(_.version == version).get.access shouldBe PUBLIC
        }
      }
    }
  }

}
