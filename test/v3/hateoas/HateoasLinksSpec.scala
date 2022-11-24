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
import play.api.Configuration
import support.UnitSpec
import v3.models.domain.TaxYear
import v3.models.hateoas.{Link, Method}

class HateoasLinksSpec extends UnitSpec with MockAppConfig {

//  private val nino        = "AA111111A"
  private val taxYear2023 = TaxYear.fromMtd("2022-23")
  private val taxYear2024 = TaxYear.fromMtd("2023-24")

  object Target extends HateoasLinks

  class Test {
    MockAppConfig.apiGatewayContext.returns("context").anyNumberOfTimes
  }

  class TysDisabledTest extends Test {
    MockAppConfig.featureSwitches returns Configuration("tys-api.enabled" -> false)
  }

  class TysEnabledTest extends Test {
    MockAppConfig.featureSwitches returns Configuration("tys-api.enabled" -> true)
  }

  "HateoasLinks" when {
    "trigger" should {
//      assertCorrectLink(makeLink)
    }
  }

  def assertCorrectLink(makeLink: Option[TaxYear] => Link, baseHref: String, method: Method, rel: String): Unit = {
    "return the correct link" in new TysDisabledTest {
      makeLink(None) shouldBe Link(href = baseHref, method = method, rel = rel)
    }

    "TYS feature switch is disabled" should {
      "not include tax year query parameter given a TYS tax year" in new TysDisabledTest {
        makeLink(Some(taxYear2024)) shouldBe Link(href = baseHref, method = method, rel = rel)
      }
    }

    "TYS feature switch is enabled" should {
      "not include tax year query parameter given a non-TYS tax year" in new TysEnabledTest {
        makeLink(Some(taxYear2023)) shouldBe Link(href = baseHref, method = method, rel = rel)
      }

      "include tax year query parameter given a TYS tax year" in new TysEnabledTest {
        makeLink(Some(taxYear2024)) shouldBe Link(href = s"$baseHref?taxYear=2023-24", method = method, rel = rel)
      }
    }
  }

}
