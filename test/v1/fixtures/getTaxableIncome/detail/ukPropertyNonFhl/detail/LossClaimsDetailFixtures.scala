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

package v1.fixtures.getTaxableIncome.detail.ukPropertyNonFhl.detail

import play.api.libs.json.{JsValue, Json}
import support.UnitSpec
import v1.fixtures.getTaxableIncome.detail.ukPropertyNonFhl.detail.ClaimNotAppliedFixtures._
import v1.fixtures.getTaxableIncome.detail.ukPropertyNonFhl.detail.DefaultCarriedForwardLossFixtures._
import v1.fixtures.getTaxableIncome.detail.ukPropertyNonFhl.detail.LossBroughtForwardFixtures._
import v1.fixtures.getTaxableIncome.detail.ukPropertyNonFhl.detail.ResultOfClaimAppliedFixtures._
import v1.models.response.getTaxableIncome.detail.ukPropertyNonFhl.detail.LossClaimsDetail

object LossClaimsDetailFixtures extends UnitSpec {

  val lossClaimsDetailModel: LossClaimsDetail =
    LossClaimsDetail(
      lossesBroughtForward = Some(Seq(lossBroughtForwardModel)),
      resultOfClaimsApplied = Some(Seq(resultOfClaimAppliedModel)),
      defaultCarriedForwardLosses = Some(Seq(defaultCarriedForwardLossModel)),
      claimsNotApplied = Some(Seq(claimNotAppliedModel))
    )

  val lossClaimsDetailMtdJson: JsValue = Json.parse(
    s"""
       |{
       |   "lossesBroughtForward" : [$lossBroughtForwardJson],
       |   "resultOfClaimsApplied" : [$resultOfClaimAppliedJson],
       |   "defaultCarriedForwardLosses" :[$defaultCarriedForwardLossJson],
       |   "claimsNotApplied" : [$claimNotAppliedJson]
       |}
    """.stripMargin
  )
}