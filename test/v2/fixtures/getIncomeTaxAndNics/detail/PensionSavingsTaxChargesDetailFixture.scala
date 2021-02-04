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

package v2.fixtures.getIncomeTaxAndNics.detail

import play.api.libs.json.{JsValue, Json}
import v2.fixtures.getIncomeTaxAndNics.detail.OverseasPensionContributionsFixture._
import v2.fixtures.getIncomeTaxAndNics.detail.PensionContributionsInExcessOfTheAnnualAllowanceFixture._
import v2.fixtures.getIncomeTaxAndNics.detail.PensionSavingsDetailBreakdownFixture._
import v2.fixtures.getIncomeTaxAndNics.detail.PensionSchemeOverseasTransfersFixture._
import v2.models.response.getIncomeTaxAndNics.detail.PensionSavingsTaxChargesDetail

object PensionSavingsTaxChargesDetailFixture {

  val pensionSavingsTaxChargesDetailModel: PensionSavingsTaxChargesDetail =
    PensionSavingsTaxChargesDetail(
      lumpSumBenefitTakenInExcessOfLifetimeAllowance = Some(pensionSavingsDetailBreakdownModel),
      benefitInExcessOfLifetimeAllowance = Some(pensionSavingsDetailBreakdownModel),
      pensionSchemeUnauthorisedPaymentsSurcharge = Some(pensionSavingsDetailBreakdownModel),
      pensionSchemeUnauthorisedPaymentsNonSurcharge = Some(pensionSavingsDetailBreakdownModel),
      pensionSchemeOverseasTransfers = Some(pensionSchemeOverseasTransfersModel),
      pensionContributionsInExcessOfTheAnnualAllowance = Some(pensionContributionsInExcessOfTheAnnualAllowanceModel),
      overseasPensionContributions = Some(overseasPensionContributionsModel)
    )

  val pensionSavingsTaxChargesDetailJson: JsValue = Json.parse(
    s"""
       |{
       |   "lumpSumBenefitTakenInExcessOfLifetimeAllowance": $pensionSavingsDetailBreakdownJson,
       |   "benefitInExcessOfLifetimeAllowance": $pensionSavingsDetailBreakdownJson,
       |   "pensionSchemeUnauthorisedPaymentsSurcharge": $pensionSavingsDetailBreakdownJson,
       |   "pensionSchemeUnauthorisedPaymentsNonSurcharge": $pensionSavingsDetailBreakdownJson,
       |   "pensionSchemeOverseasTransfers": $pensionSchemeOverseasTransfersJson,
       |   "pensionContributionsInExcessOfTheAnnualAllowance": $pensionContributionsInExcessOfTheAnnualAllowanceJson,
       |   "overseasPensionContributions": $overseasPensionContributionsJson
       |}
     """.stripMargin
  )
}