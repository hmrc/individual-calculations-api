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

package v7.retrieveCalculation.schema

import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import shared.models.domain.{TaxYear, TaxYearPropertyCheckSupport}
import shared.utils.UnitSpec

class RetrieveCalculationSchemaSpec extends UnitSpec with ScalaCheckDrivenPropertyChecks with TaxYearPropertyCheckSupport {

  "Getting a schema" when {
    "a tax year is valid" must {
      "use Def1 for tax year 2023-24" in {
        val taxYear = TaxYear.fromMtd("2023-24")
        RetrieveCalculationSchema.schemaFor(taxYear.asMtd) shouldBe RetrieveCalculationSchema.Def1
      }

      "use Def2 for tax year 2024-25" in {
        val taxYear = TaxYear.fromMtd("2024-25")
        RetrieveCalculationSchema.schemaFor(taxYear.asMtd) shouldBe RetrieveCalculationSchema.Def2
      }

      "use Def3 for tax years from 2025-26" in {
        forTaxYearsFrom(TaxYear.fromMtd("2025-26")) { taxYear =>
          RetrieveCalculationSchema.schemaFor(taxYear.asMtd) shouldBe RetrieveCalculationSchema.Def3
        }
      }

      "use Def1 for pre-TYS tax years" in {
        forPreTysTaxYears { taxYear =>
          RetrieveCalculationSchema.schemaFor(taxYear.asMtd) shouldBe RetrieveCalculationSchema.Def1
        }
      }
    }

    "the tax year is not valid" must {
      "use a default of Def3 (where tax year validation will fail)" in {
        RetrieveCalculationSchema.schemaFor("NotATaxYear") shouldBe RetrieveCalculationSchema.Def3
      }
    }
  }

}
