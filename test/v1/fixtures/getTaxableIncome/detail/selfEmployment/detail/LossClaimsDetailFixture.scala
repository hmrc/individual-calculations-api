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

package v1.fixtures.getTaxableIncome.detail.selfEmployment.detail

import play.api.libs.json.{JsValue, Json}
import v1.fixtures.getTaxableIncome.detail.selfEmployment.detail.CarriedForwardLossFixture._
import v1.fixtures.getTaxableIncome.detail.selfEmployment.detail.ClaimNotAppliedFixture._
import v1.fixtures.getTaxableIncome.detail.selfEmployment.detail.LossBroughtForwardFixture._
import v1.fixtures.getTaxableIncome.detail.selfEmployment.detail.ResultOfClaimAppliedFixture._
import v1.fixtures.getTaxableIncome.detail.selfEmployment.detail.UnclaimedLossFixture._
import v1.models.response.getTaxableIncome.detail.selfEmployment.detail._

object LossClaimsDetailFixture {

  val lossClaimsDetailDefaultResponse: LossClaimsDetail =
    LossClaimsDetail(
      Some(Seq(lossBroughtForwardResponse)),
      Some(Seq(resultOfClaimAppliedModel)),
      Some(Seq(unclaimedLossModel)),
      Some(Seq(carriedForwardLossModel)),
      Some(Seq(claimNotAppliedModel))
    )

  val lossClaimsDetailJson: JsValue = Json.parse(
    s"""
       |{
       |  "lossesBroughtForward" : [$lossBroughtForwardJson],
       |  "resultOfClaimsApplied" : [$resultOfClaimAppliedJson],
       |  "unclaimedLosses" : [$unclaimedLossJson],
       |  "carriedForwardLosses" : [$carriedForwardLossJson],
       |  "claimsNotApplied" : [$claimNotAppliedJson]
       |}
    """.stripMargin
  )
}