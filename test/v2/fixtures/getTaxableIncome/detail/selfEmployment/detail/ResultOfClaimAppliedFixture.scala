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

package v2.fixtures.getTaxableIncome.detail.selfEmployment.detail

import play.api.libs.json.{JsValue, Json}
import v2.models.response.common.{LossType, TypeOfClaim}
import v2.models.response.getTaxableIncome.detail.selfEmployment.detail.ResultOfClaimApplied

object ResultOfClaimAppliedFixture {

  val claimId: Option[String] = Some("CCIS12345678901")
  val taxYearClaimMade: String = "2039-40"
  val mtdLoss: Boolean = true
  val taxYearLossIncurred: String = "2051-52"
  val lossAmountUsed: Long = 64613077921L
  val remainingLossValue: Long = 72548288090L
  val lossType: LossType = LossType.INCOME

  val resultOfClaimAppliedModel: ResultOfClaimApplied =
    ResultOfClaimApplied(
      claimId = claimId,
      taxYearClaimMade = taxYearClaimMade,
      claimType = TypeOfClaim.`carry-forward`,
      mtdLoss = mtdLoss,
      taxYearLossIncurred = taxYearLossIncurred,
      lossAmountUsed = lossAmountUsed,
      remainingLossValue = remainingLossValue, lossType
    )

  val resultOfClaimAppliedJson: JsValue = Json.parse(
    s"""
       |{
       |    "claimId": "${claimId.get}",
       |    "taxYearClaimMade": "$taxYearClaimMade",
       |    "claimType": "carry-forward",
       |    "mtdLoss": $mtdLoss,
       |    "taxYearLossIncurred": "$taxYearLossIncurred",
       |    "lossAmountUsed": $lossAmountUsed,
       |    "remainingLossValue": $remainingLossValue,
       |    "lossType": ${Json.toJson(lossType)}
       |}
     """.stripMargin
  )
}