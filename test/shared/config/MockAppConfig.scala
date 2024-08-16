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

package shared.config

import cats.data.Validated
import org.scalamock.handlers.{CallHandler, CallHandler0}
import org.scalamock.scalatest.MockFactory
import play.api.Configuration
import shared.routing.Version

trait MockAppConfig extends MockFactory {

  implicit val mockAppConfig: AppConfig = mock[AppConfig]

  object MockedAppConfig {
    // MTD ID Lookup Config
    def mtdIdBaseUrl: CallHandler0[String] = (() => mockAppConfig.mtdIdBaseUrl: String).expects()

    // DES Config
    def desBaseUrl: CallHandler0[String]                         = (() => mockAppConfig.desBaseUrl: String).expects()
    def desToken: CallHandler0[String]                           = (() => mockAppConfig.desToken: String).expects()
    def desEnv: CallHandler0[String]                             = (() => mockAppConfig.desEnv: String).expects()
    def desEnvironment: CallHandler0[String]                     = (() => mockAppConfig.desEnv: String).expects()
    def desEnvironmentHeaders: CallHandler0[Option[Seq[String]]] = (() => mockAppConfig.desEnvironmentHeaders: Option[Seq[String]]).expects()
    def desDownstreamConfig: CallHandler0[DownstreamConfig]      = (() => mockAppConfig.desDownstreamConfig: DownstreamConfig).expects()

    // IFS Config
    def ifsBaseUrl: CallHandler0[String]                         = (() => mockAppConfig.ifsBaseUrl: String).expects()
    def ifsToken: CallHandler0[String]                           = (() => mockAppConfig.ifsToken: String).expects()
    def ifsEnv: CallHandler0[String]                             = (() => mockAppConfig.ifsEnv: String).expects()
    def ifsEnabled: CallHandler0[Boolean]                        = (() => mockAppConfig.ifsEnabled: Boolean).expects()
    def ifsEnvironment: CallHandler0[String]                     = (() => mockAppConfig.ifsEnv: String).expects()
    def ifsEnvironmentHeaders: CallHandler0[Option[Seq[String]]] = (() => mockAppConfig.ifsEnvironmentHeaders: Option[Seq[String]]).expects()
    def ifsDownstreamConfig: CallHandler0[DownstreamConfig]      = (() => mockAppConfig.ifsDownstreamConfig: DownstreamConfig).expects()

    // TYS IFS Config
    def tysIfsBaseUrl: CallHandler0[String]                         = (() => mockAppConfig.tysIfsBaseUrl: String).expects()
    def tysIfsToken: CallHandler0[String]                           = (() => mockAppConfig.tysIfsToken: String).expects()
    def tysIfsEnv: CallHandler0[String]                             = (() => mockAppConfig.tysIfsEnv: String).expects()
    def tysIfsEnvironment: CallHandler0[String]                     = (() => mockAppConfig.tysIfsEnv: String).expects()
    def tysIfsEnvironmentHeaders: CallHandler0[Option[Seq[String]]] = (() => mockAppConfig.tysIfsEnvironmentHeaders: Option[Seq[String]]).expects()
    def tysIfsDownstreamConfig: CallHandler0[DownstreamConfig]      = (() => mockAppConfig.tysIfsDownstreamConfig: DownstreamConfig).expects()

    // API Config
    def featureSwitchConfig: CallHandler0[Configuration]         = (() => mockAppConfig.featureSwitchConfig: Configuration).expects()
    def apiGatewayContext: CallHandler0[String]                  = (() => mockAppConfig.apiGatewayContext: String).expects()
    def apiStatus(version: Version): CallHandler[String]         = (mockAppConfig.apiStatus: Version => String).expects(version)
    def endpointsEnabled(version: String): CallHandler[Boolean]  = (mockAppConfig.endpointsEnabled(_: String)).expects(version)
    def endpointsEnabled(version: Version): CallHandler[Boolean] = (mockAppConfig.endpointsEnabled: Version => Boolean).expects(version)
    def deprecationFor(version: Version): CallHandler[Validated[String, Deprecation]] = (mockAppConfig.deprecationFor(_: Version)).expects(version)
    def apiDocumentationUrl(): CallHandler[String]                                    = (() => mockAppConfig.apiDocumentationUrl: String).expects()

    def apiVersionReleasedInProduction(version: String): CallHandler[Boolean] =
      (mockAppConfig.apiVersionReleasedInProduction: String => Boolean).expects(version)

    def endpointReleasedInProduction(version: String, key: String): CallHandler[Boolean] =
      (mockAppConfig.endpointReleasedInProduction: (String, String) => Boolean).expects(version, key)

    def confidenceLevelConfig: CallHandler0[ConfidenceLevelConfig] =
      (() => mockAppConfig.confidenceLevelConfig).expects()

    def endpointAllowsSupportingAgents(endpointName: String): CallHandler[Boolean] =
      (mockAppConfig.endpointAllowsSupportingAgents(_: String)).expects(endpointName)

    def allowRequestCannotBeFulfilledHeader(version: Version): CallHandler[Boolean] =
      (mockAppConfig.allowRequestCannotBeFulfilledHeader: Version => Boolean).expects(version)

  }

}
