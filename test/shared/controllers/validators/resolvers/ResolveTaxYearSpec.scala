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

import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import shared.models.domain.TaxYear
import shared.models.errors._
import shared.utils.UnitSpec

import java.time.{Clock, LocalDate, ZoneOffset}

class ResolveTaxYearSpec extends UnitSpec with ResolverSupport {

  "ResolveTaxYear" should {
    "return no errors" when {
      val validTaxYear = "2018-19"

      "given a valid tax year" in {
        val result: Validated[Seq[MtdError], TaxYear] = ResolveTaxYear(validTaxYear)
        result shouldBe Valid(TaxYear.fromMtd(validTaxYear))
      }

      "given a valid tax year in an Option" in {
        val result: Validated[Seq[MtdError], Option[TaxYear]] = ResolveTaxYear(Option(validTaxYear))
        result shouldBe Valid(Some(TaxYear.fromMtd(validTaxYear)))
      }

      "given an empty Option" in {
        val result: Validated[Seq[MtdError], Option[TaxYear]] = ResolveTaxYear(None)
        result shouldBe Valid(None)
      }
    }

    "return an error" when {
      "given an invalid tax year format" in {
        ResolveTaxYear("2019") shouldBe Invalid(List(TaxYearFormatError))
      }

      "given a tax year string in which the range is greater than 1 year" in {
        ResolveTaxYear("2017-19") shouldBe Invalid(List(RuleTaxYearRangeInvalidError))
      }

      "the end year is before the start year" in {
        ResolveTaxYear("2018-17") shouldBe Invalid(List(RuleTaxYearRangeInvalidError))
      }

      "the start and end years are the same" in {
        ResolveTaxYear("2017-17") shouldBe Invalid(List(RuleTaxYearRangeInvalidError))
      }

      "the tax year is bad" in {
        ResolveTaxYear("20177-17") shouldBe Invalid(List(TaxYearFormatError))
      }
    }
  }

  "ResolveTaxYearMinimum using the default errors" should {
    val minimumTaxYear = TaxYear.fromMtd("2021-22")
    val resolver       = ResolveTaxYearMinimum(minimumTaxYear)

    "return no errors" when {
      "given the minimum allowed tax year" in {
        val result: Validated[Seq[MtdError], TaxYear] = resolver("2021-22")
        result shouldBe Valid(minimumTaxYear)
      }

      "given the minimum allowed tax year via the adaptor for existing callers" in {
        val result: Validated[Seq[MtdError], TaxYear] = ResolveTaxYear(minimumTaxYear, "2021-22")
        result shouldBe Valid(minimumTaxYear)
      }

      "given the minimum allowed tax year in an Option" in {
        val result: Validated[Seq[MtdError], Option[TaxYear]] = resolver(Option("2021-22"))
        result shouldBe Valid(Some(minimumTaxYear))
      }

      "given an empty Option" in {
        val result: Validated[Seq[MtdError], Option[TaxYear]] = resolver(None)
        result shouldBe Valid(None)
      }
    }

    "return RuleTaxYearNotSupportedError" when {
      "when the tax year is before the minimum tax year" in {
        val result: Validated[Seq[MtdError], TaxYear] = resolver("2020-21")
        result shouldBe Invalid(List(RuleTaxYearNotSupportedError))
      }
    }
  }

  "ResolveTaxYearMinimum using custom errors" should {
    val minimumTaxYear = TaxYear.fromMtd("2021-22")

    val notSupportedError = NotFoundError.withPath("/notSupported")
    val formatError       = NinoFormatError.withPath("/formatError")
    val rangeError        = BadRequestError.withPath("/rangeError")

    val resolver = ResolveTaxYearMinimum(
      minimumTaxYear,
      notSupportedError = notSupportedError,
      formatError = formatError,
      rangeError = rangeError
    )

    "return no errors" when {
      "given the minimum allowed tax year" in {
        val result: Validated[Seq[MtdError], TaxYear] = resolver("2021-22")
        result shouldBe Valid(minimumTaxYear)
      }
    }

    "return the custom error" when {
      "given a tax year before the minimum tax year" in {
        val result: Validated[Seq[MtdError], TaxYear] = resolver("2020-21")
        result shouldBe Invalid(List(notSupportedError))
      }

      "given a badly formatted tax year" in {
        val result: Validated[Seq[MtdError], TaxYear] = resolver("not-a-tax-year")
        result shouldBe Invalid(List(formatError))
      }

      "given a tax year with an invalid range" in {
        val result: Validated[Seq[MtdError], TaxYear] = resolver("2024-26")
        result shouldBe Invalid(List(rangeError))
      }
    }
  }

  "ResolveTaxYearMaximum" should {
    val maximumTaxYear = TaxYear.fromMtd("2024-25")
    val resolver       = ResolveTaxYearMaximum(maximumTaxYear)

    "return no errors" when {
      "given the maximum allowed tax year" in {
        val result: Validated[Seq[MtdError], TaxYear] = resolver("2024-25")
        result shouldBe Valid(maximumTaxYear)
      }

      "given the maximum allowed tax year in an Option" in {
        val result: Validated[Seq[MtdError], Option[TaxYear]] = resolver(Option("2024-25"))
        result shouldBe Valid(Some(maximumTaxYear))
      }

      "given an empty Option" in {
        val result: Validated[Seq[MtdError], Option[TaxYear]] = resolver(None)
        result shouldBe Valid(None)
      }
    }

    "return RuleTaxYearNotSupportedError" when {
      "when the tax year is after the maximum tax year" in {
        val result: Validated[Seq[MtdError], TaxYear] = resolver("2025-26")
        result shouldBe Invalid(List(RuleTaxYearNotSupportedError))
      }
    }
  }

  "ResolveTaxYearMinMax" should {
    val minimumTaxYear = TaxYear.fromMtd("2021-22")
    val maximumTaxYear = TaxYear.fromMtd("2024-25")
    val resolver       = ResolveTaxYearMinMax(minimumTaxYear -> maximumTaxYear)

    "return no errors" when {
      "given the minimum allowed tax year" in {
        val result: Validated[Seq[MtdError], TaxYear] = resolver("2021-22")
        result shouldBe Valid(minimumTaxYear)
      }

      "given the maximum allowed tax year" in {
        val result: Validated[Seq[MtdError], TaxYear] = resolver("2024-25")
        result shouldBe Valid(maximumTaxYear)
      }
    }

    "return RuleTaxYearNotSupportedError" when {
      "given a tax year earlier than the minimum" in {
        val result: Validated[Seq[MtdError], TaxYear] = resolver("2020-21")
        result shouldBe Invalid(List(RuleTaxYearNotSupportedError))
      }

      "given a tax year later than the maximum" in {
        val result: Validated[Seq[MtdError], TaxYear] = resolver("2025-26")
        result shouldBe Invalid(List(RuleTaxYearNotSupportedError))
      }
    }

    "return the expected error" when {
      val resolver = ResolveTaxYearMinMax(minimumTaxYear -> maximumTaxYear, minError = BadRequestError, maxError = InvalidTaxYearParameterError)

      "given a tax year earlier than the minimum and a non-default MtdError" in {
        val result: Validated[Seq[MtdError], TaxYear] = resolver("2020-21")
        result shouldBe Invalid(List(BadRequestError))
      }

      "given a tax year later than the maximum and a non-default MtdError" in {
        val result: Validated[Seq[MtdError], TaxYear] = resolver("2025-26")
        result shouldBe Invalid(List(InvalidTaxYearParameterError))
      }
    }

    "ResolveTaxYearMinMax" should {
      "return no errors when given an optional valid tax year" in {
        val result: Validated[Seq[MtdError], Option[TaxYear]] = resolver(Some("2021-22"))
        result shouldBe Valid(Some(minimumTaxYear))
      }

      "return RuleTaxYearNotSupportedError when given an optional invalid tax year earlier than the minimum" in {
        val result: Validated[Seq[MtdError], Option[TaxYear]] = resolver(Some("2020-21"))
        result shouldBe Invalid(Seq(RuleTaxYearNotSupportedError))
      }

      "return RuleTaxYearNotSupportedError when given an optional invalid tax year later than the maximum" in {
        val result: Validated[Seq[MtdError], Option[TaxYear]] = resolver(Some("2025-26"))
        result shouldBe Invalid(Seq(RuleTaxYearNotSupportedError))
      }

      "return no errors when given no tax year" in {
        val result: Validated[Seq[MtdError], Option[TaxYear]] = resolver(None)
        result shouldBe Valid(None)
      }
    }
  }

  "ResolveTysTaxYear" should {
    "return no errors" when {

      val validTaxYear = "2023-24"

      "given a valid tax year that's above or equal to TaxYear.tysTaxYear" in {
        ResolveTysTaxYear(validTaxYear) shouldBe Valid(TaxYear.fromMtd(validTaxYear))
      }

      "given a valid tax year in an Option" in {
        val result: Validated[Seq[MtdError], Option[TaxYear]] = ResolveTysTaxYear(Option(validTaxYear))
        result shouldBe Valid(Some(TaxYear.fromMtd(validTaxYear)))
      }

      "given an empty Option" in {
        val result: Validated[Seq[MtdError], Option[TaxYear]] = ResolveTysTaxYear(None)
        result shouldBe Valid(None)
      }

    }

    "return an error" when {
      "given a valid tax year but below TaxYear.tysTaxYear" in {
        ResolveTysTaxYear("2021-22") shouldBe Invalid(List(InvalidTaxYearParameterError))
      }
    }
  }

  "ResolveIncompleteTaxYear" should {
    val error = MtdError("SOME_ERROR", "Message", 400)

    def resolver(localDate: LocalDate): Resolver[String, TaxYear] = {
      implicit val clock: Clock = Clock.fixed(localDate.atStartOfDay(ZoneOffset.UTC).toInstant, ZoneOffset.UTC)
      ResolveIncompleteTaxYear(error).resolver
    }

    val taxYear       = "2020-21"
    val parsedTaxYear = TaxYear.fromMtd(taxYear)

    "accept when now is after the tax year ends" in {
      val date = parsedTaxYear.endDate.plusDays(1)
      resolver(date)(taxYear) shouldBe Valid(parsedTaxYear)
    }

    "accept when now is after the tax year ends, called via apply(value) in the case class" in {
      val date = parsedTaxYear.endDate
        .plusDays(1)
        .atStartOfDay(ZoneOffset.UTC)
        .toInstant
      val clock: Clock = Clock.fixed(date, ZoneOffset.UTC)

      val result = ResolveIncompleteTaxYear()(clock)(taxYear)
      result shouldBe Valid(parsedTaxYear)
    }

    "reject when now is on the day the tax year ends" in {
      val date = parsedTaxYear.endDate
      resolver(date)(taxYear) shouldBe Invalid(List(error))
    }

    "reject when now is before the tax year starts" in {
      val date = parsedTaxYear.startDate.minusDays(1)
      resolver(date)(taxYear) shouldBe Invalid(List(error))
    }

  }

}
