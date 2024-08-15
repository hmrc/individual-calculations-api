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

import com.typesafe.config.ConfigFactory
import play.api.Configuration
import shared.utils.UnitSpec
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

trait RealAppConfig {
  _: UnitSpec =>

  protected lazy val latestEnabledApiVersion: Int =
    (99 to 1 by -1)
      .find(num => realAppConfig.safeEndpointsEnabled(s"$num.0"))
      .getOrElse(fail("Couldn't find an enabled API version in the config"))

  protected lazy val emaEndpoints: Map[String, Boolean] =
    realAppConfig.configuration
      .getOptional[Map[String, Boolean]]("api.supporting-agent-endpoints")
      .getOrElse(Map.empty)

  protected lazy val realAppConfig: AppConfig = {
    val conf           = ConfigFactory.load()
    val configuration  = Configuration(conf)
    val servicesConfig = new ServicesConfig(configuration)
    new AppConfig(servicesConfig, configuration)
  }

}
