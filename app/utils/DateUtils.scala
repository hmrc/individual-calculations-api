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

import java.time.{LocalDate, Year}

object DateUtils {

  def getTaxYear(taxYearOpt: Option[String], currentDate: LocalDate = LocalDate.now()): String = taxYearOpt match {
    case Some(taxYear) => taxYear
    case None =>
      val fiscalYearStartDate = LocalDate.parse(s"${currentDate.getYear.toString}-04-05")

      if(currentDate.isAfter(fiscalYearStartDate)){
        s"${currentDate.getYear}-${currentDate.getYear. +(1).toString.drop(2)}"
      }
      else {
        s"${currentDate.getYear. -(1)}-${currentDate.getYear.toString.drop(2)}"
      }
  }
}
