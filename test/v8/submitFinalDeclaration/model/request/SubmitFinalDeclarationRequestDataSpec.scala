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

package v8.submitFinalDeclaration.model.request

import shared.models.domain.{CalculationId, Nino, TaxYear}
import shared.utils.UnitSpec
import v8.common.model.domain.`final-declaration`
import v8.retrieveCalculation.models.request.{
  Def1_RetrieveCalculationRequestData,
  Def2_RetrieveCalculationRequestData,
  Def3_RetrieveCalculationRequestData
}

class SubmitFinalDeclarationRequestDataSpec extends UnitSpec {

  "SubmitFinalDeclarationRequestData" when {
    "getting corresponding retrieve data (for NRS)" when {
      val nino            = Nino("ZG903729C")
      val calculationId   = CalculationId("calcId")
      val calculationType = `final-declaration`

      "retrieve uses schema Def1" must {
        "return a Def1_RetrieveCalculationRequestData" in {
          val taxYear = TaxYear.fromMtd("2023-24")
          Def1_SubmitFinalDeclarationRequestData(nino, taxYear, calculationId, calculationType).toRetrieveRequestData shouldBe
            Def1_RetrieveCalculationRequestData(nino, taxYear, calculationId)
        }
      }

      "retrieve uses schema Def2" must {
        "return a Def2_RetrieveCalculationRequestData" in {
          val taxYear = TaxYear.fromMtd("2024-25")
          Def1_SubmitFinalDeclarationRequestData(nino, taxYear, calculationId, calculationType).toRetrieveRequestData shouldBe
            Def2_RetrieveCalculationRequestData(nino, taxYear, calculationId)
        }

        "retrieve uses schema Def3" must {
          "return a Def2_RetrieveCalculationRequestData" in {
            val taxYear = TaxYear.fromMtd("2025-26")
            Def1_SubmitFinalDeclarationRequestData(nino, taxYear, calculationId, calculationType).toRetrieveRequestData shouldBe
              Def3_RetrieveCalculationRequestData(nino, taxYear, calculationId)
          }
        }
      }
    }
  }

}
