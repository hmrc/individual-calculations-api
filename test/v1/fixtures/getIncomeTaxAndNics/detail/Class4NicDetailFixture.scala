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
import v1.fixtures.getIncomeTaxAndNics.detail.Class4LossesFixture._
import v1.fixtures.getIncomeTaxAndNics.detail.NicBandFixture._
import v1.models.response.getIncomeTaxAndNics.detail.Class4NicDetail

object Class4NicDetailFixture {

  val class4NicDetailModel: Class4NicDetail =
    Class4NicDetail(
      class4Losses = Some(class4LossesModel),
      totalIncomeLiableToClass4Charge = Some(3003),
      totalIncomeChargeableToClass4 = Some(3004),
      class4NicBands = Some(Seq(nicBandModel))
    )

  val class4NicDetailJson: JsValue = Json.parse(
    s"""
      |{
      |   "class4Losses": ${class4LossesJson.toString()},
      |   "totalIncomeLiableToClass4Charge": 3003,
      |   "totalIncomeChargeableToClass4": 3004,
      |   "class4NicBands": [${nicBandJson.toString()}]
      |}
    """.stripMargin
  )
}