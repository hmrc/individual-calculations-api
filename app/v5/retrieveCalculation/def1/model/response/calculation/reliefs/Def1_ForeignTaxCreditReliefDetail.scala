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

import play.api.libs.json.{Format, Json, OFormat}
import v5.retrieveCalculation.def1.model.response.common.Def1_IncomeSourceType
import v5.retrieveCalculation.def1.model.response.common.Def1_IncomeSourceType._

case class Def1_ForeignTaxCreditReliefDetail(incomeSourceType: Option[Def1_IncomeSourceType],
                                             incomeSourceId: Option[String],
                                             countryCode: String,
                                             foreignIncome: BigDecimal,
                                             foreignTax: Option[BigDecimal],
                                             dtaRate: Option[BigDecimal],
                                             dtaAmount: Option[BigDecimal],
                                             ukLiabilityOnIncome: Option[BigDecimal],
                                             foreignTaxCredit: BigDecimal,
                                             employmentLumpSum: Option[Boolean])

object Def1_ForeignTaxCreditReliefDetail {

  implicit val incomeSourceTypeFormat: Format[Def1_IncomeSourceType] =
    Def1_IncomeSourceType.formatRestricted(
      `foreign-dividends`,
      `foreign-property`,
      `foreign-savings-and-gains`,
      `other-income`,
      `foreign-pension`
    )

  implicit val format: OFormat[Def1_ForeignTaxCreditReliefDetail] = Json.format[Def1_ForeignTaxCreditReliefDetail]
}
