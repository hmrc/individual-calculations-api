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

package v5.retrieveCalculation.def1.model.response.calculation.reliefs

import play.api.libs.json.{Json, OFormat}

case class ForeignTaxCreditRelief(customerCalculatedRelief: Option[Boolean],
                                       totalForeignTaxCreditRelief: BigDecimal,
                                       foreignTaxCreditReliefOnProperty: Option[BigDecimal],
                                       foreignTaxCreditReliefOnDividends: Option[BigDecimal],
                                       foreignTaxCreditReliefOnSavings: Option[BigDecimal],
                                       foreignTaxCreditReliefOnForeignIncome: Option[BigDecimal],
                                       foreignTaxCreditReliefDetail: Option[Seq[ForeignTaxCreditReliefDetail]])

object ForeignTaxCreditRelief {
  implicit val format: OFormat[ForeignTaxCreditRelief] = Json.format[ForeignTaxCreditRelief]
}
