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
import shared.config.{AppConfig, FeatureSwitches}

import javax.inject.Inject

case class CalculationsFeatureSwitches @Inject() (protected val featureSwitchConfig: Configuration) extends FeatureSwitches {

  val isR8bSpecificApiEnabled: Boolean             = isEnabled("r8b-api")
  val isRetrieveSAAdditionalFieldsEnabled: Boolean = isEnabled("retrieveSAAdditionalFields")
  val isCl290Enabled: Boolean                      = isEnabled("cl290")
  val isBasicRateDivergenceEnabled: Boolean        = isEnabled("basicRateDivergence")
  val isDesIf_MigrationEnabled: Boolean            = isEnabled("desIf_Migration")
}

object CalculationsFeatureSwitches {
  def apply()(implicit appConfig: AppConfig): CalculationsFeatureSwitches = CalculationsFeatureSwitches(appConfig.featureSwitchConfig)
}
