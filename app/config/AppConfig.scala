/*
 * Copyright 2021 HM Revenue & Customs
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

package config

import com.typesafe.config.Config
import play.api.{ConfigLoader, Configuration}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import javax.inject.{Inject, Singleton}


trait AppConfig {
  def mtdIdBaseUrl: String

  def backendBaseUrl: String

  def desBaseUrl: String

  def desEnv: String

  def desToken: String

  def apiGatewayContext: String

  def apiStatus(version: String): String

  def endpointsEnabled(version: String): Boolean

  def featureSwitch: Option[Configuration]

  def confidenceLevelConfig: ConfidenceLevelConfig
}

@Singleton
class AppConfigImpl @Inject()(config: ServicesConfig, configuration: Configuration) extends AppConfig {
  val mtdIdBaseUrl: String = config.baseUrl("mtd-id-lookup")
  val backendBaseUrl: String = config.baseUrl("individual-calculations")
  val desBaseUrl: String = config.baseUrl("des")
  val desEnv: String = config.getString("microservice.services.des.env")
  val desToken: String = config.getString("microservice.services.des.token")
  val apiGatewayContext: String = config.getString("api.gateway.context")

  def apiStatus(version: String): String = config.getString(s"api.$version.status")

  def featureSwitch: Option[Configuration] = configuration.getOptional[Configuration](s"feature-switch")

  def endpointsEnabled(version: String): Boolean = config.getBoolean(s"feature-switch.version-$version.enabled")

  val confidenceLevelConfig: ConfidenceLevelConfig = configuration.get[ConfidenceLevelConfig](s"api.confidence-level-check")
}

trait FixedConfig {
  // Minimum tax year for MTD
  val minimumTaxYear = 2018
}

case class ConfidenceLevelConfig(definitionEnabled: Boolean, authValidationEnabled: Boolean)

object ConfidenceLevelConfig {
  implicit val configLoader: ConfigLoader[ConfidenceLevelConfig] = (rootConfig: Config, path: String) => {
    val config = rootConfig.getConfig(path)
    ConfidenceLevelConfig(
      definitionEnabled = config.getBoolean("definition.enabled"),
      authValidationEnabled = config.getBoolean("auth-validation.enabled")
    )
  }
}
