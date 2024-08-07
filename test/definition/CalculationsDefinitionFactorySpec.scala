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
import common.definition.CalculationsDefinitionFactory
import shared.config.MockAppConfig
import shared.config.Deprecation.NotDeprecated
import mocks.MockCalculationsConfig
import shared.mocks.MockHttpClient
import shared.routing.{Version4, Version5, Version6}
import shared.utils.UnitSpec

class CalculationsDefinitionFactorySpec extends UnitSpec {

  class Test extends MockHttpClient with MockAppConfig with MockCalculationsConfig {
    val apiDefinitionFactory = new CalculationsDefinitionFactory(mockAppConfig)
    MockAppConfig.apiGatewayContext returns "api.gateway.context"
  }

  "definition" when {
    "called" should {
      "return a valid Definition case class" in new Test {
        List(Version4, Version5, Version6).foreach { version =>
          MockAppConfig.apiStatus(version) returns "BETA"
          MockAppConfig.endpointsEnabled(version).returns(true).anyNumberOfTimes()
          MockAppConfig.deprecationFor(version).returns(NotDeprecated.valid).anyNumberOfTimes()
        }

      }
    }
  }

  "buildAPIStatus" when {
    "the 'apiStatus' parameter is present and valid" should {
      "return the correct status" in new Test {
        MockAppConfig.apiStatus(Version6) returns "BETA"
        MockAppConfig
          .deprecationFor(Version6)
          .returns(NotDeprecated.valid)
          .anyNumberOfTimes()
      }
    }

    "the 'apiStatus' parameter is present and invalid" should {
      "default to alpha" in new Test {
        MockAppConfig.apiStatus(Version6) returns "ALPHO"
        MockAppConfig
          .deprecationFor(Version6)
          .returns(NotDeprecated.valid)
          .anyNumberOfTimes()
      }
    }
  }

}
