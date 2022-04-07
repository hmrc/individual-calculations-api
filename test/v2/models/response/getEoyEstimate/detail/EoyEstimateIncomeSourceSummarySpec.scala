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

package v2.models.response.getEoyEstimate.detail

import support.UnitSpec
import v2.fixtures.getEndOfYearEstimate.detail.EoyEstimateIncomeSourceSummaryFixture.{incomeSourceSummaryJson, incomeSourceSummaryWithFinalisedJson}
import v2.models.utils.JsonErrorValidators

class EoyEstimateIncomeSourceSummarySpec extends UnitSpec with JsonErrorValidators {

  import EoyEstimateIncomeSourceSummary._

  testJsonProperties[IncomeSourceSummary](incomeSourceSummaryJson)(
    mandatoryProperties = Seq(
      "taxableIncome"
    ),
    optionalProperties = Seq(),
    modelName = Some("IncomeSourceSummary")
  )

  testJsonProperties[IncomeSourceSummaryWithFinalised](incomeSourceSummaryWithFinalisedJson)(
    mandatoryProperties = Seq(
      "taxableIncome"
    ),
    optionalProperties = Seq(
      "finalised"
    ),
    modelName = Some("IncomeSourceSummaryWithFinalised")
  )

}
