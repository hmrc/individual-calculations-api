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

import play.api.Configuration

case class FeatureSwitches(featureSwitchConfig: Configuration) {

  val isR8bSpecificApiEnabled: Boolean             = isEnabled("r8b-api")
  val isRetrieveSAAdditionalFieldsEnabled: Boolean = isEnabled("retrieveSAAdditionalFields")
  val isCl290Enabled: Boolean                      = isEnabled("cl290")
  val isBasicRateDivergenceEnabled: Boolean        = isEnabled("basicRateDivergence")
  val isDesIf_MigrationEnabled: Boolean            = isEnabled("desIf_Migration")

  def isReleasedInProduction(feature: String): Boolean = isConfigTrue(feature + ".released-in-production")
  def isEnabled(feature: String): Boolean              = isConfigTrue(feature + ".enabled")

  private def isConfigTrue(feature: String): Boolean = featureSwitchConfig.getOptional[Boolean](feature).getOrElse(true)
}

object FeatureSwitches {
  def apply()(implicit appConfig: AppConfig): FeatureSwitches = FeatureSwitches(appConfig.featureSwitches)
}
