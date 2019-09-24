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

package v1.fixtures.taxableIncome.ukPropertyFhl

import play.api.libs.json.{JsValue, Json}
import v1.models.response.taxableIncome.detail.ukPropertyFhl.detail.ResultOfClaimApplied

object ResultOfClaimAppliedFixtures {

  val claimId: Option[String] = Some("CCIS12345678901")
  val originatingClaimId: String = "000000000000210"
  val incomeSourceId: String = "AAIS12345678904"
  val incomeSourceType: String = "04"
  val taxYearClaimMade: String = "2019-20"
  val claimType: String = "CF"
  val mtdLoss: Boolean = true
  val taxYearLossIncurred: String = "2018-19"
  val lossAmountUsed: BigInt = 1000
  val remainingLossValue: BigInt = 4000

  val resultOfClaimAppliedResponse: ResultOfClaimApplied = ResultOfClaimApplied(
    claimId,
    taxYearClaimMade,
    "carry-forward",
    mtdLoss,
    taxYearLossIncurred,
    lossAmountUsed,
    remainingLossValue
  )

  val resultOfClaimAppliedJson: JsValue = Json.parse(
    f"""{
       |    "claimId": "${claimId.get}",
       |    "taxYearLossIncurred": "$taxYearLossIncurred",
       |    "taxYearClaimMade": "$taxYearClaimMade",
       |    "claimType": "carry-forward",
       |    "mtdLoss": $mtdLoss,
       |    "lossAmountUsed": $lossAmountUsed,
       |    "remainingLossValue": $remainingLossValue
       |}""".stripMargin)

  val resultOfClaimAppliedInvalidJson: JsValue = Json.parse(
    f"""{
       |    "taxYearLossIncurred": "$taxYearLossIncurred",
       |    "mtdLoss": $mtdLoss
       |}""".stripMargin)
}
