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

package shared.controllers.validators.resolvers

import cats.data.Validated.{Invalid, Valid}
import shared.models.domain.DateRange
import shared.models.errors.{EndDateFormatError, RuleEndBeforeStartDateError, StartDateFormatError}
import shared.utils.UnitSpec

import java.time.LocalDate

class ResolveDateRangeSpec extends UnitSpec {

  private val validStart = "2023-06-21"
  private val validEnd   = "2024-06-21"

  private val sameValidStart = "2023-06-21"
  private val sameValidEnd   = "2023-06-21"

  // To be sure it's using the construction params...
  private val startDateFormatError    = StartDateFormatError.withPath("pathToStartDate")
  private val endDateFormatError      = EndDateFormatError.withPath("pathToEndDate")
  private val endBeforeStartDateError = RuleEndBeforeStartDateError.withPath("somePath")

  private val resolveDateRange = ResolveDateRange(
    startDateFormatError = startDateFormatError,
    endDateFormatError = endDateFormatError,
    endBeforeStartDateError = endBeforeStartDateError)

  "ResolveDateRange" should {
    "return no errors" when {
      "passed a valid start and end date" in {
        val result = resolveDateRange(validStart -> validEnd)
        result shouldBe Valid(DateRange(LocalDate.parse(validStart), LocalDate.parse(validEnd)))
      }

      "passed an end date equal to start date" in {
        val result = resolveDateRange(sameValidStart -> sameValidEnd)
        result shouldBe Valid(DateRange(LocalDate.parse(sameValidStart), LocalDate.parse(sameValidEnd)))
      }
    }

    "return an error" when {
      "passed an invalid start date" in {
        val result = resolveDateRange("not-a-date" -> validEnd)
        result shouldBe Invalid(List(startDateFormatError))
      }

      "passed an invalid end date" in {
        val result = resolveDateRange(validStart -> "not-a-date")
        result shouldBe Invalid(List(endDateFormatError))
      }

      "passed an end date before start date" in {
        val result = resolveDateRange(validEnd -> validStart)
        result shouldBe Invalid(List(endBeforeStartDateError))
      }
    }
  }

  "ResolveDateRange withDatesLimitedTo" should {
    val minDate            = LocalDate.parse("2000-02-01")
    val maxDate            = LocalDate.parse("2000-02-10")
    val resolverWithLimits = resolveDateRange.withDatesLimitedTo(minDate, maxDate)

    "return no errors for dates within limits" in {
      val result = resolverWithLimits("2000-02-01" -> "2000-02-10")
      result shouldBe Valid(DateRange(minDate, maxDate))
    }

    "return an error for dates outside the limits" when {
      "the start date is too early" in {
        val result = resolverWithLimits("1999-12-31" -> "2000-02-10")
        result shouldBe Invalid(List(startDateFormatError))
      }

      "the end date is too late" in {
        val result = resolverWithLimits("2000-02-01" -> "2000-02-11")
        result shouldBe Invalid(List(endDateFormatError))
      }

      "both start and end dates are outside the limits" in {
        val result = resolverWithLimits("1999-12-31" -> "2000-02-11")
        result shouldBe Invalid(List(startDateFormatError, endDateFormatError))
      }
    }
  }

  "ResolveDateRange withYearsLimitedTo" should {
    val minYear                = 2000
    val maxYear                = 2010
    val resolverWithYearLimits = resolveDateRange.withYearsLimitedTo(minYear, maxYear)

    "return no errors for dates within the year limits" in {
      val result = resolverWithYearLimits("2000-01-01" -> "2010-12-31")
      result shouldBe Valid(DateRange(LocalDate.parse("2000-01-01"), LocalDate.parse("2010-12-31")))
    }

    "return an error for dates outside the year limits" when {
      "the start date is before the minimum year" in {
        val result = resolverWithYearLimits("1999-12-31" -> "2010-12-31")
        result shouldBe Invalid(List(startDateFormatError))
      }

      "the end date is after the maximum year" in {
        val result = resolverWithYearLimits("2000-01-01" -> "2011-01-01")
        result shouldBe Invalid(List(endDateFormatError))
      }

      "both start and end dates are outside the year limits" in {
        val result = resolverWithYearLimits("1999-12-31" -> "2011-01-01")
        result shouldBe Invalid(List(startDateFormatError, endDateFormatError))
      }
    }
  }

  "ResolveDateRange datesLimitedTo validator" should {
    val minDate   = LocalDate.parse("2000-02-01")
    val maxDate   = LocalDate.parse("2000-02-10")
    val validator = ResolveDateRange.datesLimitedTo(minDate, startDateFormatError, maxDate, endDateFormatError)

    val tooEarly = minDate.minusDays(1)
    val tooLate  = maxDate.plusDays(1)

    "allow min and max dates" in {
      val result = validator(DateRange(minDate, maxDate))
      result shouldBe None
    }

    "disallow dates earlier than min or later than max" in {
      val result = validator(DateRange(tooEarly -> tooLate))
      result shouldBe Some(List(startDateFormatError, endDateFormatError))
    }

    "disallow start and dates later than max" in {
      val result = validator(DateRange(tooLate -> tooLate))
      result shouldBe Some(List(startDateFormatError, endDateFormatError))
    }

    "disallow start and dates earlier than min" in {
      val result = validator(DateRange(tooEarly -> tooEarly))
      result shouldBe Some(List(startDateFormatError, endDateFormatError))
    }
  }

  "ResolveDateRange yearsLimitedTo validator" should {
    val minYear = 2000
    val maxYear = 2010

    val validator = ResolveDateRange.yearsLimitedTo(minYear, startDateFormatError, maxYear, endDateFormatError)

    "allow dates in min and max years" in {
      val result = validator(DateRange(LocalDate.parse("2000-01-01") -> LocalDate.parse("2010-12-31")))
      result shouldBe None
    }

    "disallow start date earlier than min year and end date later than max year" in {
      val result = validator(DateRange(LocalDate.parse("1999-12-31") -> LocalDate.parse("2011-01-01")))
      result shouldBe Some(List(startDateFormatError, endDateFormatError))
    }

    "disallow start and end dates later than max year" in {
      val result = validator(DateRange(LocalDate.parse("2011-01-01") -> LocalDate.parse("2012-12-31")))
      result shouldBe Some(List(startDateFormatError, endDateFormatError))
    }

    "disallow start and end dates earlier than min year" in {
      val result = validator(DateRange(LocalDate.parse("1998-01-01") -> LocalDate.parse("1999-12-31")))
      result shouldBe Some(List(startDateFormatError, endDateFormatError))
    }
  }

}
