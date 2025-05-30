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

package v8.retrieveCalculation.def3.model.response.calculation.lossesAndClaims

import play.api.libs.json.{JsValue, Json}
import shared.models.domain.TaxYear
import shared.utils.UnitSpec
import v8.common.model.response.IncomeSourceType
import v8.retrieveCalculation.def3.model.response.ClaimType

class ClaimNotAppliedSpec extends UnitSpec {

  def downstreamJson(incomeSourceType: String, claimType: String): JsValue = Json.parse(s"""
       |{
       |  "claimId": "123456789012345",
       |  "incomeSourceId": "123456789012347",
       |  "incomeSourceType": "$incomeSourceType",
       |  "taxYearClaimMade": 2020,
       |  "claimType": "$claimType"
       |}
       |""".stripMargin)

  def model(incomeSourceType: IncomeSourceType, claimType: ClaimType): ClaimNotApplied =
    ClaimNotApplied(
      claimId = "123456789012345",
      incomeSourceId = "123456789012347",
      incomeSourceType = incomeSourceType,
      taxYearClaimMade = TaxYear.fromDownstream("2020"),
      claimType = claimType
    )

  def mtdJson(incomeSourceType: IncomeSourceType, claimType: ClaimType): JsValue = Json.parse(s"""
       |{
       |  "claimId": "123456789012345",
       |  "incomeSourceId": "123456789012347",
       |  "incomeSourceType": "$incomeSourceType",
       |  "taxYearClaimMade": "2019-20",
       |  "claimType": "$claimType"
       |}
       |""".stripMargin)

  case class Test(downstreamIncomeSourceType: String, incomeSourceType: IncomeSourceType, downstreamClaimType: String, claimType: ClaimType)

  val testData: Seq[Test] = Seq[Test](
    Test("01", IncomeSourceType.`self-employment`, "CF", ClaimType.`carry-forward`),
    Test("02", IncomeSourceType.`uk-property`, "CSGI", ClaimType.`carry-sideways`),
    Test("15", IncomeSourceType.`foreign-property`, "CB", ClaimType.`carry-backwards`),
    Test("01", IncomeSourceType.`self-employment`, "CBGI", ClaimType.`carry-backwards-general-income`)
  )

  "reads" should {
    "successfully read in a model" when {

      testData.foreach { case Test(downstreamIncomeSourceType, incomeSourceType, downstreamClaimType, claimType) =>
        s"provided downstream type of claim $downstreamClaimType" in {
          downstreamJson(downstreamIncomeSourceType, downstreamClaimType).as[ClaimNotApplied] shouldBe
            model(incomeSourceType, claimType)
        }
      }
    }
  }

  "writes" should {
    "successfully write a model to json" when {

      testData.foreach { case Test(_, incomeSourceType, _, claimType) =>
        s"provided type of claim $claimType" in {
          Json.toJson(model(incomeSourceType, claimType)) shouldBe
            mtdJson(incomeSourceType, claimType)
        }
      }
    }
  }

}
