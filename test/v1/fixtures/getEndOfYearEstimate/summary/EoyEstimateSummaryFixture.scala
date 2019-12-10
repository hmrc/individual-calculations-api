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

package v1.fixtures.getEndOfYearEstimate.summary

import play.api.libs.json.{JsValue, Json}
import v1.models.response.getEoyEstimate.summary.EoyEstimateSummary

object EoyEstimateSummaryFixture {

  val eoyEstimateSummaryModel: EoyEstimateSummary =
    EoyEstimateSummary(
      totalEstimatedIncome = Some(1000),
      totalTaxableIncome = Some(2000),
      incomeTaxAmount = Some(3000.98),
      nic2 = Some(4000.98),
      nic4 = Some(5000.98),
      totalNicAmount = Some(6000.98),
      incomeTaxNicAmount = Some(7000.98)
    )

  val eoyEstimateSummaryJson: JsValue = Json.parse(
    """
      |{
      | "totalEstimatedIncome" : 1000,
      | "totalTaxableIncome" : 2000,
      | "incomeTaxAmount" : 3000.98,
      | "nic2" : 4000.98,
      | "nic4" : 5000.98,
      | "totalNicAmount" : 6000.98,
      | "incomeTaxNicAmount" : 7000.98
      |}
    """.stripMargin
  )
}