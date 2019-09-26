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

package v1.models.response.getEndOfYearEstimate

import support.UnitSpec
import v1.fixtures.getEndOfYearEstimate.EoyEstimateSummaryFixture
import v1.models.utils.JsonErrorValidators
import v1.models.response.getEndOfYearEstimate.EoyEstimateSummary._

class EoyEstimateSummarySpec extends UnitSpec with JsonErrorValidators {
  testJsonProperties[EoyEstimateSummary](EoyEstimateSummaryFixture.json)(
    mandatoryProperties = Seq(),
    optionalProperties = Seq("totalEstimatedIncome", "totalTaxableIncome", "incomeTaxAmount", "nic2", "nic4", "totalNicAmount", "incomeTaxNicAmount")
  )
}
