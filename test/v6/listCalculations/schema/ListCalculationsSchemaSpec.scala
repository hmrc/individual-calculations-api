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

package v6.listCalculations.schema

import org.scalacheck.{Arbitrary, Gen}
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import shared.models.domain.{TaxYear, TaxYearPropertyCheckSupport}
import shared.utils.UnitSpec

import java.time.{Clock, LocalDate, ZoneOffset}

class ListCalculationsSchemaSpec extends UnitSpec with ScalaCheckDrivenPropertyChecks with TaxYearPropertyCheckSupport {

  "schema lookup" when {
    "a tax year is present" must {
      "use Def1 for tax years from 2023-24" in {
        forTaxYearsFrom(TaxYear.fromMtd("2023-24")) { taxYear =>
          ListCalculationsSchema.schemaFor(Some(taxYear.asMtd)) shouldBe ListCalculationsSchema.Def1
        }
      }

      "use Def1 for pre-TYS tax years" in {
        forPreTysTaxYears { taxYear =>
          ListCalculationsSchema.schemaFor(Some(taxYear.asMtd)) shouldBe ListCalculationsSchema.Def1
        }
      }
    }

    "the tax year is present but not valid" must {
      "use a default of Def1" in {
        ListCalculationsSchema.schemaFor(Some("NotATaxYear")) shouldBe ListCalculationsSchema.Def1
      }
    }

    "no tax year is present" must {
      def clockForTimeInYear(year: Int) =
        Clock.fixed(LocalDate.of(year, 6, 1).atStartOfDay().toInstant(ZoneOffset.UTC), ZoneOffset.UTC)

      "use the same schema as for the current tax year" in {
        implicit val arbYear: Arbitrary[Int] = Arbitrary(Gen.choose(min = 2000, max = 2099))

        forAll { (year: Int) =>
          implicit val clock: Clock = clockForTimeInYear(year)

          ListCalculationsSchema.schemaFor(None) shouldBe
            ListCalculationsSchema.schemaFor(Some(TaxYear.currentTaxYear.asMtd))
        }
      }
    }
  }

}
