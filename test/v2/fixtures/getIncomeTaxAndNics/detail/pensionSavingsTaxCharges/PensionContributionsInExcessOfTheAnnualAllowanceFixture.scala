/*
 * Copyright 2023 HM Revenue & Customs
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

package v2.fixtures.getIncomeTaxAndNics.detail.pensionSavingsTaxCharges

import play.api.libs.json.{JsValue, Json}
import v2.fixtures.getIncomeTaxAndNics.detail.pensionSavingsTaxCharges.PensionBandsFixture._
import v2.models.response.getIncomeTaxAndNics.detail.pensionSavingsTaxCharges.PensionContributionsInExcessOfTheAnnualAllowance

object PensionContributionsInExcessOfTheAnnualAllowanceFixture {

  val totalContributions: BigDecimal             = 2000.99
  val totalPensionCharge: BigDecimal             = 3000.99
  val annualAllowanceTaxPaid: Option[BigDecimal] = Some(4000.99)
  val totalPensionChargeDue: BigDecimal          = 5000.99

  val pensionContributionsInExcessOfTheAnnualAllowanceModel: PensionContributionsInExcessOfTheAnnualAllowance =
    PensionContributionsInExcessOfTheAnnualAllowance(
      totalContributions = totalContributions,
      totalPensionCharge = totalPensionCharge,
      annualAllowanceTaxPaid = annualAllowanceTaxPaid,
      totalPensionChargeDue = totalPensionChargeDue,
      pensionBands = Some(Seq(pensionBandsModel))
    )

  val pensionContributionsInExcessOfTheAnnualAllowanceJson: JsValue = Json.parse(
    s"""
       |{
       |   "totalContributions": $totalContributions,
       |   "totalPensionCharge": $totalPensionCharge,
       |   "annualAllowanceTaxPaid": ${annualAllowanceTaxPaid.get},
       |   "totalPensionChargeDue": $totalPensionChargeDue,
       |   "pensionBands": [$pensionBandsJson]
       |}
     """.stripMargin
  )

}
