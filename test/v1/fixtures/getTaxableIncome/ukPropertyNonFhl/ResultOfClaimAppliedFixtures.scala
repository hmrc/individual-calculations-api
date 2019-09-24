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

package v1.fixtures.getTaxableIncome.ukPropertyNonFhl

import play.api.libs.json.{JsValue, Json}
import v1.models.response.getTaxableIncome.detail.ukPropertyNonFhl.detail.ResultOfClaimApplied

object ResultOfClaimAppliedFixtures {

  val resultOfClaimAppliedMtdJson: JsValue = Json.parse(
    """
      |{
      | "claimId" : "EzluDU2ObK02SdA",
      | "originatingClaimId" : "EzluDU2ObK02SdA",
      | "taxYearClaimMade" : "2018-19",
      | "claimType" : "carry-forward",
      | "mtdLoss" : false,
      | "taxYearLossIncurred" : "2018-19",
      | "lossAmountUsed" : 2000,
      | "remainingLossValue" : 2000
      |}
    """.stripMargin)

  val resultOfClaimAppliedMtdJsonWithoutOptionals: JsValue = Json.parse(
    """
      |{
      | "taxYearClaimMade" : "2018-19",
      | "claimType" : "carry-forward",
      | "mtdLoss" : false,
      | "taxYearLossIncurred" : "2018-19",
      | "lossAmountUsed" : 2000,
      | "remainingLossValue" : 2000
      |}
    """.stripMargin)

  val resultOfClaimAppliedModel = ResultOfClaimApplied(Some("EzluDU2ObK02SdA"), Some("EzluDU2ObK02SdA"), "2018-19",
    "carry-forward", false, "2018-19", 2000, 2000)

  val resultOfClaimAppliedModelWithoutOptionals = ResultOfClaimApplied(None, None, "2018-19",
    "carry-forward", false, "2018-19", 2000, 2000)
}
