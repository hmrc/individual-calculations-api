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

package common.models.domain

/**
 * Represents a tax year for Downstream
 *
 * @param value the tax year string (where 2018 represents 2017-18)
 */
case class DownstreamTaxYear(value: String) extends AnyVal {
  override def toString: String = value
}

object DownstreamTaxYear {

  /**
   * @param taxYear tax year in MTD format (e.g. 2017-18)
   */
  def fromMtd(taxYear: String): DownstreamTaxYear =
    DownstreamTaxYear(taxYear.take(2) + taxYear.drop(5))

  def fromDownstreamIntToString(taxYear: Int): String =
    (taxYear - 1) + "-" + taxYear.toString.drop(2)
}