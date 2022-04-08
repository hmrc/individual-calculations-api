/*
 * Copyright 2022 HM Revenue & Customs
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

package v3.models.domain

import play.api.libs.json.{Format, Reads, Writes}

/** Opaque representation of a tax year
  */
case class TaxYear(private val value: String) extends AnyVal {
  def toDownstream: String = value

  def toMtd: String = {
    val prefix  = value.take(2)
    val yearTwo = value.drop(2)
    val yearOne = (yearTwo.toInt - 1).toString
    prefix + yearOne + "-" + yearTwo
  }

}

object TaxYear {

  /** @param taxYear
    *   tax year in MTD format (e.g. 2017-18)
    */
  def fromMtd(taxYear: String): TaxYear =
    TaxYear(taxYear.take(2) + taxYear.drop(5))

  def fromDownstream(taxYear: String): TaxYear =
    TaxYear(taxYear)

  val fromDownstreamReads: Reads[TaxYear] = implicitly[Reads[String]].map(fromDownstream)

  val toMtdWrites: Writes[TaxYear] = implicitly[Writes[String]].contramap(_.toMtd)

  val downstreamToMtdFormat: Format[TaxYear] = Format(fromDownstreamReads, toMtdWrites)
}
