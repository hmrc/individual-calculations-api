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

package v1.fixtures.getAllowancesDeductionsAndReliefs.detail

import play.api.libs.json.{JsValue, Json}
import v1.models.response.getAllowancesDeductionsAndReliefs.detail.ResidentialFinanceCosts

object ResidentialFinancialCostsFixture {

  val amountClaimed: BigInt = 12500
  val allowableAmount: Option[BigInt] = Some(12500)
  val rate: BigDecimal = 20.0
  val propertyFinanceRelief: BigInt = 12500

  val residentialFinancialCostsModel: ResidentialFinanceCosts =
    ResidentialFinanceCosts(
      amountClaimed = amountClaimed,
      allowableAmount = allowableAmount,
      rate = rate,
      propertyFinanceRelief = propertyFinanceRelief
    )

  val residentialFinancialCostsJson: JsValue = Json.parse(
    s"""
      |{
      |  "amountClaimed" : $amountClaimed,
      |  "allowableAmount" : ${allowableAmount.get},
      |  "rate" : $rate,
      |  "propertyFinanceRelief" : $propertyFinanceRelief
      |}
    """.stripMargin
  )
}