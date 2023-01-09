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

package v2.fixtures.getTaxableIncome.detail

import play.api.libs.json.{JsValue, Json}
import v2.models.response.getTaxableIncome.detail.UkSecurities

object UkSecuritiesFixture {

  val grossIncome: BigDecimal         = 300.1
  val netIncome: Option[BigDecimal]   = Some(12.3)
  val taxDeducted: Option[BigDecimal] = Some(456.3)

  val securitiesModel: UkSecurities =
    UkSecurities(
      grossIncome = grossIncome,
      netIncome = netIncome,
      taxDeducted = taxDeducted
    )

  val securitiesJson: JsValue = Json.parse(
    s"""
       |{
       |    "grossIncome": $grossIncome,
       |    "netIncome": ${netIncome.get},
       |    "taxDeducted": ${taxDeducted.get}
       |}
     """.stripMargin
  )

}
