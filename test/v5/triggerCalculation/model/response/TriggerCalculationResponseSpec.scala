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

package v5.triggerCalculation.model.response

import api.hateoas
import api.hateoas.Method.GET
import api.hateoas.{HateoasFactory, HateoasWrapper}
import api.models.domain.TaxYear
import mocks.MockAppConfig
import support.UnitSpec

class TriggerCalculationResponseSpec extends UnitSpec {
  private val calculationId                                       = "testId"
  val triggerCalculationResponseModel: TriggerCalculationResponse = Def1_TriggerCalculationResponse(calculationId)

  "LinksFactory" when {
    class Test extends MockAppConfig {
      val hateoasFactory = new HateoasFactory(mockAppConfig)
      val nino           = "someNino"
      val taxYear        = TaxYear.fromMtd("2020-21")
      MockAppConfig.apiGatewayContext.returns("individuals/calculations").anyNumberOfTimes()
    }

    "wrapping a TriggerCalculationResponse object" should {
      "expose the correct hateoas links" in new Test {
        hateoasFactory.wrap(
          triggerCalculationResponseModel,
          TriggerCalculationHateoasData(nino, taxYear, finalDeclaration = true, calculationId)) shouldBe
          HateoasWrapper(
            triggerCalculationResponseModel,
            Seq(
              hateoas.Link(s"/individuals/calculations/$nino/self-assessment?taxYear=2020-21", GET, "list"),
              hateoas.Link(s"/individuals/calculations/$nino/self-assessment/2020-21/$calculationId", GET, "self")
            )
          )
      }
    }
  }

}
