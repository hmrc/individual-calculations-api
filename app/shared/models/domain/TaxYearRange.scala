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

package shared.models.domain

import java.time.Clock

/** A tax year range.
  *
  * @param from
  *   the from date (where YYXX-ZZ is translated to 20XX-04-06)
  * @param to
  *   the to date (where YYXX-ZZ is translated to 20ZZ-04-05)
  */
case class TaxYearRange(from: TaxYear, to: TaxYear)

object TaxYearRange {

  def todayMinus(years: Int)(implicit clock: Clock = Clock.systemUTC): TaxYearRange = {
    val currentTaxYear = TaxYear.currentTaxYear
    val from           = TaxYear.fromDownstreamInt(currentTaxYear.year - years)
    TaxYearRange(from = from, to = currentTaxYear)
  }

  def apply(taxYear: TaxYear): TaxYearRange = TaxYearRange(taxYear, taxYear)

  /** @param taxYear
    *   tax year in MTD format (e.g. 2017-18)
    * @return
    *   a 1-year range e.g. 2017 to 2018
    */
  def fromMtd(taxYear: String): TaxYearRange = {
    val ty = TaxYear.fromMtd(taxYear)
    new TaxYearRange(ty, ty)
  }

}
