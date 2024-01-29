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

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime, ZoneId}
import java.util.Locale

object DateUtils {

  private val longDateTimeFormatGmt: DateTimeFormatter = DateTimeFormatter
    .ofPattern("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH)
    .withZone(ZoneId.of("GMT"))

  def getTaxYear(taxYearOpt: Option[String], currentDate: LocalDate): String = taxYearOpt match {
    case Some(taxYear) => taxYear
    case None =>
      val fiscalYearStartDate = LocalDate.parse(s"${currentDate.getYear.toString}-04-05")

      if (currentDate.isAfter(fiscalYearStartDate)) {
        s"${currentDate.getYear}-${currentDate.getYear.+(1).toString.drop(2)}"
      } else {
        s"${currentDate.getYear.-(1)}-${currentDate.getYear.toString.drop(2)}"
      }
  }

  def longDateTimestampGmt(dateTime: LocalDateTime): String = longDateTimeFormatGmt.format(dateTime)
}
