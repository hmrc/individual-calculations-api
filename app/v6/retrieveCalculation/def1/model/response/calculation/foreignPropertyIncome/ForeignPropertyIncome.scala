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

package v6.retrieveCalculation.def1.model.response.calculation.foreignPropertyIncome

import play.api.libs.json.{Format, Json, OFormat}
import v6.retrieveCalculation.def1.model.response.common.IncomeSourceType
import v6.retrieveCalculation.def1.model.response.common.IncomeSourceType.`foreign-property`

case class ForeignPropertyIncome(incomeSourceId: String,
                                      incomeSourceType: IncomeSourceType,
                                      countryCode: String,
                                      totalIncome: Option[BigDecimal],
                                      totalExpenses: Option[BigDecimal],
                                      netProfit: Option[BigDecimal],
                                      netLoss: Option[BigDecimal],
                                      totalAdditions: Option[BigDecimal],
                                      totalDeductions: Option[BigDecimal],
                                      taxableProfit: Option[BigDecimal],
                                      adjustedIncomeTaxLoss: Option[BigDecimal])

object ForeignPropertyIncome {
  implicit val incomeSourceTypeFormat: Format[IncomeSourceType] = IncomeSourceType.formatRestricted(`foreign-property`)
  implicit val format: OFormat[ForeignPropertyIncome]           = Json.format[ForeignPropertyIncome]
}
