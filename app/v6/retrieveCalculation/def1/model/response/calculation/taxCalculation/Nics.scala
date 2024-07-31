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

package v6.retrieveCalculation.def1.model.response.calculation.taxCalculation

import play.api.libs.json.{Format, Json}

case class Nics(
    class2Nics: Option[Class2Nics],
    class4Nics: Option[Class4Nics],
    nic2NetOfDeductions: Option[BigDecimal],
    nic4NetOfDeductions: Option[BigDecimal],
    totalNic: Option[BigDecimal]
) {

  def isDefined: Boolean = !(
    class2Nics.isEmpty &&
      class4Nics.isEmpty &&
      nic2NetOfDeductions.isEmpty &&
      nic4NetOfDeductions.isEmpty &&
      totalNic.isEmpty
  )

  def withoutUnderLowerProfitThreshold: Nics =
    copy(class2Nics = class2Nics.map(_.withoutUnderLowerProfitThreshold))

}

object Nics {
  implicit val format: Format[Nics] = Json.format
}
