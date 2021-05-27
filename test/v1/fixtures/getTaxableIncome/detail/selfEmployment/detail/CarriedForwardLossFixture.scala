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

package v1.fixtures.getTaxableIncome.detail.selfEmployment.detail

import play.api.libs.json.{JsValue, Json}
import v1.models.response.common.{LossType, TypeOfClaim}
import v1.models.response.getTaxableIncome.detail.selfEmployment.detail.CarriedForwardLoss

object CarriedForwardLossFixture {

  val claimId: Option[String] = Some("CCIS12345678911")
  val taxYearClaimMade: Option[String] = Some("2047-48")
  val taxYearLossIncurred: String = "2045-46"
  val currentLossValue: BigInt = 49177438626L
  val lossType: LossType = LossType.INCOME

  val carriedForwardLossModel: CarriedForwardLoss =
    CarriedForwardLoss(
      claimId = claimId,
      claimType = TypeOfClaim.`carry-forward`,
      taxYearClaimMade = taxYearClaimMade,
      taxYearLossIncurred = taxYearLossIncurred,
      currentLossValue = currentLossValue,
      lossType = lossType
    )

  val carriedForwardLossJson: JsValue = Json.parse(
    s"""
      |{
      |   "claimId": "${claimId.get}",
      |   "claimType": "carry-forward",
      |   "taxYearClaimMade": "${taxYearClaimMade.get}",
      |   "taxYearLossIncurred": "$taxYearLossIncurred",
      |   "currentLossValue": $currentLossValue,
      |   "lossType": ${Json.toJson(lossType)}
      |}
    """.stripMargin
  )
}