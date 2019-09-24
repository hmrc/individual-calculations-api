/*
 * Copyright 2019 HM Revenue & Customs
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

package v1.fixtures.taxableIncome.detail

import play.api.libs.json.{JsValue, Json}
import v1.models.response.taxableIncome.detail.Savings

object SavingsFixtures {
  val incomeSourceId: String                   = "anId"
  val incomeSourceName: String                 = "aName"
  val grossIncome: BigDecimal                  = 300.1
  val netIncome: Option[BigDecimal]            = Some(12.3)
  val taxDeducted: Option[BigDecimal]          = Some(456.3)
  val savingsResponse: Savings                 = Savings(incomeSourceId, incomeSourceName, grossIncome, netIncome, taxDeducted)
  val savingsResponseWithoutOptionals: Savings = savingsResponse.copy(netIncome = None, taxDeducted = None)

  val savingsJson: JsValue = Json.parse(s"""{
       |    "savingsAccountId":"$incomeSourceId",
       |    "savingsAccountName":"$incomeSourceName",
       |    "grossIncome":$grossIncome,
       |    "netIncome": ${netIncome.get},
       |    "taxDeducted": ${taxDeducted.get}
       |}""".stripMargin)

  val savingsJsonWithoutOptionals: JsValue = Json.parse(s"""{
       |    "savingsAccountId":"$incomeSourceId",
       |    "savingsAccountName":"$incomeSourceName",
       |    "grossIncome":$grossIncome
       |}""".stripMargin)

  val savingsInvalidJson: JsValue = Json.parse(s"""{
       |    "incomeSourceName":"$incomeSourceName",
       |    "grossIncome":$grossIncome,
       |    "netIncome": ${netIncome.get},
       |    "taxDeducted": ${taxDeducted.get}
       |}""".stripMargin)
}
