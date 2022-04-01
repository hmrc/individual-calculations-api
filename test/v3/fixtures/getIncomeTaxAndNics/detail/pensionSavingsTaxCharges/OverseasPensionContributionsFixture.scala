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

package v3.fixtures.getIncomeTaxAndNics.detail.pensionSavingsTaxCharges

import play.api.libs.json.{JsValue, Json}
import v3.fixtures.getIncomeTaxAndNics.detail.pensionSavingsTaxCharges.ShortServiceRefundBandsFixture._
import v3.models.response.getIncomeTaxAndNics.detail.pensionSavingsTaxCharges.OverseasPensionContributions

object OverseasPensionContributionsFixture {

  val totalShortServiceRefund: BigDecimal = 2000.99
  val totalShortServiceRefundCharge: BigDecimal = 3000.99
  val shortServiceRefundTaxPaid: Option[BigDecimal] = Some(4000.99)
  val totalShortServiceRefundChargeDue: BigDecimal = 5000.99

  val overseasPensionContributionsModel: OverseasPensionContributions =
    OverseasPensionContributions(
      totalShortServiceRefund = totalShortServiceRefund,
      totalShortServiceRefundCharge = totalShortServiceRefundCharge,
      shortServiceRefundTaxPaid = shortServiceRefundTaxPaid,
      totalShortServiceRefundChargeDue = totalShortServiceRefundChargeDue,
      shortServiceRefundBands = Some(Seq(shortServiceRefundBandsModel))
    )

  val overseasPensionContributionsJson: JsValue = Json.parse(
    s"""
       |{
       |   "totalShortServiceRefund": $totalShortServiceRefund,
       |   "totalShortServiceRefundCharge": $totalShortServiceRefundCharge,
       |   "shortServiceRefundTaxPaid": ${shortServiceRefundTaxPaid.get},
       |   "totalShortServiceRefundChargeDue": $totalShortServiceRefundChargeDue,
       |   "shortServiceRefundBands": [$shortServiceRefundBandsJson]
       |}
     """.stripMargin
  )
}