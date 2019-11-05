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

package v1.fixtures.getTaxableIncome.detail.ukPropertyFhl

import play.api.libs.json.{JsValue, Json}
import v1.models.response.getTaxableIncome.detail.ukPropertyFhl.LossClaimsDetail
import v1.models.response.getTaxableIncome.detail.ukPropertyFhl.detail._

object LossClaimsDetailFixtures {

  val lossClaimsDetail = LossClaimsDetail(
    Some(List(LossBroughtForward("2054-55", 1000, mtdLoss = true))),
    Some(List(ResultOfClaimApplied(Some("CCIS12345678901"), "2038-39", "carry-forward", mtdLoss = true,
      "2050-51", 1000, 1000))),
    Some(List(DefaultCarriedForwardLoss("2026-27", 1000))))

  val lossClaimsDetailWithoutLossBroughtForward = LossClaimsDetail(None,
    Some(List(ResultOfClaimApplied(Some("CCIS12345678901"), "2038-39", "carry-forward", mtdLoss = true,
      "2050-51", 1000, 1000))),
    Some(List(DefaultCarriedForwardLoss("2026-27", 1000))))

  val lossClaimsDetailResultOfClaimApplied = LossClaimsDetail(
    Some(List(LossBroughtForward("2054-55", 1000, mtdLoss = true))),
    None,
    Some(List(DefaultCarriedForwardLoss("2026-27", 1000))))

  val lossClaimsDetailDefaultCarriedForwardLoss = LossClaimsDetail(
    Some(List(LossBroughtForward("2054-55", 1000, mtdLoss = true))),
    Some(List(ResultOfClaimApplied(Some("CCIS12345678901"), "2038-39", "carry-forward", mtdLoss = true, "2050-51", 1000, 1000))), None)

  val lossClaimsDetailWithMultipleLBF = LossClaimsDetail(Some(List(LossBroughtForward("2054-55", 1000, mtdLoss = true),
    LossBroughtForward("2056-57", 1000, mtdLoss = true))),
    Some(List(ResultOfClaimApplied(Some("CCIS12345678901"), "2038-39", "carry-forward", mtdLoss = true,
      "2050-51", 1000, 1000))),
    Some(List(DefaultCarriedForwardLoss("2026-27", 1000))))

  val mtdJson: JsValue = Json.parse(
    """
      |{
      |	"lossesBroughtForward": [{
      |		"taxYearLossIncurred": "2054-55",
      |		"currentLossValue": 1000,
      |		"mtdLoss": true
      |	}],
      |	"resultOfClaimsApplied": [{
      |		"claimId": "CCIS12345678901",
      |		"taxYearClaimMade": "2038-39",
      |		"claimType": "carry-forward",
      |		"mtdLoss": true,
      |		"taxYearLossIncurred": "2050-51",
      |		"lossAmountUsed": 1000,
      |		"remainingLossValue": 1000
      |	}],
      |	"carriedForwardLosses": [{
      |		"taxYearLossIncurred": "2026-27",
      |		"currentLossValue": 1000
      |	}]
      |}
    """.stripMargin)
}
