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
import shared.utils.UnitSpec

class FeatureSwitchesSpec extends UnitSpec {

  "a feature switch" should {
    "be true" when {

      "absent from the config" in {
        val configuration   = Configuration.empty
        val featureSwitches = CalculationsFeatureSwitches(configuration)

        featureSwitches.isR8bSpecificApiEnabled shouldBe true
        featureSwitches.isRetrieveSAAdditionalFieldsEnabled shouldBe true
        featureSwitches.isCl290Enabled shouldBe true
        featureSwitches.isBasicRateDivergenceEnabled shouldBe true
        featureSwitches.isDesIf_MigrationEnabled shouldBe true

      }

      "enabled" in {
        val configuration = Configuration(
          "r8b-api.enabled"                    -> true,
          "retrieveSAAdditionalFields.enabled" -> true,
          "cl290.enabled"                      -> true,
          "basicRateDivergence.enabled"        -> true,
          "desIf_Migration.enabled"            -> true)
        val featureSwitches = CalculationsFeatureSwitches(configuration)

        featureSwitches.isR8bSpecificApiEnabled shouldBe true
        featureSwitches.isRetrieveSAAdditionalFieldsEnabled shouldBe true
        featureSwitches.isCl290Enabled shouldBe true
        featureSwitches.isBasicRateDivergenceEnabled shouldBe true
        featureSwitches.isDesIf_MigrationEnabled shouldBe true
      }
    }

    "be false" when {
      "disabled" in {
        val configuration = Configuration(
          "r8b-api.enabled"                    -> false,
          "retrieveSAAdditionalFields.enabled" -> false,
          "cl290.enabled"                      -> false,
          "basicRateDivergence.enabled"        -> false,
          "desIf_Migration.enabled"            -> false)

        val featureSwitches = CalculationsFeatureSwitches(configuration)

        featureSwitches.isR8bSpecificApiEnabled shouldBe false
        featureSwitches.isRetrieveSAAdditionalFieldsEnabled shouldBe false
        featureSwitches.isCl290Enabled shouldBe false
        featureSwitches.isBasicRateDivergenceEnabled shouldBe false
        featureSwitches.isDesIf_MigrationEnabled shouldBe false
      }
    }
  }

}
