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

package shared.connectors

import org.scalatest.concurrent.ScalaFutures
import play.api.Configuration
import shared.config.{BasicAuthDownstreamConfig, DownstreamConfig, MockAppConfig}
import shared.utils.UnitSpec

import scala.concurrent.ExecutionContext.Implicits.global

class DownstreamStrategySpec extends UnitSpec with ScalaFutures with MockAppConfig {

  "StandardStrategy" must {
    "use the supplied DownstreamConfig" in {
      val downstreamConfig =
        DownstreamConfig(baseUrl = "someBaseUrl", env = "someEnv", token = "someToken", environmentHeaders = Some(Seq("header1", "header2")))

      val strategy = DownstreamStrategy.standardStrategy(downstreamConfig)

      strategy.baseUrl shouldBe "someBaseUrl"
      strategy.contractHeaders("someCorrelationId").futureValue should contain theSameElementsAs
        Seq(
          "Authorization" -> "Bearer someToken",
          "Environment"   -> "someEnv",
          "CorrelationId" -> "someCorrelationId"
        )
      strategy.environmentHeaders should contain theSameElementsAs Seq("header1", "header2")
    }
  }

  "BasicAuthStrategy" must {
    "use the supplied BasicAuthDownstreamConfig and ClientAuthConfig" in {
      val downstreamConfig =
        BasicAuthDownstreamConfig(
          baseUrl = "someBaseUrl",
          env = "someEnv",
          clientId = "someClient",
          clientSecret = "someSecret",
          environmentHeaders = Some(Seq("header1", "header2")))

      val strategy = DownstreamStrategy.basicAuthStrategy(downstreamConfig)

      strategy.baseUrl shouldBe "someBaseUrl"
      strategy.contractHeaders("someCorrelationId").futureValue should contain theSameElementsAs
        Seq(
          "Authorization" -> "Basic c29tZUNsaWVudDpzb21lU2VjcmV0",
          "Environment"   -> "someEnv",
          "CorrelationId" -> "someCorrelationId"
        )
      strategy.environmentHeaders should contain theSameElementsAs Seq("header1", "header2")

    }
  }

  "switched strategy" must {
    val onStrategy  = mock[DownstreamStrategy]
    val offStrategy = mock[DownstreamStrategy]

    "use the provided onStrategy when the switch is enabled" in {
      MockedAppConfig.featureSwitchConfig.returns(Configuration("someSwitch.enabled" -> true))

      DownstreamStrategy.switchedStrategy(onStrategy, offStrategy, "someSwitch") shouldBe onStrategy
    }

    "use the provided offStrategy when the switch is disabled" in {
      MockedAppConfig.featureSwitchConfig.returns(Configuration("someSwitch.enabled" -> false))

      DownstreamStrategy.switchedStrategy(onStrategy, offStrategy, "someSwitch") shouldBe offStrategy
    }
  }

}
