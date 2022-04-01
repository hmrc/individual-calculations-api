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

package v3.fixtures.getEndOfYearEstimate.detail

import play.api.libs.json.{JsValue, Json}
import v3.fixtures.getEndOfYearEstimate.detail.EoyEstimateIncomeSourceSummaryFixture._
import v3.fixtures.getEndOfYearEstimate.detail.EoyEstimateSelfEmploymentsFixture._
import v3.fixtures.getEndOfYearEstimate.detail.EoyEstimateUkSavingsFixture._
import v3.models.response.getEoyEstimate.detail.EoyEstimateDetail

object EoyEstimateDetailFixture {

  val eoyEstimateDetailModel: EoyEstimateDetail =
    EoyEstimateDetail(
      selfEmployments = Some(Seq(eoyEstimateSelfEmploymentsModel)),
      ukPropertyFhl = Some(incomeSourceSummaryWithFinalisedModel),
      ukPropertyNonFhl = Some(incomeSourceSummaryWithFinalisedModel),
      ukSavings = Some(Seq(eoyEstimateUkSavingsModel)),
      ukDividends = Some(incomeSourceSummaryModel),
      otherDividends = Some(incomeSourceSummaryModel),
      foreignCompanyDividends = Some(incomeSourceSummaryModel),
      stateBenefits = Some(incomeSourceSummaryModel),
      ukSecurities = Some(incomeSourceSummaryModel),
      foreignProperty = Some(incomeSourceSummaryWithFinalisedModel),
      eeaPropertyFhl = Some(incomeSourceSummaryModel),
      foreignInterest = Some(incomeSourceSummaryModel),
      otherIncome = Some(incomeSourceSummaryModel),
      foreignPension = Some(incomeSourceSummaryModel)
    )

  val eoyEstimateDetailJson: JsValue = Json.parse(
    s"""
       |{
       | "selfEmployments": [
       |   $eoyEstimateSelfEmploymentsJson
       | ],
       | "ukPropertyFhl": $incomeSourceSummaryWithFinalisedJson,
       | "ukPropertyNonFhl": $incomeSourceSummaryWithFinalisedJson,
       | "ukSavings": [
       |   $eoyEstimateUkSavingsJson
       | ],
       | "ukDividends": $incomeSourceSummaryJson,
       | "otherDividends": $incomeSourceSummaryJson,
       | "foreignCompanyDividends": $incomeSourceSummaryJson,
       | "stateBenefits": $incomeSourceSummaryJson,
       | "ukSecurities": $incomeSourceSummaryJson,
       | "foreignProperty": $incomeSourceSummaryWithFinalisedJson,
       | "eeaPropertyFhl": $incomeSourceSummaryJson,
       | "foreignInterest": $incomeSourceSummaryJson,
       | "otherIncome": $incomeSourceSummaryJson,
       | "foreignPension": $incomeSourceSummaryJson
       |}
     """.stripMargin
  )
}