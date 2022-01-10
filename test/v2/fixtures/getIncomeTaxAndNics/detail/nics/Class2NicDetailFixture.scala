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

package v2.fixtures.getIncomeTaxAndNics.detail.nics

import play.api.libs.json.{JsValue, Json}
import v2.models.response.getIncomeTaxAndNics.detail.nics.Class2NicDetail

object Class2NicDetailFixture {

  val weeklyRate: Option[BigDecimal] = Some(100.25)
  val weeks: Option[BigDecimal] = Some(200.25)
  val limit: Option[BigDecimal] = Some(300.25)
  val apportionedLimit: Option[BigDecimal] = Some(400.25)
  val underSmallProfitThreshold: Boolean = true
  val actualClass2Nic: Option[Boolean] = Some(false)
  val class2VoluntaryContributions: Option[Boolean] = Some(true)

  val class2NicDetailModel: Class2NicDetail =
    Class2NicDetail(
      weeklyRate = weeklyRate,
      weeks = weeks,
      limit = limit,
      apportionedLimit = apportionedLimit,
      underSmallProfitThreshold = underSmallProfitThreshold,
      actualClass2Nic = actualClass2Nic,
      class2VoluntaryContributions = class2VoluntaryContributions
    )

  val class2NicDetailJson: JsValue = Json.parse(
    s"""
       |{
       |   "weeklyRate": ${weeklyRate.get},
       |   "weeks": ${weeks.get},
       |   "limit": ${limit.get},
       |   "apportionedLimit": ${apportionedLimit.get},
       |   "underSmallProfitThreshold": $underSmallProfitThreshold,
       |   "actualClass2Nic": ${actualClass2Nic.get},
       |   "class2VoluntaryContributions": ${class2VoluntaryContributions.get}
       |}
     """.stripMargin
  )
}