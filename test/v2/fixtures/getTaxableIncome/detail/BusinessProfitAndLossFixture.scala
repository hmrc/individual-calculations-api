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
import v2.fixtures.getTaxableIncome.detail.eeaPropertyFhl.EeaPropertyFhlFixture._
import v2.fixtures.getTaxableIncome.detail.foreignProperty.ForeignPropertyFixture._
import v2.fixtures.getTaxableIncome.detail.selfEmployment.SelfEmploymentFixture._
import v2.fixtures.getTaxableIncome.detail.ukPropertyFhl.UkPropertyFhlFixture._
import v2.fixtures.getTaxableIncome.detail.ukPropertyNonFhl.UkPropertyNonFhlFixture._
import v2.models.response.getTaxableIncome.detail.BusinessProfitAndLoss

object BusinessProfitAndLossFixture {

  val businessProfitAndLossModel: BusinessProfitAndLoss =
    BusinessProfitAndLoss(
      Some(Seq(selfEmploymentModel)),
      Some(ukPropertyFhlModel),
      Some(ukPropertyNonFhlModel),
      Some(eeaPropertyFhlModel),
      Some(foreignPropertyModel)
    )

  val businessProfitAndLossJson: JsValue = Json.parse(
    s"""
      |{
      |   "selfEmployments": [$selfEmploymentJson],
      |   "ukPropertyFhl": $ukPropertyFhlJson,
      |   "ukPropertyNonFhl": $ukPropertyNonFhlJson,
      |   "eeaPropertyFhl": $eeaPropertyFhlJson,
      |   "foreignProperty": $foreignPropertyJson
      |}
    """.stripMargin
  )

}
