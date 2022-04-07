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
import v2.models.response.getAllowancesDeductionsAndReliefs.detail.ForeignTaxCreditRelief

object ForeignTaxCreditReliefFixture {

  val customerCalculatedRelief: Option[Boolean]                 = Some(true)
  val totalForeignTaxCreditRelief: BigDecimal                   = 14500.99
  val foreignTaxCreditReliefOnProperty: Option[BigDecimal]      = Some(14501.99)
  val foreignTaxCreditReliefOnDividends: Option[BigDecimal]     = Some(14502.99)
  val foreignTaxCreditReliefOnSavings: Option[BigDecimal]       = Some(14503.99)
  val foreignTaxCreditReliefOnForeignIncome: Option[BigDecimal] = Some(14504.99)

  val foreignTaxCreditReliefModel: ForeignTaxCreditRelief =
    ForeignTaxCreditRelief(
      customerCalculatedRelief = customerCalculatedRelief,
      totalForeignTaxCreditRelief = totalForeignTaxCreditRelief,
      foreignTaxCreditReliefOnProperty = foreignTaxCreditReliefOnProperty,
      foreignTaxCreditReliefOnDividends = foreignTaxCreditReliefOnDividends,
      foreignTaxCreditReliefOnSavings = foreignTaxCreditReliefOnSavings,
      foreignTaxCreditReliefOnForeignIncome = foreignTaxCreditReliefOnForeignIncome
    )

  val foreignTaxCreditReliefJson: JsValue = Json.parse(
    s"""
      |{
      |  "customerCalculatedRelief": ${customerCalculatedRelief.get},
      |  "totalForeignTaxCreditRelief": $totalForeignTaxCreditRelief,
      |  "foreignTaxCreditReliefOnProperty": ${foreignTaxCreditReliefOnProperty.get},
      |  "foreignTaxCreditReliefOnDividends": ${foreignTaxCreditReliefOnDividends.get},
      |  "foreignTaxCreditReliefOnSavings": ${foreignTaxCreditReliefOnSavings.get},
      |  "foreignTaxCreditReliefOnForeignIncome": ${foreignTaxCreditReliefOnForeignIncome.get}
      |}
    """.stripMargin
  )

}
