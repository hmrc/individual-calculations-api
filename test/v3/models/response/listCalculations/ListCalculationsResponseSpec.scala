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

package v3.models.response.listCalculations

import mocks.MockAppConfig
import play.api.libs.json.Json
import support.UnitSpec
import v3.fixtures.ListCalculationsFixture
import v3.hateoas.HateoasFactory
import v3.models.domain.TaxYear
import v3.models.hateoas.{HateoasWrapper, Link, Method, RelType}

class ListCalculationsResponseSpec extends UnitSpec with ListCalculationsFixture {

  "ListCalculationsResponse" when {
    "read from downstream JSON" must {
      "return the expected data model" in {
        listCalculationsDownstreamJson.as[ListCalculationsResponse[Calculation]] shouldBe listCalculationsResponseModel
      }
    }

    "written to MTD JSON" must {
      "produce the expected JSON body" in {
        Json.toJson(listCalculationsResponseModel) shouldBe listCalculationsMtdJson
      }
    }
  }

  "HateoasFactory" must {
    trait Test extends MockAppConfig {
      val hateoasFactory   = new HateoasFactory(mockAppConfig)
      val nino: String     = "someNino"
      val calcId: String   = "c432a56d-e811-474c-a26a-76fc3bcaefe5"
      val taxYear: TaxYear = TaxYear.fromMtd("2020-21")
      MockAppConfig.apiGatewayContext.returns("individuals/calculations").anyNumberOfTimes()
    }

    "wrap response correctly when tax year is defined" in new Test {
      hateoasFactory.wrapList(listCalculationsResponseModel, ListCalculationsHateoasData(nino, taxYear)) shouldBe
        HateoasWrapper(
          payload = ListCalculationsResponse(
            Seq(
              HateoasWrapper(
                calculationModel,
                Seq(
                  Link(
                    href = s"/individuals/calculations/someNino/self-assessment/2020-21/$calcId",
                    rel = RelType.SELF,
                    method = Method.GET
                  )
                )))),
          links = Seq(
            Link(
              href = s"/individuals/calculations/someNino/self-assessment/2020-21",
              rel = RelType.TRIGGER,
              method = Method.POST
            ),
            Link(
              href = "/individuals/calculations/someNino/self-assessment?taxYear=2020-21",
              rel = RelType.SELF,
              method = Method.GET
            )
          )
        )
    }

    "omit retrieve item link when calculation does not contain tax year" in new Test {
      val calculationWithoutTaxYear = calculationModel.copy(taxYear = None)
      val responseWithoutTaxYear    = ListCalculationsResponse(Seq(calculationWithoutTaxYear))

      hateoasFactory.wrapList(responseWithoutTaxYear, ListCalculationsHateoasData(nino, taxYear)) shouldBe
        HateoasWrapper(
          payload = ListCalculationsResponse(Seq(HateoasWrapper(calculationWithoutTaxYear, Seq()))),
          links = Seq(
            Link(
              href = s"/individuals/calculations/someNino/self-assessment/2020-21",
              rel = RelType.TRIGGER,
              method = Method.POST
            ),
            Link(
              href = "/individuals/calculations/someNino/self-assessment?taxYear=2020-21",
              rel = RelType.SELF,
              method = Method.GET
            )
          )
        )
    }
  }

}
