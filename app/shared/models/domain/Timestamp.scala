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

import play.api.libs.json.{JsString, Reads, Writes}

import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.ISO_DATE_TIME
import java.time.{ZoneId, ZonedDateTime}

case class Timestamp private (value: String) extends AnyVal {
  override def toString: String = value
}

object Timestamp {

  private val formatter =
    DateTimeFormatter
      .ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
      .withZone(ZoneId.of("UTC"))

  /** Adds milliseconds to the timestamp string if not already present.
    */
  def apply(value: String): Timestamp = {
    val ts  = ISO_DATE_TIME.parse(value)
    val dt  = ZonedDateTime.from(ts)
    val str = dt.format(formatter)
    new Timestamp(str)
  }

  implicit val reads: Reads[Timestamp]   = Reads.of[String].map(Timestamp(_))
  implicit val writes: Writes[Timestamp] = ts => JsString(ts.value)
}
