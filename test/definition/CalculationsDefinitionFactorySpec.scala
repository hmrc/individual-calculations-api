/*
 * Copyright 2025 HM Revenue & Customs
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
import common.definition.CalculationsDefinitionFactory
import shared.config.Deprecation.NotDeprecated
import shared.config.MockAppConfig
import shared.definition.APIStatus.{ALPHA, BETA}
import shared.definition.{APIDefinition, APIVersion, Definition}
import shared.mocks.MockHttpClient
import shared.routing.{Version5, Version6, Version7, Version8}
import shared.utils.UnitSpec

class CalculationsDefinitionFactorySpec extends UnitSpec with MockHttpClient with MockAppConfig {

  private val validVersions = Seq(Version5, Version6, Version7, Version8)

  "CalculationsDefinitionFactory" when {

    "definition is called" should {
      "return a valid Definition case class when all versions are configured correctly" in {
        MockedAppConfig.apiGatewayContext returns "api.gateway.context"

        validVersions.foreach { version =>
          MockedAppConfig.apiStatus(version) returns "BETA"
          MockedAppConfig.endpointsEnabled(version) returns true
          MockedAppConfig.deprecationFor(version).returns(NotDeprecated.valid).anyNumberOfTimes()
        }

        val factory = CalculationsDefinitionFactory(mockAppConfig)

        factory.definition shouldBe Definition(
          api = APIDefinition(
            name = "Individual Calculations (MTD)",
            description = "An API for providing individual calculations data",
            context = "api.gateway.context",
            categories = Seq("INCOME_TAX_MTD"),
            versions = validVersions.map { version =>
              APIVersion(
                version = version,
                status = BETA,
                endpointsEnabled = true
              )
            },
            requiresTrust = None
          )
        )
      }

      "default to ALPHA status when an invalid apiStatus is configured" in {
        val faultyVersion = Version6

        MockedAppConfig.apiGatewayContext returns "api.gateway.context"

        validVersions.foreach { version =>
          val status = if (version == faultyVersion) "ALPHO" else "BETA"
          MockedAppConfig.apiStatus(version) returns status
          MockedAppConfig.endpointsEnabled(version) returns true
          MockedAppConfig.deprecationFor(version).returns(NotDeprecated.valid).anyNumberOfTimes()
        }

        val factory = CalculationsDefinitionFactory(mockAppConfig)

        val resultVersions = factory.definition.api.versions

        resultVersions.find(_.version == faultyVersion).get.status shouldBe ALPHA
        resultVersions.filterNot(_.version == faultyVersion).foreach { v =>
          v.status shouldBe BETA
        }
      }
    }
  }

}
