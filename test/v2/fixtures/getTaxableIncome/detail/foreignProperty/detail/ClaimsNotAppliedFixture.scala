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

package v2.fixtures.getTaxableIncome.detail.foreignProperty.detail

import play.api.libs.json.{JsValue, Json}
import v2.models.response.common.TypeOfClaim
import v2.models.response.getTaxableIncome.detail.foreignProperty.detail.ClaimsNotApplied

object ClaimsNotAppliedFixture {

  val claimId: String = "CCIS12345678901"
  val taxYearClaimMade: String = "2019-20"
  val claimType: TypeOfClaim = TypeOfClaim.`carry-forward`

  val claimsNotAppliedModel: ClaimsNotApplied =
    ClaimsNotApplied(
      claimId = claimId,
      taxYearClaimMade = taxYearClaimMade,
      claimType = claimType
    )

  val claimsNotAppliedJson: JsValue = Json.parse(
    s"""
       |{
       |  "claimId": "$claimId",
       |   "claimType": "$claimType",
       |   "taxYearClaimMade": "$taxYearClaimMade"
       |}
     """.stripMargin
  )
}