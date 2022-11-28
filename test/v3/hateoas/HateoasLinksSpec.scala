/*
 * Copyright 2022 HM Revenue & Customs
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

package v3.hateoas

import mocks.MockAppConfig
import support.UnitSpec
import v3.models.domain.TaxYear
import v3.models.hateoas.Link
import v3.models.hateoas.Method.{GET, POST}

class HateoasLinksSpec extends UnitSpec with MockAppConfig {

  private val nino    = "AA111111A"
  private val taxYear = TaxYear.fromMtd("2022-23")

  class Test extends HateoasLinks {
    MockAppConfig.apiGatewayContext.returns("context").anyNumberOfTimes()
  }

  "HateoasLinks" when {
    "trigger" should {
      "return the correct link" in new Test {
        val result       = trigger(mockAppConfig, nino, taxYear)
        val expectedHref = s"/context/$nino/self-assessment/2022-23"
        result shouldBe Link(href = expectedHref, method = POST, rel = "trigger")
      }
    }

    "list" should {
      "return the correct link with isSelf true" in new Test {
        val result       = list(mockAppConfig, nino, taxYear, isSelf = true)
        val expectedHref = s"/context/$nino/self-assessment?taxYear=2022-23"
        result shouldBe Link(href = expectedHref, method = GET, rel = "self")
      }

      "return the correct link with isSelf false" in new Test {
        val result       = list(mockAppConfig, nino, taxYear, isSelf = false)
        val expectedHref = s"/context/$nino/self-assessment?taxYear=2022-23"
        result shouldBe Link(href = expectedHref, method = GET, rel = "list")
      }
    }

    "retrieve" should {
      "return the correct link" in new Test {
        val result       = retrieve(mockAppConfig, nino, taxYear, "calcId")
        val expectedHref = s"/context/$nino/self-assessment/2022-23/calcId"
        result shouldBe Link(href = expectedHref, method = GET, rel = "self")
      }
    }

    "submitFinalDeclaration" should {
      "return the correct link" in new Test {
        val result       = submitFinalDeclaration(mockAppConfig, nino, taxYear, "calcId")
        val expectedHref = s"/context/$nino/self-assessment/2022-23/calcId/final-declaration"
        result shouldBe Link(href = expectedHref, method = POST, rel = "submit-final-declaration")
      }
    }
  }

}
