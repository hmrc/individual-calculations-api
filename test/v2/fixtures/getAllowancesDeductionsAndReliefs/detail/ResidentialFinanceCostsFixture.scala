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

package v2.fixtures.getAllowancesDeductionsAndReliefs.detail

import play.api.libs.json.{JsValue, Json}
import v2.fixtures.getAllowancesDeductionsAndReliefs.detail.AllOtherIncomeReceivedWhilstAbroadFixture._
import v2.fixtures.getAllowancesDeductionsAndReliefs.detail.ForeignPropertyFixture._
import v2.fixtures.getAllowancesDeductionsAndReliefs.detail.UkPropertyFixture._
import v2.models.response.getAllowancesDeductionsAndReliefs.detail.ResidentialFinanceCosts

object ResidentialFinanceCostsFixture {

  val adjustedTotalIncome: BigDecimal = 10500.99
  val totalAllowableAmount: Option[BigDecimal] = Some(10501.99)
  val relievableAmount: BigDecimal = 10502.99
  val rate: BigDecimal = 12.99
  val totalResidentialFinanceCostsRelief: BigDecimal = 10503.99

  val residentialFinanceCostsModel: ResidentialFinanceCosts =
    ResidentialFinanceCosts(
      adjustedTotalIncome = adjustedTotalIncome,
      totalAllowableAmount = totalAllowableAmount,
      relievableAmount = relievableAmount,
      rate = rate,
      totalResidentialFinanceCostsRelief = totalResidentialFinanceCostsRelief,
      ukProperty = Some(ukPropertyModel),
      foreignProperty = Some(foreignPropertyModel),
      allOtherIncomeReceivedWhilstAbroad = Some(allOtherIncomeReceivedWhilstAbroadModel)
    )

  val residentialFinanceCostsJson: JsValue = Json.parse(
    s"""
      |{
      |  "adjustedTotalIncome": $adjustedTotalIncome,
      |  "totalAllowableAmount": ${totalAllowableAmount.get},
      |  "relievableAmount": $relievableAmount,
      |  "rate": $rate,
      |  "totalResidentialFinanceCostsRelief": $totalResidentialFinanceCostsRelief,
      |  "ukProperty": $ukPropertyJson,
      |  "foreignProperty": $foreignPropertyJson,
      |  "allOtherIncomeReceivedWhilstAbroad": $allOtherIncomeReceivedWhilstAbroadJson
      |}
    """.stripMargin
  )
}