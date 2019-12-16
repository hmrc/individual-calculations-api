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

package v1.fixtures.getIncomeTaxAndNics.summary

import play.api.libs.json.{JsValue, Json}
import v1.fixtures.getIncomeTaxAndNics.summary.IncomeTaxSummaryFixture._
import v1.fixtures.getIncomeTaxAndNics.summary.NicSummaryFixture._
import v1.models.response.getIncomeTaxAndNics.summary.CalculationSummary

object CalculationSummaryFixture {

  val totalIncomeTaxNicsCharged: Option[BigDecimal] = Some(300.25)
  val totalTaxDeducted: Option[BigDecimal] = Some(400.25)
  val totalIncomeTaxAndNicsDue: BigDecimal = 500.25
  val taxRegime: String = "UK"

  val calculationSummaryModel: CalculationSummary =
    CalculationSummary(
      incomeTax = incomeTaxSummaryModel,
      nics = Some(nicSummaryModel),
      totalIncomeTaxNicsCharged = totalIncomeTaxNicsCharged,
      totalTaxDeducted = totalTaxDeducted,
      totalIncomeTaxAndNicsDue = totalIncomeTaxAndNicsDue,
      taxRegime = taxRegime
    )

  val calculationSummaryJson: JsValue = Json.parse(
    s"""
       |{
       |   "incomeTax" : $incomeTaxSummaryJson,
       |   "nics" : $nicSummaryJson,
       |   "totalIncomeTaxNicsCharged" : ${totalIncomeTaxNicsCharged.get},
       |   "totalTaxDeducted" : ${totalTaxDeducted.get},
       |   "totalIncomeTaxAndNicsDue" : $totalIncomeTaxAndNicsDue,
       |   "taxRegime" : "$taxRegime"
       |}
    """.stripMargin)
}