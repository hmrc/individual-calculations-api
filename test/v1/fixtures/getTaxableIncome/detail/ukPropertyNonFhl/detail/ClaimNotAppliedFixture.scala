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
import v1.models.response.common.TypeOfClaim
import v1.models.response.getTaxableIncome.detail.ukPropertyNonFhl.detail.ClaimNotApplied

object ClaimNotAppliedFixture {

  val claimId: String = "EzluDU2ObK02SdA"
  val taxYearClaimMade = "2018-19"
  val claimType: TypeOfClaim = TypeOfClaim.`carry-sideways`

  val claimNotAppliedModel: ClaimNotApplied =
    ClaimNotApplied(
      claimId = claimId,
      taxYearClaimMade = taxYearClaimMade,
      claimType = claimType
    )

  val claimNotAppliedJson: JsValue = Json.parse(
    s"""
      |{
      |   "claimId" : "$claimId",
      |   "taxYearClaimMade" : "$taxYearClaimMade",
      |   "claimType" : ${Json.toJson(claimType)}
      |}
    """.stripMargin
  )
}