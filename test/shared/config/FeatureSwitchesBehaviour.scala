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

import play.api.Configuration
import shared.utils.UnitSpec

trait FeatureSwitchesBehaviour[FS <: FeatureSwitches] {
  _: UnitSpec =>

  def featureSwitches(configuration: Configuration): FS

  def aFeatureSwitchWithKey(key: String, evaluateSwitch: FS => Boolean): Unit =
    s"a feature switch with key $key" should {
      def config(value: Boolean) = Configuration(key -> value)

      "be true" when {
        "absent from the config" in {
          val fs = featureSwitches(Configuration.empty)
          evaluateSwitch(fs) shouldBe true
        }

        "enabled" in {
          val fs = featureSwitches(config(true))
          evaluateSwitch(fs) shouldBe true
        }
      }

      "be false" when {
        "disabled" in {
          val fs = featureSwitches(config(false))
          evaluateSwitch(fs) shouldBe false
        }
      }
    }

}
