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

package v3.fixtures.getIncomeTaxAndNics.detail.nics

import play.api.libs.json.{JsValue, Json}
import Class4LossesFixture._
import v3.fixtures.getIncomeTaxAndNics.detail.nics.NicBandFixture._
import v3.models.response.getIncomeTaxAndNics.detail.nics.Class4NicDetail

object Class4NicDetailFixture {

  val totalIncomeLiableToClass4Charge: Option[BigInt] = Some(3003)
  val totalIncomeChargeableToClass4: Option[BigInt] = Some(3004)

  val class4NicDetailModel: Class4NicDetail =
    Class4NicDetail(
      class4Losses = Some(class4LossesModel),
      totalIncomeLiableToClass4Charge = totalIncomeLiableToClass4Charge,
      totalIncomeChargeableToClass4 = totalIncomeChargeableToClass4,
      class4NicBands = Some(Seq(nicBandModel))
    )

  val class4NicDetailJson: JsValue = Json.parse(
    s"""
       |{
       |   "class4Losses": $class4LossesJson,
       |   "totalIncomeLiableToClass4Charge": ${totalIncomeLiableToClass4Charge.get},
       |   "totalIncomeChargeableToClass4": ${totalIncomeChargeableToClass4.get},
       |   "class4NicBands": [$nicBandJson]
       |}
     """.stripMargin
  )
}