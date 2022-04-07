/*
 * Copyright 2022 HM Revenue & Customs
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
import v2.models.response.getAllowancesDeductionsAndReliefs.detail.ForeignPropertyRfcDetail

object ForeignPropertyRfcDetailFixture {

  val countryCode: String                    = "FRA"
  val amountClaimed: BigInt                  = 13500
  val allowableAmount: BigDecimal            = 13501.99
  val carryForwardAmount: Option[BigDecimal] = Some(13502.99)

  val foreignPropertyRfcDetailModel: ForeignPropertyRfcDetail =
    ForeignPropertyRfcDetail(
      countryCode = countryCode,
      amountClaimed = amountClaimed,
      allowableAmount = allowableAmount,
      carryForwardAmount = carryForwardAmount
    )

  val foreignPropertyRfcDetailJson: JsValue = Json.parse(
    s"""
      |{
      |  "countryCode": "$countryCode",
      |  "amountClaimed": $amountClaimed,
      |  "allowableAmount": $allowableAmount,
      |  "carryForwardAmount": ${carryForwardAmount.get}
      |}
    """.stripMargin
  )

}
