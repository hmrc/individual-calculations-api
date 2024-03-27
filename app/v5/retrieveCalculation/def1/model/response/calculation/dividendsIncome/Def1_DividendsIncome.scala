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

package v5.retrieveCalculation.def1.model.response.calculation.dividendsIncome

import play.api.libs.json.{Format, Json}

case class Def1_DividendsIncome(totalChargeableDividends: Option[BigInt],
                                totalUkDividends: Option[BigInt],
                                ukDividends: Option[Def1_UkDividends],
                                otherDividends: Option[Seq[Def1_OtherDividends]],
                                chargeableForeignDividends: Option[BigInt],
                                foreignDividends: Option[Seq[Def1_CommonForeignDividend]],
                                dividendIncomeReceivedWhilstAbroad: Option[Seq[Def1_CommonForeignDividend]])

object Def1_DividendsIncome {
  implicit val format: Format[Def1_DividendsIncome] = Json.format[Def1_DividendsIncome]
}
