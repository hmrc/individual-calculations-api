/*
 * Copyright 2022 HM Revenue & Customs
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

package mocks

import config.{AppConfig, ConfidenceLevelConfig}
import org.scalamock.handlers.CallHandler
import org.scalamock.scalatest.MockFactory
import play.api.Configuration

trait MockAppConfig extends MockFactory {
  val mockAppConfig: AppConfig = mock[AppConfig]

  object MockAppConfig {
    // MTD ID Lookup Config
    def mtdIdBaseUrl: CallHandler[String] = (mockAppConfig.mtdIdBaseUrl _: () => String).expects()

    // Backend Config
    def backendBaseUrl: CallHandler[String] = (mockAppConfig.backendBaseUrl _: () => String).expects()

    // DES Config
    def downstreamBaseUrl: CallHandler[String] = (mockAppConfig.downstreamBaseUrl _: () => String).expects()
    def downstreamToken: CallHandler[String] = (mockAppConfig.downstreamToken _).expects()
    def downstreamEnvironment: CallHandler[String] = (mockAppConfig.downstreamEnv _).expects()
    def downstreamEnvironmentHeaders: CallHandler[Option[Seq[String]]] = (mockAppConfig.downstreamEnvironmentHeaders _).expects()

    // API Config
    def featureSwitch: CallHandler[Option[Configuration]] = (mockAppConfig.featureSwitch _: () => Option[Configuration]).expects()
    def apiGatewayContext: CallHandler[String] = (mockAppConfig.apiGatewayContext _: () => String).expects()
    def apiStatus(status: String): CallHandler[String] = (mockAppConfig.apiStatus: String => String).expects(status)
    def endpointsEnabled(version: String): CallHandler[Boolean] = (mockAppConfig.endpointsEnabled: String => Boolean).expects(version)
    def confidenceLevelCheckEnabled: CallHandler[ConfidenceLevelConfig] = (mockAppConfig.confidenceLevelConfig _: () => ConfidenceLevelConfig).expects()

    // NRS Config
    def mtdNrsProxyBaseUrl: CallHandler[String] = (mockAppConfig.mtdNrsProxyBaseUrl _).expects()
  }

}
