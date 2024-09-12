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
import shared.utils.UnitSpec

class FeatureSwitchesSpec extends UnitSpec with FeatureSwitchesBehaviour[FeatureSwitches] {

  override def featureSwitches(configuration: Configuration): FeatureSwitches = new FeatureSwitches {
    override protected val featureSwitchConfig: Configuration = configuration
  }

  "isEnabled" should {
    behave like aFeatureSwitchWithKey("some-feature.enabled", _.isEnabled("some-feature"))
  }

  "isReleasedInProduction" should {
    behave like aFeatureSwitchWithKey("some-feature.released-in-production", _.isReleasedInProduction("some-feature"))
  }

}
