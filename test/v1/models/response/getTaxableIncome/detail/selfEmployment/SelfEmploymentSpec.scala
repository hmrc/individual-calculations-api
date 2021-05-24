/*
 * Copyright 2021 HM Revenue & Customs
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

package v1.models.response.getTaxableIncome.detail.selfEmployment

import support.UnitSpec
import v1.fixtures.getTaxableIncome.detail.selfEmployment.SelfEmploymentFixture._
import v1.models.utils.JsonErrorValidators

class SelfEmploymentSpec extends UnitSpec with JsonErrorValidators {

  testJsonProperties[SelfEmployment](selfEmploymentJson)(
    mandatoryProperties = Seq(
      "selfEmploymentId"
    ),
    optionalProperties = Seq(
      "totalIncome",
      "totalExpenses",
      "netProfit",
      "netLoss",
      "class4Loss",
      "totalAdditions",
      "totalDeductions",
      "accountingAdjustments",
      "adjustedIncomeTaxLoss",
      "taxableProfit",
      "taxableProfitAfterIncomeTaxLossesDeduction",
      "lossClaimsSummary",
      "lossClaimsDetail",
      "bsas"
    )
  )
}