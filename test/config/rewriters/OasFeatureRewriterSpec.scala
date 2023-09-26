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

package config.rewriters

import config.rewriters.DocumentationRewriters.CheckRewrite
import controllers.Rewriter
import mocks.MockAppConfig
import play.api.Configuration
import support.UnitSpec

class OasFeatureRewriterSpec extends UnitSpec with MockAppConfig {

  private def setupCheckAndRewrite(oasFeatureEnabled: Boolean, oasFeatureEnabledInProd: Boolean): (CheckRewrite, Rewriter) = {
    MockAppConfig.featureSwitches returns Configuration(
      "oasFeature.enabled"                -> oasFeatureEnabled,
      "oasFeature.released-in-production" -> oasFeatureEnabledInProd
    )

    val rewriter = new OasFeatureRewriter()(mockAppConfig)
    rewriter.rewriteOasFeature.asTuple
  }

  "check and rewrite" should {
    "indicate whether it wants to rewrite the file" when {
      "feature is enabled both in environment and in prod" in {
        val (check, _) = setupCheckAndRewrite(oasFeatureEnabled = true, oasFeatureEnabledInProd = true)

        val result = check("1.0", "any-file.yaml")
        result shouldBe true
      }

      "feature is enabled in environment but disabled in prod" in {
        val (check, _) = setupCheckAndRewrite(oasFeatureEnabled = true, oasFeatureEnabledInProd = false)

        val result = check("1.0", "any-file.yaml")
        result shouldBe true
      }

      "feature disabled in environment and in prod" in {
        val (check, _) = setupCheckAndRewrite(oasFeatureEnabled = false, oasFeatureEnabledInProd = false)

        val result = check("1.0", "any-file.yaml")
        result shouldBe true
      }
      "feature disabled in environment and and enabled in prod" in {
        val (check, _) = setupCheckAndRewrite(oasFeatureEnabled = false, oasFeatureEnabledInProd = true)

        val result = check("1.0", "any-file.yaml")
        result shouldBe true
      }
    }

    "rewrite" when {

      val yaml =
        """
          |summary: Retrieve Employment Expenses
          |description: |
          |  This endpoint enables you to retrieve existing employment expenses.
          |  A National Insurance number and tax year must be provided.
          |
          |  ### Test data
          |  {{#if (releasedInProduction "oasFeature")}}
          |  <p>Scenario simulations using Gov-Test-Scenario headers ARE ONLY AVAILABLE IN the sandbox environment.</p>
          |  {{else}}
          |  <p>Scenario simulations using Gov-Test-Scenario headers are only available in the sandbox environment.</p>
          |  {{/if}}
          |
          |tags:
          |  - Employment Expenses
          |""".stripMargin

      "the feature isn't enabled" in {
        val (_, rewrite) = setupCheckAndRewrite(oasFeatureEnabled = false, oasFeatureEnabledInProd = false)

        val expected =
          s"""
             |summary: Retrieve Employment Expenses
             |description: |
             |  This endpoint enables you to retrieve existing employment expenses.
             |  A National Insurance number and tax year must be provided.
             |
             |  ### Test data
             |${" "}${" "}
             |  <p>Scenario simulations using Gov-Test-Scenario headers are only available in the sandbox environment.</p>
             |${" "}${" "}
             |
             |tags:
             |  - Employment Expenses
             |""".stripMargin

        val result = rewrite("/...", "something.yaml", yaml)
        result shouldBe expected
      }

      "the feature is enabled in environment but not in prod" in {
        val (_, rewrite) = setupCheckAndRewrite(oasFeatureEnabled = true, oasFeatureEnabledInProd = false)

        val expected =
          s"""
          |summary: Retrieve Employment Expenses
          |description: |
          |  This endpoint enables you to retrieve existing employment expenses.
          |  A National Insurance number and tax year must be provided.
          |
          |  ### Test data
          |${" "}${" "}
          |  <p>Scenario simulations using Gov-Test-Scenario headers are only available in the sandbox environment.</p>
          |${" "}${" "}
          |
          |tags:
          |  - Employment Expenses
          |""".stripMargin

        val result = rewrite("/...", "something.yaml", yaml)
        result shouldBe expected
      }

      "the feature is enabled in environment and in prod" in {
        val (_, rewrite) = setupCheckAndRewrite(oasFeatureEnabled = true, oasFeatureEnabledInProd = true)

        val expected =
          s"""
             |summary: Retrieve Employment Expenses
             |description: |
             |  This endpoint enables you to retrieve existing employment expenses.
             |  A National Insurance number and tax year must be provided.
             |
             |  ### Test data
             |${" "}${" "}
             |  <p>Scenario simulations using Gov-Test-Scenario headers ARE ONLY AVAILABLE IN the sandbox environment.</p>
             |${" "}${" "}
             |
             |tags:
             |  - Employment Expenses
             |""".stripMargin

        val result = rewrite("/...", "something.yaml", yaml)
        result shouldBe expected
      }
    }
  }

}
