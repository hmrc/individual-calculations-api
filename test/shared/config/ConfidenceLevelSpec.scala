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
import play.api.ConfigLoader
import shared.utils.UnitSpec
import uk.gov.hmrc.auth.core.ConfidenceLevel.L500

class ConfidenceLevelSpec extends UnitSpec {

  "configLoader" when {
    "given config that specifies the confidence-level" should {
      "return the parsed ConfidenceLevel" in {
        val conf = ConfigFactory.parseString(
          """
            |  api {
            |    confidence-level-check {
            |      confidence-level = 500
            |      definition.enabled = true
            |      auth-validation.enabled = true
            |    }
            |  }
            |""".stripMargin
        )

        val configLoader: ConfigLoader[ConfidenceLevelConfig] = ConfidenceLevelConfig.configLoader
        val result                                            = configLoader.load(conf, "api.confidence-level-check")

        result.confidenceLevel shouldBe L500
        result.definitionEnabled shouldBe true
        result.authValidationEnabled shouldBe true
      }
    }

    "given config that specifies an invalid confidence-level" should {
      "throw the expected exception" in {
        val conf = ConfigFactory.parseString(
          """
            |  api {
            |    confidence-level-check {
            |      confidence-level = 999
            |      definition.enabled = false
            |      auth-validation.enabled = false
            |    }
            |  }
            |""".stripMargin
        )

        val configLoader: ConfigLoader[ConfidenceLevelConfig] = ConfidenceLevelConfig.configLoader

        the[Exception] thrownBy {
          configLoader.load(conf, "api.confidence-level-check")
        } should have message "Illegal confidence level: 999"
      }
    }
  }

}
