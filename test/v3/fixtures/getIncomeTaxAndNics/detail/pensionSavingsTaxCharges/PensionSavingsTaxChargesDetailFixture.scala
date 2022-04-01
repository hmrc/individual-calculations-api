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

package v3.fixtures.getIncomeTaxAndNics.detail.pensionSavingsTaxCharges

import play.api.libs.json.{JsValue, Json}
import v3.fixtures.getIncomeTaxAndNics.detail.pensionSavingsTaxCharges.ExcessOfLifetimeAllowanceFixture._
import v3.fixtures.getIncomeTaxAndNics.detail.pensionSavingsTaxCharges.OverseasPensionContributionsFixture._
import v3.fixtures.getIncomeTaxAndNics.detail.pensionSavingsTaxCharges.PensionContributionsInExcessOfTheAnnualAllowanceFixture._
import v3.fixtures.getIncomeTaxAndNics.detail.pensionSavingsTaxCharges.PensionSchemeOverseasTransfersFixture._
import v3.fixtures.getIncomeTaxAndNics.detail.pensionSavingsTaxCharges.PensionSchemeUnauthorisedPaymentsFixture._
import v3.models.response.getIncomeTaxAndNics.detail.pensionSavingsTaxCharges.PensionSavingsTaxChargesDetail

object PensionSavingsTaxChargesDetailFixture {

  val pensionSavingsTaxChargesDetailModel: PensionSavingsTaxChargesDetail =
    PensionSavingsTaxChargesDetail(
      excessOfLifetimeAllowance = Some(excessOfLifetimeAllowanceModel),
      pensionSchemeUnauthorisedPayments = Some(pensionSchemeUnauthorisedPaymentsModel),
      pensionSchemeOverseasTransfers = Some(pensionSchemeOverseasTransfersModel),
      pensionContributionsInExcessOfTheAnnualAllowance = Some(pensionContributionsInExcessOfTheAnnualAllowanceModel),
      overseasPensionContributions = Some(overseasPensionContributionsModel)
    )

  val pensionSavingsTaxChargesDetailJson: JsValue = Json.parse(
    s"""
       |{
       |   "excessOfLifetimeAllowance": $excessOfLifetimeAllowanceJson,
       |   "pensionSchemeUnauthorisedPayments": $pensionSchemeUnauthorisedPaymentsJson,
       |   "pensionSchemeOverseasTransfers": $pensionSchemeOverseasTransfersJson,
       |   "pensionContributionsInExcessOfTheAnnualAllowance": $pensionContributionsInExcessOfTheAnnualAllowanceJson,
       |   "overseasPensionContributions": $overseasPensionContributionsJson
       |}
     """.stripMargin
  )
}