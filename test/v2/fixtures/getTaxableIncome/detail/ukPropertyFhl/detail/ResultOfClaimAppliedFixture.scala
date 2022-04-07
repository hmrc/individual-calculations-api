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

package v2.fixtures.getTaxableIncome.detail.ukPropertyFhl.detail

import play.api.libs.json.{JsValue, Json}
import v2.models.response.common.TypeOfClaim
import v2.models.response.getTaxableIncome.detail.ukPropertyFhl.detail.ResultOfClaimApplied

object ResultOfClaimAppliedFixture {

  val claimId: Option[String]     = Some("CCIS12345678901")
  val taxYearClaimMade: String    = "2019-20"
  val claimType: TypeOfClaim      = TypeOfClaim.`carry-forward`
  val mtdLoss: Boolean            = true
  val taxYearLossIncurred: String = "2018-19"
  val lossAmountUsed: BigInt      = 1000
  val remainingLossValue: BigInt  = 4000

  val resultOfClaimAppliedModel: ResultOfClaimApplied =
    ResultOfClaimApplied(
      claimId = claimId,
      taxYearClaimMade = taxYearClaimMade,
      claimType = claimType,
      mtdLoss = mtdLoss,
      taxYearLossIncurred = taxYearLossIncurred,
      lossAmountUsed = lossAmountUsed,
      remainingLossValue = remainingLossValue
    )

  val resultOfClaimAppliedJson: JsValue = Json.parse(
    s"""
      |{
      |  "claimId": "${claimId.get}",
      |   "taxYearLossIncurred": "$taxYearLossIncurred",
      |   "taxYearClaimMade": "$taxYearClaimMade",
      |   "claimType": ${Json.toJson(claimType)},
      |   "mtdLoss": $mtdLoss,
      |   "lossAmountUsed": $lossAmountUsed,
      |   "remainingLossValue": $remainingLossValue
      |}
    """.stripMargin
  )

}
