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

package config

import com.typesafe.config.Config
import play.api.{ConfigLoader, Configuration}
import routing.Version
import uk.gov.hmrc.auth.core.ConfidenceLevel
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import java.time.LocalDateTime
import java.time.format.{DateTimeFormatter, DateTimeFormatterBuilder}
import java.time.temporal.ChronoField
import javax.inject.{Inject, Singleton}

trait AppConfig {
  // MTD ID Lookup Config
  def mtdIdBaseUrl: String

  // Backend Config
  def backendBaseUrl: String

  // DES Config
  def desBaseUrl: String
  def desEnv: String
  def desToken: String
  def desEnvironmentHeaders: Option[Seq[String]]

  lazy val desDownstreamConfig: DownstreamConfig =
    DownstreamConfig(baseUrl = desBaseUrl, env = desEnv, token = desToken, environmentHeaders = desEnvironmentHeaders)

  // IFS Config
  def ifsBaseUrl: String
  def ifsEnv: String
  def ifsToken: String
  def ifsEnvironmentHeaders: Option[Seq[String]]

  lazy val ifsDownstreamConfig: DownstreamConfig =
    DownstreamConfig(baseUrl = ifsBaseUrl, env = ifsEnv, token = ifsToken, environmentHeaders = ifsEnvironmentHeaders)

  // Tax Year Specific (TYS) IFS Config
  def tysIfsBaseUrl: String
  def tysIfsEnv: String
  def tysIfsToken: String
  def tysIfsEnvironmentHeaders: Option[Seq[String]]

  lazy val taxYearSpecificIfsDownstreamConfig: DownstreamConfig =
    DownstreamConfig(baseUrl = tysIfsBaseUrl, env = tysIfsEnv, token = tysIfsToken, environmentHeaders = tysIfsEnvironmentHeaders)

  // API Config
  def apiGatewayContext: String
  def confidenceLevelConfig: ConfidenceLevelConfig
  def apiStatus(version: Version): String
  def endpointsEnabled(version: String): Boolean
  def endpointsEnabled(version: Version): Boolean
  def featureSwitches: Configuration

  def apiDocumentationUrl: String

  def isApiDeprecated(version: Version): Boolean = apiStatus(version) == "DEPRECATED"

  def deprecatedOn(version: Version): Option[LocalDateTime]

  def sunsetDate(version: Version): Option[LocalDateTime]

  def sunsetEnabled(version: Version): Boolean

  /** Currently only for OAS documentation.
    */
  def apiVersionReleasedInProduction(version: String): Boolean

  /** Currently only for OAS documentation.
    */
  def endpointReleasedInProduction(version: String, name: String): Boolean

  // NRS Config
  def mtdNrsProxyBaseUrl: String
}

@Singleton
class AppConfigImpl @Inject() (config: ServicesConfig, configuration: Configuration) extends AppConfig {

  // MTD ID Lookup Config
  val mtdIdBaseUrl: String = config.baseUrl("mtd-id-lookup")

  // Backend Config
  val backendBaseUrl: String = config.baseUrl("individual-calculations")

  // DES Config
  val desBaseUrl: String                         = config.baseUrl("des")
  val desEnv: String                             = config.getString("microservice.services.des.env")
  val desToken: String                           = config.getString("microservice.services.des.token")
  val desEnvironmentHeaders: Option[Seq[String]] = configuration.getOptional[Seq[String]]("microservice.services.des.environmentHeaders")

  // IFS Config
  val ifsBaseUrl: String                         = config.baseUrl("ifs")
  val ifsEnv: String                             = config.getString("microservice.services.ifs.env")
  val ifsToken: String                           = config.getString("microservice.services.ifs.token")
  val ifsEnvironmentHeaders: Option[Seq[String]] = configuration.getOptional[Seq[String]]("microservice.services.ifs.environmentHeaders")

  // Tax Year Specific (TYS) IFS Config
  val tysIfsBaseUrl: String                         = config.baseUrl("tys-ifs")
  val tysIfsEnv: String                             = config.getString("microservice.services.tys-ifs.env")
  val tysIfsToken: String                           = config.getString("microservice.services.tys-ifs.token")
  val tysIfsEnvironmentHeaders: Option[Seq[String]] = configuration.getOptional[Seq[String]]("microservice.services.tys-ifs.environmentHeaders")

  // API Config
  val apiGatewayContext: String                    = config.getString("api.gateway.context")
  val confidenceLevelConfig: ConfidenceLevelConfig = configuration.get[ConfidenceLevelConfig](s"api.confidence-level-check")
  def apiStatus(version: Version): String          = config.getString(s"api.${version.name}.status")
  def featureSwitches: Configuration               = configuration.getOptional[Configuration](s"feature-switch").getOrElse(Configuration.empty)
  def endpointsEnabled(version: String): Boolean   = config.getBoolean(s"api.$version.endpoints.enabled")
  def endpointsEnabled(version: Version): Boolean  = config.getBoolean(s"api.${version.name}.endpoints.enabled")

  private val DATE_FORMATTER = new DateTimeFormatterBuilder()
    .append(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    .parseDefaulting(ChronoField.HOUR_OF_DAY, 23)
    .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 59)
    .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 59)
    .toFormatter()

  def deprecatedOn(version: Version): Option[LocalDateTime] = configuration.getOptional[String](s"api.$version.deprecatedOn") match {
    case Some(value) => Some(LocalDateTime.parse(value, DATE_FORMATTER))
    case None        => None
  }

  def sunsetDate(version: Version): Option[LocalDateTime] = configuration.getOptional[String](s"api.$version.sunsetDate") match {
    case Some(value) => Some(LocalDateTime.parse(value, DATE_FORMATTER))
    case None        => None
  }

  override def sunsetEnabled(version: Version): Boolean = configuration.getOptional[Boolean](s"api.$version.sunsetEnabled") match {
    case Some(value) => value
    case None        => true
  }
  def apiVersionReleasedInProduction(version: String): Boolean = config.getBoolean(s"api.$version.endpoints.api-released-in-production")
  val apiDocumentationUrl: String =
    config.getConfString("api.documentation-url", defString = "https://developer.service.hmrc.gov.uk/api-documentation/docs/api")
  def endpointReleasedInProduction(version: String, name: String): Boolean = {
    val versionReleasedInProd = apiVersionReleasedInProduction(version)
    val path                  = s"api.$version.endpoints.released-in-production.$name"

    val conf = configuration.underlying
    if (versionReleasedInProd && conf.hasPath(path)) config.getBoolean(path) else versionReleasedInProd
  }

  // NRS Config
  val mtdNrsProxyBaseUrl: String = config.baseUrl("mtd-api-nrs-proxy")
}

trait FixedConfig {
  // Minimum tax year for MTD
  val minimumTaxYear = 2018
}

case class ConfidenceLevelConfig(confidenceLevel: ConfidenceLevel, definitionEnabled: Boolean, authValidationEnabled: Boolean)

object ConfidenceLevelConfig {

  implicit val configLoader: ConfigLoader[ConfidenceLevelConfig] = (rootConfig: Config, path: String) => {
    val config = rootConfig.getConfig(path)
    ConfidenceLevelConfig(
      confidenceLevel = ConfidenceLevel.fromInt(config.getInt("confidence-level")).getOrElse(ConfidenceLevel.L200),
      definitionEnabled = config.getBoolean("definition.enabled"),
      authValidationEnabled = config.getBoolean("auth-validation.enabled")
    )
  }

}
