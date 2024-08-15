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

import play.api.Configuration

trait FeatureSwitches {

  protected val featureSwitchConfig: Configuration

  def isEnabled(feature: String): Boolean = isConfigTrue(feature + ".enabled")

  def isReleasedInProduction(feature: String): Boolean = isConfigTrue(feature + ".released-in-production")

  private def isConfigTrue(key: String): Boolean = featureSwitchConfig.getOptional[Boolean](key).getOrElse(true)

  def supportingAgentsAccessControlEnabled: Boolean = isEnabled("supporting-agents-access-control")

}

/** This is just here for non-typesafe usage such as Handlebars using OasFeatureRewriter. In most cases, should use the API-specific
  * XyzFeatureSwitches class instead.
  */
case class ConfigFeatureSwitches private (protected val featureSwitchConfig: Configuration) extends FeatureSwitches

object ConfigFeatureSwitches {
  def apply()(implicit appConfig: AppConfig): ConfigFeatureSwitches = ConfigFeatureSwitches(appConfig.featureSwitchConfig)
}
