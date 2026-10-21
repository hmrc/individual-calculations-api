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

package api.definition

import api.config.Deprecation.NotDeprecated
import api.config.MockAppConfig
import api.definition.APIAccessType.{CONTROLLED, PUBLIC}
import api.definition.APIStatus.{ALPHA, BETA, RETIRED}
import api.mocks.MockHttpClient
import api.routing.*
import api.utils.UnitSpec
import cats.implicits.catsSyntaxValidatedId

import scala.language.reflectiveCalls

class ApiDefinitionFactorySpec extends UnitSpec {

  "buildAPIStatus" when {
    "the 'apiStatus' parameter is present and valid" should {

      s"return the expected status" in new Test {
        setupMockConfig(Version9)
        MockedAppConfig.apiStatus(Version9) returns "BETA"

        val result: APIStatus = checkBuildApiStatus(Version9)
        result shouldBe BETA
      }

    }

    "the 'apiStatus' parameter is present but invalid" should {
      s"default to alpha" in new Test {
        MockedAppConfig.apiStatus(Version9) returns "not-a-status"
        MockedAppConfig.deprecationFor(Version9).returns(NotDeprecated.valid).anyNumberOfTimes()

        checkBuildApiStatus(Version9) shouldBe ALPHA
      }
    }

    "the 'deprecatedOn' parameter is missing for a deprecated version" should {
      "throw an exception" in new Test {
        MockedAppConfig.apiStatus(Version9) returns "DEPRECATED"

        MockedAppConfig
          .deprecationFor(Version9)
          .returns("deprecatedOn date is required for a deprecated version".invalid)
          .anyNumberOfTimes()

        val exception: Exception = intercept[Exception] {
          checkBuildApiStatus(Version9)
        }

        val exceptionMessage: String = exception.getMessage
        exceptionMessage shouldBe "deprecatedOn date is required for a deprecated version"
      }
    }
  }

  "definition" when {
    "called" should {
      "return a valid Definition case class when all versions are configured correctly" in new Test {
        setupMockConfig(Version7)
        setupMockConfig(Version8)
        setupMockConfig(Version9)

        val factory = new ApiDefinitionFactory(mockAppConfig)

        factory.definition shouldBe Definition(
          api = APIDefinition(
            name = "Individual Calculations (MTD)",
            description = "An API for providing individual calculations data",
            context = "api.gateway.context",
            categories = List("INCOME_TAX_MTD"),
            versions = List(
              APIVersion(
                version = Version7,
                status = RETIRED,
                access = CONTROLLED,
                endpointsEnabled = false
              ),
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

      "default to ALPHA status when an invalid apiStatus is configured" in new Test {
        setupMockConfig(Version7)
        val versions = List(Version8, Version9)

        MockedAppConfig.apiGatewayContext returns "api.gateway.context"

        versions.foreach { version =>
          MockedAppConfig.apiStatus(version) returns "ALPHO"
          MockedAppConfig.endpointsEnabled(version) returns true
          MockedAppConfig.controlledAccessEnabled returns false
          MockedAppConfig.deprecationFor(version) returns NotDeprecated.valid
        }

        val factory = new ApiDefinitionFactory(mockAppConfig)

        val resultVersions: Seq[APIVersion] = factory.definition.api.versions

        versions.foreach { version =>
          resultVersions.find(_.version == version).get.status shouldBe ALPHA
        }
      }
    }
  }

  "set the access level" when {
    "the controlled access flag is enabled" should {
      "to be CONTROLLED" in new Test {
        setupMockConfig(Version7)
        val versions = List(Version8, Version9)

        MockedAppConfig.apiGatewayContext returns "api.gateway.context"

        versions.foreach { version =>
          MockedAppConfig.apiStatus(version) returns "BETA"
          MockedAppConfig.endpointsEnabled(version) returns true
          MockedAppConfig.controlledAccessEnabled returns true
          MockedAppConfig.deprecationFor(version) returns NotDeprecated.valid
        }

        val factory = new ApiDefinitionFactory(mockAppConfig)

        val resultVersions: Seq[APIVersion] = factory.definition.api.versions

        versions.foreach { version =>
          resultVersions.find(_.version == version).get.access shouldBe CONTROLLED
        }
      }
    }

    "the controlled access flag is disabled" should {
      "return PUBLIC" in new Test {
        setupMockConfig(Version7)
        val versions = List(Version8, Version9)

        MockedAppConfig.apiGatewayContext returns "api.gateway.context"

        versions.foreach { version =>
          MockedAppConfig.apiStatus(version) returns "BETA"
          MockedAppConfig.endpointsEnabled(version) returns true
          MockedAppConfig.controlledAccessEnabled returns false
          MockedAppConfig.deprecationFor(version) returns NotDeprecated.valid
        }

        val factory = new ApiDefinitionFactory(mockAppConfig)

        val resultVersions: Seq[APIVersion] = factory.definition.api.versions

        versions.foreach { version =>
          resultVersions.find(_.version == version).get.access shouldBe PUBLIC
        }
      }
    }
  }

  trait Test extends UnitSpec with MockHttpClient with MockAppConfig {
    protected val apiDefinitionFactory = new ApiDefinitionFactory(mockAppConfig)

    protected def checkBuildApiStatus(version: Version): APIStatus =
      apiDefinitionFactory.buildAPIStatus(version)

    protected def setupMockConfig(version: Version): Unit = {
      MockedAppConfig.apiGatewayContext returns "api.gateway.context"

      if (version == Version7) {
        MockedAppConfig.apiStatus(version) returns "RETIRED"
        MockedAppConfig.endpointsEnabled(version) returns true
        MockedAppConfig.controlledAccessEnabled returns true
        MockedAppConfig.deprecationFor(version) returns NotDeprecated.valid
      } else {
        MockedAppConfig.apiStatus(version) returns "BETA"
        MockedAppConfig.endpointsEnabled(version) returns true
        MockedAppConfig.controlledAccessEnabled returns false
        MockedAppConfig.deprecationFor(version) returns NotDeprecated.valid
      }
    }

  }

}
