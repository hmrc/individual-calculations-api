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

package v1.fixtures.getIncomeTaxAndNics.detail

import play.api.libs.json.{JsValue, Json}
import v1.models.response.getIncomeTaxAndNics.detail._

object CalculationDetailFixture {

  val incomeTaxDetail = IncomeTaxDetail(Some(IncomeTypeBreakdown(100.25, 200.25, None)), None, None, None)
  val nicDetail = NicDetail(Some(Class2NicDetail(Some(300.25), None, None, None, true, Some(false))), None)
  val taxDeductedAtSource = TaxDeductedAtSource(Some(400.25), None)

  val json: JsValue = Json.parse(
    s"""
       |{
       | "incomeTax" : ${Json.toJson(incomeTaxDetail).toString()},
       | "nics" : ${Json.toJson(nicDetail).toString()},
       | "taxDeductedAtSource" : ${Json.toJson(taxDeductedAtSource).toString()}
       |}
    """.stripMargin)

  val model = CalculationDetail(incomeTaxDetail, Some(nicDetail), Some(taxDeductedAtSource))
}
