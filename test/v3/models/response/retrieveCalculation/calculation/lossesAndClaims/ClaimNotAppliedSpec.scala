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

package v3.models.response.retrieveCalculation.calculation.lossesAndClaims

import play.api.libs.json.{JsValue, Json}
import support.UnitSpec
import v3.models.domain.TaxYear
import v3.models.response.common.{IncomeSourceType, TypeOfClaim}

class ClaimNotAppliedSpec extends UnitSpec {

  def downstreamJson(incomeSourceType: String, typeOfClaim: String): JsValue = Json.parse(s"""
       |{
       |  "claimId": "123456789012345",
       |  "incomeSourceId": "123456789012347",
       |  "incomeSourceType": "$incomeSourceType",
       |  "taxYearClaimMade": 2020,
       |  "claimType": "$typeOfClaim"
       |}
       |""".stripMargin)

  def model(incomeSourceType: IncomeSourceType, typeOfClaim: TypeOfClaim): ClaimNotApplied =
    ClaimNotApplied(
      claimId = "123456789012345",
      incomeSourceId = "123456789012347",
      incomeSourceType = incomeSourceType,
      taxYearClaimMade = TaxYear("2020"),
      claimType = typeOfClaim
    )

  def mtdJson(incomeSourceType: IncomeSourceType, typeOfClaim: TypeOfClaim): JsValue = Json.parse(s"""
       |{
       |  "claimId": "123456789012345",
       |  "incomeSourceId": "123456789012347",
       |  "incomeSourceType": "$incomeSourceType",
       |  "taxYearClaimMade": "2019-20",
       |  "claimType": "$typeOfClaim"
       |}
       |""".stripMargin)

  case class Test(downstreamIncomeSourceType: String, incomeSourceType: IncomeSourceType, downstreamTypeOfClaim: String, typeOfClaim: TypeOfClaim)

  val testData: Seq[Test] = Seq[Test](
    Test("01", IncomeSourceType.`self-employment`, "CF", TypeOfClaim.`carry-forward`),
    Test("02", IncomeSourceType.`uk-property-non-fhl`, "CSGI", TypeOfClaim.`carry-sideways`),
    Test("03", IncomeSourceType.`foreign-property-fhl-eea`, "CFCSGI", TypeOfClaim.`carry-forward-to-carry-sideways-general-income`),
    Test("04", IncomeSourceType.`uk-property-fhl`, "CSFHL", TypeOfClaim.`carry-sideways-fhl`),
    Test("15", IncomeSourceType.`foreign-property`, "CB", TypeOfClaim.`carry-backwards`),
    Test("01", IncomeSourceType.`self-employment`, "CBGI", TypeOfClaim.`carry-backwards-general-income`)
  )

  "reads" should {
    "successfully read in a model" when {

      testData.foreach { case Test(downstreamIncomeSourceType, incomeSourceType, downstreamTypeOfClaim, typeOfClaim) =>
        s"provided downstream type of claim $downstreamTypeOfClaim" in {
          downstreamJson(downstreamIncomeSourceType, downstreamTypeOfClaim).as[ClaimNotApplied] shouldBe
            model(incomeSourceType, typeOfClaim)
        }
      }
    }
  }

  "writes" should {
    "successfully write a model to json" when {

      testData.foreach { case Test(_, incomeSourceType, _, typeOfClaim) =>
        s"provided type of claim $typeOfClaim" in {
          Json.toJson(model(incomeSourceType, typeOfClaim)) shouldBe
            mtdJson(incomeSourceType, typeOfClaim)
        }
      }
    }
  }

}
