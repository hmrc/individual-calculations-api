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

package v1.fixtures.taxableIncome.detail

import play.api.libs.json.{JsArray, JsValue, Json}
import v1.models.response.taxableIncome.detail.BusinessProfitAndLoss
import v1.models.response.taxableIncome.detail.ukPropertyFhl.UkPropertyFhl
import v1.models.response.taxableIncome.detail.ukPropertyFhl.detail.{DefaultCarriedForwardLoss => FhlDefaultCarriedForwardLoss, LossBroughtForward => FhlLossBroughtForward, LossClaimsDetail => FhlLossClaimsDetail, ResultOfClaimApplied => FhlResultOfClaimApplied}
import v1.models.response.taxableIncome.detail.ukPropertyFhl.summary.{LossClaimsSummary => FhlLossClaimsSummary}
import v1.models.response.taxableIncome.detail.ukPropertyNonFhl.UkPropertyNonFhl
import v1.models.response.taxableIncome.detail.ukPropertyNonFhl.detail.{ClaimNotApplied, DefaultCarriedForwardLoss => NonFhlDefaultCarriedForwardLoss, LossBroughtForward => NonFhlLossBroughtForward, LossClaimsDetail => NonFhlLossClaimsDetail, ResultOfClaimApplied => NonFhlResultOfClaimApplied}
import v1.models.response.taxableIncome.detail.ukPropertyNonFhl.summary.{LossClaimsSummary => NonFhlLossClaimsSummary}

object BusinessProfitAndLossFixtures {

  //@TODO Add self-employment sequence objects once it is ready
  def businessProfitAndLoss(ukPropertyFhl: Option[UkPropertyFhl],
                            ukPropertyNonFhl: Option[UkPropertyNonFhl]): BusinessProfitAndLoss =
    BusinessProfitAndLoss(None,ukPropertyFhl, ukPropertyNonFhl)

  val ukPropertyFhlObject = Some(UkPropertyFhl(Some(1000.00),Some(1000.00),Some(1000.00),Some(1000.00),
    Some(1000.00),Some(1000.00),Some(1000.00),None,Some(1000),None,
    Some(FhlLossClaimsSummary(Some(1000),Some(1000),Some(1000),Some(100))),
    Some(FhlLossClaimsDetail(Some(List(FhlLossBroughtForward("2054-55",1000, mtdLoss = true))),
      Some(List(FhlResultOfClaimApplied(Some("CCIS12345678901"),"2038-39", "carry-forward",
        mtdLoss = true,"2050-51",1000,1000))),Some(List(FhlDefaultCarriedForwardLoss("2026-27",1000)))))))

  val ukPropertyFhlWithoutLossClaimsDetailObject = Some(UkPropertyFhl(Some(1000.00),Some(1000.00),Some(1000.00),Some(1000.00),
    Some(1000.00),Some(1000.00),Some(1000.00),None,Some(1000),None,
    Some(FhlLossClaimsSummary(Some(1000),Some(1000),Some(1000),Some(100))),None))

  val ukPropertyNonFhlWithoutLossClaimsDetailObject = Some(UkPropertyNonFhl(Some(1000.00),Some(1000.00),Some(1000.00),Some(1000.00),Some(1000.00),
    Some(1000.00),Some(1000.00),None,Some(1000),Some(1000),Some(NonFhlLossClaimsSummary(Some(1000),Some(1000),Some(100))),None))

  val ukPropertyNonFhlObject = Some(UkPropertyNonFhl(Some(1000.00),Some(1000.00),Some(1000.00),Some(1000.00),Some(1000.00),
    Some(1000.00),Some(1000.00),None,Some(1000),Some(1000),Some(NonFhlLossClaimsSummary(Some(1000),Some(1000),Some(100))),
    Some(NonFhlLossClaimsDetail(Some(List(NonFhlLossBroughtForward("2054-55",1000, mtdLoss = true))),
    Some(List(NonFhlResultOfClaimApplied(Some("CCIS12345678901"),Some("000000000000210"),"2038-39","carry-forward",
      mtdLoss = true,"2050-51",1000,1000))),Some(List(NonFhlDefaultCarriedForwardLoss("2026-27",1000))),
      Some(List(ClaimNotApplied("CCIS12345678912","2045-46","carry-forward")))))))


}
