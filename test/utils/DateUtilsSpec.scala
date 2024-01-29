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

package utils

import support.UnitSpec

import java.time.{LocalDate, LocalDateTime}

class DateUtilsSpec extends UnitSpec {

  "getTaxYear" should {
    "return a tax year" when {
      "given an MTD tax year" in {
        val result = DateUtils.getTaxYear(Some("2018-19"), LocalDate.now())
        result shouldBe "2018-19"
      }

      "given date 5th April of the current year but no tax year" in {
        val result = DateUtils.getTaxYear(None, LocalDate.parse("2019-04-05"))
        result shouldBe "2018-19"
      }

      "given date 6th April of the current year but no tax year" in {
        val result = DateUtils.getTaxYear(None, LocalDate.parse("2019-04-06"))
        result shouldBe "2019-20"
      }
    }
  }

  "longDateTimestampGmt" should {
    "return Date/time in format [EEE, dd MMM yyyy HH:mm:ss z]" when {
      "given a LocalDateTime" in {
        val result = DateUtils.longDateTimestampGmt(
          LocalDateTime.of(2023, 1, 17, 12, 0)
        )
        result shouldBe "Tue, 17 Jan 2023 12:00:00 GMT"
      }
    }
  }

}
