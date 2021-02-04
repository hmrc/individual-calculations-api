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
import v2.models.response.getAllowancesDeductionsAndReliefs.detail.ForeignTaxCreditRelief

object ForeignTaxCreditReliefFixture {

  val incomeSourceType: String = "fhlPropertyEea"
  val incomeSourceId: Option[String] = Some("FTR647261934212")
  val countryCode: String = "FRA"
  val allowableAmount: Option[BigDecimal] = Some(12503.99)
  val rate: Option[BigDecimal] = Some(13.99)
  val amountUsed: Option[BigDecimal] = Some(12503.99)

  val foreignTaxCreditReliefModel: ForeignTaxCreditRelief =
    ForeignTaxCreditRelief(
      incomeSourceType = incomeSourceType,
      incomeSourceId = incomeSourceId,
      countryCode = countryCode,
      allowableAmount = allowableAmount,
      rate = rate,
      amountUsed = amountUsed
    )

  val foreignTaxCreditReliefJson: JsValue = Json.parse(
    s"""
      |{
      |  "incomeSourceType": ${Json.toJson(incomeSourceType)},
      |  "incomeSourceId": ${Json.toJson(incomeSourceId.get)},
      |  "countryCode": ${Json.toJson(countryCode)},
      |  "allowableAmount": ${allowableAmount.get},
      |  "rate": ${rate.get},
      |  "amountUsed": ${amountUsed.get}
      |}
    """.stripMargin
  )
}