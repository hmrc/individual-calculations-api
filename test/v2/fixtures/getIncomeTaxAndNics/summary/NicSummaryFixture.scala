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

package v2.fixtures.getIncomeTaxAndNics.summary

import play.api.libs.json.{JsValue, Json}
import v2.models.response.getIncomeTaxAndNics.summary.NicSummary

object NicSummaryFixture {

  val class2NicsAmount: Option[BigDecimal] = Some(100.25)
  val class4NicsAmount: Option[BigDecimal] = Some(200.25)
  val totalNic: Option[BigDecimal] = Some(300.25)

  val nicSummaryModel: NicSummary =
    NicSummary(
      class2NicsAmount = class2NicsAmount,
      class4NicsAmount = class4NicsAmount,
      totalNic = totalNic
    )

  val nicSummaryJson: JsValue = Json.parse(
    s"""
      |{
      |   "class2NicsAmount": ${class2NicsAmount.get},
      |   "class4NicsAmount": ${class4NicsAmount.get},
      |   "totalNic": ${totalNic.get}
      |}
    """.stripMargin
  )
}