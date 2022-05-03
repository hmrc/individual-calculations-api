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

package v3.models.response.retrieveCalculation.calculation.dividendsIncome

import play.api.libs.json.{Format, Json}
import v3.models.response.common.IncomeSourceType
import v3.models.response.common.IncomeSourceType.`foreign-dividends`

case class CommonForeignDividend(incomeSourceType: Option[IncomeSourceType],
                                 countryCode: String,
                                 grossIncome: Option[BigDecimal],
                                 netIncome: Option[BigDecimal],
                                 taxDeducted: Option[BigDecimal],
                                 foreignTaxCreditRelief: Option[Boolean])

object CommonForeignDividend {
  implicit val incomeSourceTypeFormat: Format[IncomeSourceType] = IncomeSourceType.formatRestricted(`foreign-dividends`)
  implicit val format: Format[CommonForeignDividend]            = Json.format[CommonForeignDividend]
}
