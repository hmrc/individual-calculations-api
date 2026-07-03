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
import api.definition.APIStatus.{ALPHA, BETA, RETIRED}
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
                version = Version7,
                status = RETIRED,
                endpointsEnabled = false
              ),
              APIVersion(
                version = Version8,
                status = BETA,
                endpointsEnabled = true
              ),
              APIVersion(
                version = Version9,
                status = BETA,
                endpointsEnabled = true
              )
            ),
            requiresTrust = None
          )
        )
      }

      "default to ALPHA status when an invalid apiStatus is configured" in {
        val faultyVersion = Version8

        MockedAppConfig.apiGatewayContext returns "api.gateway.context"
        List(
          Version8 -> "ALPHO",
          Version9 -> "BETA"
        ).foreach { case (version, status) =>
          MockedAppConfig.apiStatus(version) returns status
          MockedAppConfig.endpointsEnabled(version) returns true
          MockedAppConfig.deprecationFor(version).returns(NotDeprecated.valid).anyNumberOfTimes()
        }

        val factory = CalculationsDefinitionFactory(mockAppConfig)

        val resultVersions = factory.definition.api.versions

        resultVersions.find(_.version == faultyVersion).get.status shouldBe ALPHA
      }
    }
  }

}
