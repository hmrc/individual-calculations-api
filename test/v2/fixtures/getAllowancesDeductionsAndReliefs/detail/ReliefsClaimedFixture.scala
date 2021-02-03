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

package v2.fixtures.getAllowancesDeductionsAndReliefs.detail

import play.api.libs.json.{JsValue, Json}
import v2.models.response.getAllowancesDeductionsAndReliefs.detail.ReliefsClaimed

object ReliefsClaimedFixture {

  val `type`: String = "nonDeductibleLoanInterest"
  val amountClaimed: Option[BigDecimal] = Some(12503.03)
  val allowableAmount: Option[BigDecimal] = Some(12503.99)
  val amountUsed: Option[BigDecimal] = Some(12503.99)
  val rate: Option[BigDecimal] = Some(13.99)


  val reliefsClaimedModel: ReliefsClaimed =
    ReliefsClaimed(
      `type` = `type`,
      amountClaimed = amountClaimed,
      allowableAmount = allowableAmount,
      amountUsed = amountUsed,
      rate = rate
    )

  val reliefsClaimedJson: JsValue = Json.parse(
    s"""
      |{
      |  "type": ${Json.toJson(`type`)},
      |  "amountClaimed": ${amountClaimed.get},
      |  "allowableAmount": ${allowableAmount.get},
      |  "amountUsed": ${amountUsed.get},
      |  "rate": ${rate.get}
      |}
    """.stripMargin
  )
}