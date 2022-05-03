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

package v3.models.response.retrieveCalculation.calculation.chargeableEventGainsIncome

import play.api.libs.json.{Format, Json}

case class GainsWithNoTaxPaidAndVoidedIsaDetail(`type`: String,
                                                customerReference: Option[String],
                                                gainAmount: Option[BigDecimal],
                                                yearsHeld: Option[BigInt],
                                                yearsHeldSinceLastGain: Option[BigInt],
                                                voidedIsaTaxPaid: Option[BigDecimal])

object GainsWithNoTaxPaidAndVoidedIsaDetail {
  implicit val format: Format[GainsWithNoTaxPaidAndVoidedIsaDetail] = Json.format[GainsWithNoTaxPaidAndVoidedIsaDetail]
}
