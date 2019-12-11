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
import v1.models.response.getIncomeTaxAndNics.detail.NicBand

object NicBandFixture {

  val nicBandModel: NicBand =
    NicBand(
      name = "name",
      rate = 100.25,
      threshold = Some(200),
      apportionedThreshold = Some(300),
      income = 400,
      amount = 500.25
    )

  val nicBandJson: JsValue = Json.parse(
    s"""
       |{
       |   "name": "name",
       |	 "rate": 100.25,
       |	 "threshold": 200,
       |	 "apportionedThreshold": 300,
       |	 "income": 400,
       |	 "amount": 500.25
       |}
    """.stripMargin
  )
}