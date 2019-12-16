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

package v1.fixtures.getIncomeTaxAndNics.summary

import play.api.libs.json.{JsValue, Json}
import v1.models.response.getIncomeTaxAndNics.summary.IncomeTaxSummary

object IncomeTaxSummaryFixture {

  val incomeTaxCharged: BigDecimal = 2000.00
  val incomeTaxDueAfterReliefs: Option[BigDecimal] = Some(1525.22)
  val incomeTaxDueAfterGiftAid: Option[BigDecimal] = Some(120.10)

  val incomeTaxSummaryModel: IncomeTaxSummary =
    IncomeTaxSummary(
      incomeTaxCharged = 2000.00,
      incomeTaxDueAfterReliefs = Some(1525.22),
      incomeTaxDueAfterGiftAid = Some(120.10)
    )

  val incomeTaxSummaryJson: JsValue = Json.parse(
    s"""
      |{
      |   "incomeTaxCharged" : $incomeTaxCharged,
      |   "incomeTaxDueAfterReliefs" : ${incomeTaxDueAfterReliefs.get},
      |   "incomeTaxDueAfterGiftAid" : ${incomeTaxDueAfterGiftAid.get}
      |}
    """.stripMargin
  )
}