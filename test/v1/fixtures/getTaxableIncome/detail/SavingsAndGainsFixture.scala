/*
 * Copyright 2020 HM Revenue & Customs
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

package v1.fixtures.getTaxableIncome.detail

import play.api.libs.json.{JsValue, Json}
import v1.fixtures.getTaxableIncome.detail.SavingsFixture._
import v1.models.response.getTaxableIncome.detail.{Savings, SavingsAndGains}

object SavingsAndGainsFixture {

  val incomeReceivedSAG: BigInt = 392
  val taxableIncomeSAG: BigInt = 3920

  val savingsAndGainsModel: SavingsAndGains =
    SavingsAndGains(
      incomeReceived = incomeReceivedSAG,
      taxableIncome = taxableIncomeSAG,
      ukSavings = Some(Seq(savingsModel))
    )

  val savingsAndGainsJson: JsValue = Json.parse(
    s"""{
       |   "incomeReceived" : $incomeReceivedSAG,
       |   "taxableIncome" : $taxableIncomeSAG,
       |   "ukSavings" : [$savingsJson]
       |}
    """.stripMargin
  )
}
