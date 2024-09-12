/*
 * Copyright 2024 HM Revenue & Customs
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

package shared.config

import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import com.typesafe.config.ConfigFactory
import play.api.Configuration
import shared.config.Deprecation.Deprecated
import shared.routing._
import shared.utils.UnitSpec
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import java.time.LocalDate

class AppConfigSpec extends UnitSpec {

  private val simpleAppConfig = appConfig(versionConf = "")

  "AppConfig" should {
    "return the API name" in {
      val result = simpleAppConfig.appName
      result shouldBe "any-name-api"
    }

    "return the MTD ID base URL" in {
      val result = simpleAppConfig.mtdIdBaseUrl
      result shouldBe "http://localhost:9769"
    }

    "return the DES config" in {
      val expectedDesEnvHeaders = Some(
        List(
          "Des-Accept",
          "Des-Gov-Test-Scenario",
          "Des-Content-Type"
        ))

      simpleAppConfig.desDownstreamConfig shouldBe DownstreamConfig(
        "http://127.0.0.1:6666",
        "Prod",
        "DES-ABCD1234",
        expectedDesEnvHeaders
      )
    }

    "return the IFS config" in {
      val expectedIfsEnvHeaders = Some(
        List(
          "IFS-Accept",
          "IFS-Gov-Test-Scenario",
          "IFS-Content-Type"
        ))

      simpleAppConfig.ifsDownstreamConfig shouldBe DownstreamConfig(
        "http://127.0.0.1:7777",
        "Prod",
        "IFS-ABCD1234",
        expectedIfsEnvHeaders
      )
    }

    "return the TYS-IFS config" in {
      val expectedTysIfsEnvHeaders = Some(
        List(
          "TYS-IFS-Accept",
          "TYS-IFS-Gov-Test-Scenario",
          "TYS-IFS-Content-Type"
        ))

      simpleAppConfig.tysIfsDownstreamConfig shouldBe DownstreamConfig(
        "http://127.0.0.1:8888",
        "Prod",
        "TYS-IFS-ABCD1234",
        expectedTysIfsEnvHeaders
      )
    }
  }

  "endpointsEnabled" when {
    "the API version is disabled" should {
      "return false" in {
        val appConfigWithDisabledVersion = appConfig(
          """
            |    1.0 {
            |      status = "RETIRED"
            |      endpoints {
            |        enabled = false
            |        api-released-in-production = false
            |        allow-request-cannot-be-fulfilled-header = false
            |      }
            |    }
            |""".stripMargin
        )

        val result = appConfigWithDisabledVersion.endpointsEnabled("1.0")
        result shouldBe false
      }
    }

    "the API version is enabled" should {
      "return true" in {
        val appConfigWithEnabledVersion = appConfig(
          """
            |    6.0 {
            |      status = "BETA"
            |      endpoints {
            |        enabled = true
            |        api-released-in-production = true
            |        allow-request-cannot-be-fulfilled-header = false
            |
            |        released-in-production {
            |          create-something: true
            |          amend-something: false
            |        }
            |      }
            |    }
            |
            |""".stripMargin
        )
        val result = appConfigWithEnabledVersion.endpointsEnabled("6.0")
        result shouldBe true
      }
    }
  }

  "endpointReleasedInProduction" when {
    "the API version is enabled and the config specifies the endpoint status as true" should {
      "return true" in {
        val appConfigWithReleasedVersion = appConfig(
          """
            |    6.0 {
            |      status = "BETA"
            |      endpoints {
            |        enabled = true
            |        api-released-in-production = true
            |        allow-request-cannot-be-fulfilled-header = false
            |
            |        released-in-production {
            |          create-something: true
            |          amend-something: false
            |        }
            |      }
            |    }
            |""".stripMargin
        )
        val result = appConfigWithReleasedVersion.endpointReleasedInProduction("6.0", "create-something")
        result shouldBe true
      }
    }

    "the API version is enabled and the config specifies the endpoint status as false" should {
      "return true" in {
        val appConfigWithDisabledEndpoint = appConfig(
          """
            |    6.0 {
            |      status = "BETA"
            |      endpoints {
            |        enabled = true
            |        api-released-in-production = true
            |        allow-request-cannot-be-fulfilled-header = false
            |
            |        released-in-production {
            |          create-something: true
            |          amend-something: false
            |        }
            |      }
            |    }
            |""".stripMargin
        )
        val result = appConfigWithDisabledEndpoint.endpointReleasedInProduction("6.0", "amend-something")
        result shouldBe false
      }
    }

    "the API version is enabled and the config doesn't specify the endpoint status" should {
      "return the value of endpointReleasedInProduction" in {
        val appConfigWithUnspecifiedEndpointStatus = appConfig(
          """
            |    6.0 {
            |      status = "BETA"
            |      endpoints {
            |        enabled = true
            |        api-released-in-production = true
            |        allow-request-cannot-be-fulfilled-header = false
            |
            |        released-in-production {
            |          create-something: true
            |          amend-something: false
            |        }
            |      }
            |    }
            |
            |""".stripMargin
        )
        val result = appConfigWithUnspecifiedEndpointStatus.endpointReleasedInProduction("6.0", "delete-something")
        result shouldBe true
      }
    }
  }

  "deprecationFor" when {
    "the API version is deprecated and has deprecation info" should {
      "return the expected deprection info" in {
        val appConfigWithDeprecatedVersion = appConfig(
          """
            |    1.0 {
            |      status = "DEPRECATED"
            |      deprecatedOn = "2024-01-15"
            |      sunsetDate = "2025-01-15"
            |      endpoints {
            |        enabled = true
            |        api-released-in-production = true
            |        allow-request-cannot-be-fulfilled-header = false
            |      }
            |    }
            |""".stripMargin
        )

        val result: Validated[String, Deprecation] = appConfigWithDeprecatedVersion.deprecationFor(Version1)
        result shouldBe Valid(
          Deprecated(
            deprecatedOn = LocalDate.parse("2024-01-15").plusDays(1).atStartOfDay.minusSeconds(1),
            sunsetDate = Some(LocalDate.parse("2025-01-15").plusDays(1).atStartOfDay.minusSeconds(1))
          ))

      }
    }

    "the API version is deprecated but the deprecatedOn date is invalid" should {
      "return the expected message" in {
        val appConfigWithInvalidDeprecatedDate = appConfig(
          """
            |    1.0 {
            |      status = "DEPRECATED"
            |      deprecatedOn = "2025-01-15"
            |      sunsetDate = "2024-01-15"
            |      endpoints {
            |        enabled = true
            |        api-released-in-production = true
            |        allow-request-cannot-be-fulfilled-header = false
            |      }
            |    }
            |""".stripMargin
        )
        val result: Validated[String, Deprecation] = appConfigWithInvalidDeprecatedDate.deprecationFor(Version1)
        result shouldBe Invalid("sunsetDate must be later than deprecatedOn date for a deprecated version 1.0")
      }
    }

    "the API version is deprecated but sunsetDateEnabled is explicitly false" should {
      "return the deprection info with no sunset date" in {
        val appConfigWithNoSunsetDate = appConfig(
          """
            |    1.0 {
            |      status = "DEPRECATED"
            |      deprecatedOn = "2024-01-15"
            |      sunsetEnabled = false
            |      endpoints {
            |        enabled = true
            |        api-released-in-production = true
            |        allow-request-cannot-be-fulfilled-header = false
            |      }
            |    }
            |""".stripMargin
        )

        val result: Validated[String, Deprecation] = appConfigWithNoSunsetDate.deprecationFor(Version1)
        result shouldBe Valid(
          Deprecated(
            deprecatedOn = LocalDate.parse("2024-01-15").plusDays(1).atStartOfDay.minusSeconds(1),
            sunsetDate = None
          ))
      }
    }

    "the API version is deprecated but there's no deprecatedOn date" should {
      "return the expected message" in {
        val appConfigWithNoDeprecatedDate = appConfig(
          """
            |    1.0 {
            |      status = "DEPRECATED"
            |      endpoints {
            |        enabled = true
            |        api-released-in-production = true
            |        allow-request-cannot-be-fulfilled-header = false
            |      }
            |    }
            |""".stripMargin
        )

        val result: Validated[String, Deprecation] = appConfigWithNoDeprecatedDate.deprecationFor(Version1)
        result shouldBe Invalid("deprecatedOn date is required for a deprecated version 1.0")
      }
    }
  }

  private def appConfig(versionConf: String): AppConfig = {
    val conf = ConfigFactory.parseString(
      """
        |  appName = "any-name-api"
        |  appUrl = "http://localhost:9999"
        |  
        |  api {
        |""".stripMargin ++

        versionConf ++

        """
          |  }
          |  
          |  microservice {
          |    services {
          |      mtd-id-lookup {
          |        host = localhost
          |        port = 9769
          |      }
          |
          |      des {
          |        host = 127.0.0.1
          |        port = 6666
          |        env = Prod
          |        token = DES-ABCD1234
          |        environmentHeaders = ["Des-Accept", "Des-Gov-Test-Scenario", "Des-Content-Type"]
          |      }
          |
          |      ifs {
          |        enabled = true
          |        host = 127.0.0.1
          |        port = 7777
          |        env = Prod
          |        token = IFS-ABCD1234
          |        environmentHeaders = ["IFS-Accept", "IFS-Gov-Test-Scenario", "IFS-Content-Type"]
          |      }
          |
          |      tys-ifs {
          |        host = 127.0.0.1
          |        port = 8888
          |        env = Prod
          |        token = TYS-IFS-ABCD1234
          |        environmentHeaders = ["TYS-IFS-Accept", "TYS-IFS-Gov-Test-Scenario", "TYS-IFS-Content-Type"]
          |      }
          |    }
          |  }
          |""".stripMargin
    )

    val configuration  = Configuration(conf)
    val servicesConfig = new ServicesConfig(configuration)
    new AppConfig(servicesConfig, configuration)
  }

}
