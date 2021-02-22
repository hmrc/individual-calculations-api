/*
 * Copyright 2021 HM Revenue & Customs
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

package v2.fixtures.getTaxableIncome.detail

import play.api.libs.json.{JsValue, Json}
import v2.fixtures.getTaxableIncome.detail.SavingsFixture._
import v2.fixtures.getTaxableIncome.detail.UkSecuritiesFixture._
import v2.models.response.getTaxableIncome.detail.SavingsAndGains

object SavingsAndGainsFixture {

  val incomeReceivedSAG: BigInt = 392
  val taxableIncomeSAG: BigInt = 3920
  val totalOfAllGains: BigInt = 4000
  val totalUkSavingsAndSecurities: Option[BigInt] = Some(4500)
  val totalGainsWithNoTaxPaidAndVoidedIsa: Option[BigInt] = Some(5000)
  val totalForeignGainsOnLifePoliciesNoTaxPaid: Option[BigInt] = Some(5500)
  val totalForeignSavingsAndGainsIncome: Option[BigInt] = Some(6000)

  val savingsAndGainsModel: SavingsAndGains =
    SavingsAndGains(
      incomeReceived = incomeReceivedSAG,
      taxableIncome = taxableIncomeSAG,
      totalOfAllGains = totalOfAllGains,
      totalUkSavingsAndSecurities = totalUkSavingsAndSecurities,
      ukSavings = Some(Seq(savingsModel)),
      ukSecurities = Some(Seq(securitiesModel)),
      totalGainsWithNoTaxPaidAndVoidedIsa = totalGainsWithNoTaxPaidAndVoidedIsa,
      totalForeignGainsOnLifePoliciesNoTaxPaid = totalForeignGainsOnLifePoliciesNoTaxPaid,
      totalForeignSavingsAndGainsIncome = totalForeignSavingsAndGainsIncome
    )

  val savingsAndGainsJson: JsValue = Json.parse(
    s"""
       |{
       |   "incomeReceived": $incomeReceivedSAG,
       |   "taxableIncome": $taxableIncomeSAG,
       |   "totalOfAllGains": $totalOfAllGains,
       |   "totalUkSavingsAndSecurities": ${totalUkSavingsAndSecurities.get},
       |   "ukSavings": [$savingsJson],
       |   "ukSecurities": [$securitiesJson],
       |   "totalGainsWithNoTaxPaidAndVoidedIsa": ${totalGainsWithNoTaxPaidAndVoidedIsa.get},
       |   "totalForeignGainsOnLifePoliciesNoTaxPaid": ${totalForeignGainsOnLifePoliciesNoTaxPaid.get},
       |   "totalForeignSavingsAndGainsIncome": ${totalForeignSavingsAndGainsIncome.get}
       |}
     """.stripMargin
  )
}