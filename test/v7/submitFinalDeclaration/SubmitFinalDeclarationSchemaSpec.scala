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

package v7.submitFinalDeclaration

import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import shared.models.domain.{TaxYear, TaxYearPropertyCheckSupport}
import shared.utils.UnitSpec

class SubmitFinalDeclarationSchemaSpec extends UnitSpec with ScalaCheckDrivenPropertyChecks with TaxYearPropertyCheckSupport {

  "Getting a schema" when {
    "a tax year is valid" must {
      "use Def1 for tax years from 2023-24" in {
        forTaxYearsFrom(TaxYear.fromMtd("2023-24")) { taxYear =>
          SubmitFinalDeclarationSchema.schemaFor(taxYear.asMtd).shouldBe(SubmitFinalDeclarationSchema.Def1)
        }
      }

      "use Def1 for pre-TYS tax years" in {
        forPreTysTaxYears { taxYear =>
          SubmitFinalDeclarationSchema.schemaFor(taxYear.asMtd).shouldBe(SubmitFinalDeclarationSchema.Def1)
        }
      }
    }

    "the tax year is not valid" must {
      "use a default of Def1" in {
        SubmitFinalDeclarationSchema.schemaFor("NotATaxYear").shouldBe(SubmitFinalDeclarationSchema.Def1)
      }
    }
  }

}
