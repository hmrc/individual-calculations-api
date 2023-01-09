/*
 * Copyright 2023 HM Revenue & Customs
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
import v2.models.response.getAllowancesDeductionsAndReliefs.detail.UkProperty

object UkPropertyFixture {

  val amountClaimed: BigDecimal              = 12500.99
  val allowableAmount: BigDecimal            = 12501.99
  val carryForwardAmount: Option[BigDecimal] = Some(12502.99)

  val ukPropertyModel: UkProperty =
    UkProperty(
      amountClaimed = amountClaimed,
      allowableAmount = allowableAmount,
      carryForwardAmount = carryForwardAmount
    )

  val ukPropertyJson: JsValue = Json.parse(
    s"""
      |{
      |  "amountClaimed": $amountClaimed,
      |  "allowableAmount": $allowableAmount,
      |  "carryForwardAmount": ${carryForwardAmount.get}
      |}
    """.stripMargin
  )

}
