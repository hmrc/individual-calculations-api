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

package v1r2.fixtures.getAllowancesDeductionsAndReliefs.detail

import play.api.libs.json.{JsValue, Json}
import v1r2.models.response.getAllowancesDeductionsAndReliefs.detail.AnnualPayments

object AnnualPaymentsFixture {

  val grossAnnualPayments: Option[BigDecimal] = Some(12500.99)
  val reliefClaimed: Option[BigDecimal] = Some(12501.99)
  val rate: Option[BigDecimal] = Some(12.99)

  val annualPaymentsModel: AnnualPayments =
    AnnualPayments(
      grossAnnualPayments = grossAnnualPayments,
      reliefClaimed = reliefClaimed,
      rate = rate
    )

  val annualPaymentsJson: JsValue = Json.parse(
    s"""
      |{
      |  "grossAnnualPayments": ${grossAnnualPayments.get},
      |  "reliefClaimed": ${reliefClaimed.get},
      |  "rate": ${rate.get}
      |}
    """.stripMargin
  )
}