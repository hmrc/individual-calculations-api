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

import shared.utils.UnitSpec

import java.time.{Clock, LocalDate, ZoneOffset}

class TaxYearRangeSpec extends UnitSpec {

  "fromMtd()" should {

    "return a TaxYearRange" when {
      "given an MTD-format taxYear" in {
        val taxYear              = TaxYear.fromMtd("2019-20")
        val result: TaxYearRange = TaxYearRange.fromMtd("2019-20")
        result shouldBe TaxYearRange(taxYear)
      }
    }
  }

  "from and to" should {
    "return the correct taxYearStart and taxYearEnd respectively" when {
      "given a valid taxYear" in {
        val range: TaxYearRange = TaxYearRange.fromMtd("2019-20")
        range.from.startDate shouldBe LocalDate.parse("2019-04-06")
        range.to.endDate shouldBe LocalDate.parse("2020-04-05")
      }
    }
  }

  "todayMinus(years)" should {
    "return a TaxYearRange from the 'subtracted' tax year to the current tax year" in {
      implicit val clock: Clock = Clock.fixed(LocalDate.parse("2023-04-01").atStartOfDay(ZoneOffset.UTC).toInstant, ZoneOffset.UTC)

      TaxYearRange.todayMinus(years = 4) shouldBe TaxYearRange(TaxYear.fromMtd("2018-19"), TaxYear.fromMtd("2022-23"))
    }
  }

}
