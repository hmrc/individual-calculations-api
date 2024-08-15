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

package v6.retrieveCalculation.def2.model.response.calculation.endOfYearEstimate

import play.api.libs.json.Json
import shared.utils.UnitSpec
import v6.common.model.response.IncomeSourceType._

class IncomeSourceSpec extends UnitSpec {

  "reads" should {
    "successfully read in a model" when {
      Seq(
        ("01", `self-employment`),
        ("02", `uk-property-non-fhl`),
        ("03", `foreign-property-fhl-eea`),
        ("04", `uk-property-fhl`),
        ("05", `employments`),
        ("06", `foreign-income`),
        ("07", `foreign-dividends`),
        ("09", `uk-savings-and-gains`),
        ("10", `uk-dividends`),
        ("11", `state-benefits`),
        ("12", `gains-on-life-policies`),
        ("13", `share-schemes`),
        ("15", `foreign-property`),
        ("16", `foreign-savings-and-gains`),
        ("17", `other-dividends`),
        ("18", `uk-securities`),
        ("19", `other-income`),
        ("20", `foreign-pension`),
        ("21", `non-paye-income`),
        ("22", `capital-gains-tax`),
        ("98", `charitable-giving`)
      ).foreach { case (downstreamIncomeSourceType, mtdIncomeSourceType) =>
        s"provided downstream IncomeSourceType of $downstreamIncomeSourceType" in {
          val json = Json.parse(s"""
                 |{
                 |  "incomeSourceId": "123456789012345",
                 |  "incomeSourceType": "$downstreamIncomeSourceType",
                 |  "incomeSourceName": "My name",
                 |  "taxableIncome": 3,
                 |  "finalised": true
                 |}
                 |""".stripMargin)

          val model = IncomeSource(
            incomeSourceId = Some("123456789012345"),
            incomeSourceType = mtdIncomeSourceType,
            incomeSourceName = Some("My name"),
            taxableIncome = 3,
            finalised = Some(true))

          json.as[IncomeSource] shouldBe model
        }
      }
    }
  }

}
