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

package shared.models.domain

import org.scalacheck.{Arbitrary, Gen, ShrinkLowPriority}
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

// Use ShrinkLowPriority otherwise failures will shrink to produce TaxYears
// outside the Gen.choose(...) range resulting in misleading failures
trait TaxYearPropertyCheckSupport extends ShrinkLowPriority {
  self: ScalaCheckDrivenPropertyChecks =>

  // Based on the limitations of the various tax year formats:
  private val minAllowed: TaxYear = TaxYear.starting(2011)
  private val maxAllowed: TaxYear = TaxYear.starting(2098)

  private def arbTaxYear(minStartYear: Int, maxStartYear: Int): Arbitrary[TaxYear] =
    Arbitrary(Gen.choose(minStartYear, maxStartYear).map(TaxYear.starting))

  protected final def arbTaxYearInRange(min: TaxYear, max: TaxYear): Arbitrary[TaxYear] =
    arbTaxYear(min.startYear, max.startYear)

  protected final def arbTaxYearInRangeExclusive(min: TaxYear, max: TaxYear): Arbitrary[TaxYear] =
    arbTaxYear(min.startYear, max.startYear - 1)

  def forTaxYearsInRange(min: TaxYear, max: TaxYear)(f: TaxYear => Unit): Unit = {
    implicit val arbTaxYear: Arbitrary[TaxYear] = arbTaxYearInRange(min, max)

    forAll { (taxYear: TaxYear) => f(taxYear) }
  }

  def forTaxYearsFrom(min: TaxYear)(f: TaxYear => Unit): Unit =
    forTaxYearsInRange(min, maxAllowed)(f)

  def forTaxYearsBefore(maxExclusive: TaxYear)(f: TaxYear => Unit): Unit = {
    implicit val arbTaxYear: Arbitrary[TaxYear] = arbTaxYearInRangeExclusive(minAllowed, maxExclusive)

    forAll { (taxYear: TaxYear) => f(taxYear) }
  }

  def forPreTysTaxYears(f: TaxYear => Unit): Unit =
    forTaxYearsBefore(TaxYear.tysTaxYear)(f)

}
