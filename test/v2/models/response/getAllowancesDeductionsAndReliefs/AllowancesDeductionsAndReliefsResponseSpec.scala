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

package v2.models.response.getAllowancesDeductionsAndReliefs

import mocks.MockAppConfig
import play.api.libs.json.Json
import support.UnitSpec
import v2.fixtures.getAllowancesDeductionsAndReliefs.AllowancesDeductionsAndReliefsResponseFixture._
import v2.hateoas.HateoasFactory
import v2.models.hateoas.Method.GET
import v2.models.hateoas.{HateoasWrapper, Link}
import v2.models.utils.JsonErrorValidators

class AllowancesDeductionsAndReliefsResponseSpec extends UnitSpec with MockAppConfig with JsonErrorValidators {

  "AllowancesDeductionsAndReliefsResponse" when {
    "read from valid JSON" should {
      "produce the expected AllowancesDeductionsAndReliefsResponse object" in {
        allowancesDeductionsAndReliefsTopLevelJson.as[AllowancesDeductionsAndReliefsResponse] shouldBe
          allowancesDeductionsAndReliefsResponseModel
      }
    }

    "written to JSON" must {
      "produce the expected AllowancesDeductionsAndReliefsResponse object" in {
        Json.toJson(allowancesDeductionsAndReliefsResponseModel) shouldBe allowancesDeductionsAndReliefsResponseJson
      }
    }
  }

  "LinksFactory" when {
    class Test extends MockAppConfig {
      val hateoasFactory = new HateoasFactory(mockAppConfig)
      val nino           = "someNino"
      val calcId: String = "calcId"
      MockAppConfig.apiGatewayContext.returns("individuals/calculations").anyNumberOfTimes()
    }

    "wrapping a AllowancesDeductionsAndReliefsResponse object" should {
      "expose the correct hateoas links" in new Test {
        hateoasFactory.wrap(allowancesDeductionsAndReliefsResponseModel, AllowancesDeductionsAndReliefsHateoasData(nino, calcId)) shouldBe
          HateoasWrapper(
            allowancesDeductionsAndReliefsResponseModel,
            Seq(
              Link("/individuals/calculations/someNino/self-assessment/calcId", GET, "metadata"),
              Link("/individuals/calculations/someNino/self-assessment/calcId/allowances-deductions-reliefs", GET, "self")
            )
          )
      }
    }
  }

}
