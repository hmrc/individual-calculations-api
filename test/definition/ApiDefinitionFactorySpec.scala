/*
 * Copyright 2020 HM Revenue & Customs
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

import com.typesafe.config.ConfigFactory
import definition.APIStatus.{ALPHA, BETA}
import play.api.Configuration
import support.UnitSpec
import v1.mocks.{MockAppConfig, MockHttpClient}

class ApiDefinitionFactorySpec extends UnitSpec {

  class Test extends MockHttpClient with MockAppConfig {
    val apiDefinitionFactory = new ApiDefinitionFactory(mockAppConfig)
    MockedAppConfig.apiGatewayContext returns "api.gateway.context"
  }

  "buildAPIStatus" when {
    "the 'apiStatus' parameter is present and valid" should {
      "return the correct status" in new Test {
        MockedAppConfig.apiStatus returns "BETA"
        apiDefinitionFactory.buildAPIStatus("1.0") shouldBe BETA
      }
    }

    "the 'apiStatus' parameter is present and invalid" should {
      "default to alpha" in new Test {
        MockedAppConfig.apiStatus returns "ALPHO"
        apiDefinitionFactory.buildAPIStatus("1.0") shouldBe ALPHA
      }
    }
  }

  "buildWhiteListingAccess" when {
    "the 'featureSwitch' parameter is not present" should {
      "return None" in new Test {
        MockedAppConfig.featureSwitch returns None
        apiDefinitionFactory.buildWhiteListingAccess() shouldBe None
      }
    }

    "the 'featureSwitch' parameter is present and white listing is enabled" should {
      "return the correct Access object" in new Test {

        MockedAppConfig.featureSwitch.returns(Some(Configuration(ConfigFactory.parseString(
          """|white-list.enabled = true
             |white-list.applicationIds = ["anId"]
             |""".stripMargin))))

        apiDefinitionFactory.buildWhiteListingAccess() shouldBe Some(Access("PRIVATE", Seq("anId")))
      }
    }

    "the 'featureSwitch' parameter is present and white listing is not enabled" should {
      "return None" in new Test {
        MockedAppConfig.featureSwitch.returns(Some(Configuration(ConfigFactory.parseString("""|white-list.enabled = false""".stripMargin))))
        apiDefinitionFactory.buildWhiteListingAccess() shouldBe None
      }
    }
  }

}
