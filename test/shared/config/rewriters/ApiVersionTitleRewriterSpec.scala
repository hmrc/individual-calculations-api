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

package shared.config.rewriters

import shared.config.MockAppConfig
import shared.utils.UnitSpec

class ApiVersionTitleRewriterSpec extends UnitSpec with MockAppConfig {

  private val rewriter        = new ApiVersionTitleRewriter(mockAppConfig)
  private val checkAndRewrite = rewriter.rewriteApiVersionTitle

  "ApiVersionTitleRewriter" when {
    "checking if rewrite is needed for a given version" should {
      "indicate rewrite needed when API endpoints are disabled in production" in {
        MockedAppConfig.apiVersionReleasedInProduction("1.0") returns false
        val result = checkAndRewrite.check("1.0", "application.yaml")
        result shouldBe true
      }

      "indicate rewrite not needed for any other combination" in {
        MockedAppConfig.apiVersionReleasedInProduction("1.0") returns true
        val result1 = checkAndRewrite.check("1.0", "application.yaml")
        val result2 = checkAndRewrite.check("1.0", "some_other_file.yaml")
        result1 shouldBe false
        result2 shouldBe false
      }
    }

    "rewriting the title" should {
      "return the title unchanged if it already contains '[test only]'" in {
        val title  = """  title: "[tesT oNLy] API title (MTD)""""
        val result = checkAndRewrite.rewrite("", "", title)
        result shouldBe title
      }

      "return yaml with rewritten title if the yaml title is ready to be rewritten" in {
        val yaml =
          """
                    |openapi: "3.0.3"
                    |
                    |info:
                    |  version: "1.0"
                    |  title: Individuals Expenses (MTD)
                    |  description: |
                    |    # Send fraud prevention data
                    |    HMRC monitors transactions to help protect your customers' confidential data from criminals and fraudsters.
                    |
                    |servers:
                    |  - url: https://test-api.service.hmrc.gov.uk
                    |""".stripMargin

        val expected =
          """
                    |openapi: "3.0.3"
                    |
                    |info:
                    |  version: "1.0"
                    |  title: "Individuals Expenses (MTD) [test only]"
                    |  description: |
                    |    # Send fraud prevention data
                    |    HMRC monitors transactions to help protect your customers' confidential data from criminals and fraudsters.
                    |
                    |servers:
                    |  - url: https://test-api.service.hmrc.gov.uk
                    |""".stripMargin

        val result = checkAndRewrite.rewrite("", "", yaml)
        result shouldBe expected
      }

      "return the rewritten title if the yaml title is in quotes" in {
        val title    = """  title: "API title (MTD)""""
        val expected = """  title: "API title (MTD) [test only]""""
        val result   = checkAndRewrite.rewrite("", "", title)
        result shouldBe expected
      }
    }
  }

}
