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

package v3.models.response.retrieveCalculation.calculation.reliefs

import play.api.libs.json.{Format, Json, OFormat}
import v3.models.response.common.IncomeSourceType
import v3.models.response.common.IncomeSourceType._

case class ForeignTaxCreditReliefDetail(incomeSourceType: Option[IncomeSourceType],
                                        incomeSourceId: Option[String],
                                        countryCode: String,
                                        foreignIncome: BigDecimal,
                                        foreignTax: Option[BigDecimal],
                                        dtaRate: Option[BigDecimal],
                                        dtaAmount: Option[BigDecimal],
                                        ukLiabilityOnIncome: Option[BigDecimal],
                                        foreignTaxCredit: BigDecimal,
                                        employmentLumpSum: Option[Boolean])

object ForeignTaxCreditReliefDetail {

  implicit val incomeSourceTypeFormat: Format[IncomeSourceType] =
    IncomeSourceType.formatRestricted(
      `foreign-dividends`,
      `foreign-property`,
      `foreign-savings-and-gains`,
      `other-income`,
      `foreign-pension`
    )

  implicit val format: OFormat[ForeignTaxCreditReliefDetail] = Json.format[ForeignTaxCreditReliefDetail]
}
