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

package v1.fixtures.getAllowancesDeductionsAndReliefs.summary

import play.api.libs.json.{JsObject, Json}
import v1.models.response.getAllowancesDeductionsAndReliefs.summary.CalculationSummary

object CalculationSummaryFixture {

  val totalAllowancesAndDeductions: Option[BigInt] = Some(1)
  val totalReliefs: Option[BigInt] = Some(2)

  val calculationSummaryModel: CalculationSummary =
    CalculationSummary(
      totalAllowancesAndDeductions = totalAllowancesAndDeductions,
      totalReliefs = totalReliefs
    )

  val calculationSummaryJson: JsObject = Json.parse(
    s"""
      |{
      |  "totalAllowancesAndDeductions": ${totalAllowancesAndDeductions.get},
      |  "totalReliefs": ${totalReliefs.get}
      |}
    """.stripMargin
  ).as[JsObject]
}