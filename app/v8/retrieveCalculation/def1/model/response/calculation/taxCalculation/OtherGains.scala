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

package v8.retrieveCalculation.def1.model.response.calculation.taxCalculation

import play.api.libs.json.{Format, Json}

case class OtherGains(
    gainsIncome: Option[BigDecimal],
    lossesBroughtForward: Option[BigDecimal],
    lossesArisingThisYear: Option[BigDecimal],
    gainsAfterLosses: Option[BigDecimal],
    attributedGains: Option[BigDecimal],
    netGains: Option[BigDecimal],
    annualExemptionAmount: Option[BigDecimal],
    taxableGains: Option[BigDecimal],
    cgtTaxBands: Option[Seq[CgtBand]],
    totalTaxAmount: Option[BigDecimal]
)

object OtherGains {
  implicit val format: Format[OtherGains] = Json.format
}
