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

package v2.fixtures.getEndOfYearEstimate.detail

import play.api.libs.json.{JsValue, Json}
import v2.models.response.getEoyEstimate.detail.EoyEstimateUkSavings

object EoyEstimateUkSavingsFixture {

  val savingsAccountId: String = "yW4zuqfGBZGPlpq"
  val savingsAccountName: Option[String] = Some("bank & building account 1")
  val taxableIncome: BigInt = 1001

  val eoyEstimateUkSavingsModel: EoyEstimateUkSavings =
    EoyEstimateUkSavings(
      savingsAccountId = savingsAccountId,
      savingsAccountName = savingsAccountName,
      taxableIncome = taxableIncome
    )

  val eoyEstimateUkSavingsJson: JsValue = Json.parse(
    s"""
       |{
       |   "savingsAccountId" : "$savingsAccountId",
       |   "savingsAccountName" : "${savingsAccountName.get}",
       |   "taxableIncome" : $taxableIncome
       |}
     """.stripMargin
  )
}