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

import cats.implicits.catsSyntaxValidatedId
import common.definition.CalculationsDefinitionFactory
import shared.config.Deprecation.NotDeprecated
import shared.config.MockAppConfig
import shared.definition.APIStatus.{ALPHA, BETA, RETIRED}
import shared.definition.{APIDefinition, APIVersion, Definition}
import shared.mocks.MockHttpClient
import shared.routing.*
import shared.utils.UnitSpec

class CalculationsDefinitionFactorySpec extends UnitSpec with MockHttpClient with MockAppConfig {

  "CalculationsDefinitionFactory" when {

    "definition is called" should {
      "return a valid Definition case class when all versions are configured correctly" in {
        MockedAppConfig.apiGatewayContext returns "api.gateway.context"
        MockedAppConfig.apiStatus(Version8) returns "BETA"
        MockedAppConfig.endpointsEnabled(Version8) returns true
        MockedAppConfig.deprecationFor(Version8).returns(NotDeprecated.valid).anyNumberOfTimes()

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
              )
            ),
            requiresTrust = None
          )
        )
      }

      "default to ALPHA status when an invalid apiStatus is configured" in {
        val faultyVersion = Version8

        MockedAppConfig.apiGatewayContext returns "api.gateway.context"
        MockedAppConfig.apiStatus(Version8) returns "ALPHO"
        MockedAppConfig.endpointsEnabled(Version8) returns true
        MockedAppConfig.deprecationFor(Version8).returns(NotDeprecated.valid).anyNumberOfTimes()

        val factory = CalculationsDefinitionFactory(mockAppConfig)

        val resultVersions = factory.definition.api.versions

        resultVersions.find(_.version == faultyVersion).get.status shouldBe ALPHA
      }
    }
  }

}
