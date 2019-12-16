/*
 * Copyright 2019 HM Revenue & Customs
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

import java.time.LocalDate

import support.UnitSpec

class DateUtilsSpec extends UnitSpec{

  "getTaxYear" should {
    "return a tax year" when {
      "mtd formatted string tax year is supplied" in {
        DateUtils.getTaxYear(Some("2018-19"), LocalDate.now()) shouldBe "2018-19"
      }

      "no tax year is supplied and the date is 5th April of the current year" in {
        DateUtils.getTaxYear(None, LocalDate.parse("2019-04-05")) shouldBe "2018-19"
      }

      "no tax year is supplied and the date is 6th April of the current year" in {
        DateUtils.getTaxYear(None, LocalDate.parse("2019-04-06")) shouldBe "2019-20"
      }
    }
  }
}
