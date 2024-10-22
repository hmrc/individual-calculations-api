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

package v7.retrieveCalculation.def2.model.response.calculation.lossesAndClaims

import play.api.libs.json.{JsValue, Json}
import shared.models.domain.TaxYear
import shared.utils.UnitSpec
import v7.common.model.response.{ClaimType, IncomeSourceType}

class ResultOfClaimsAppliedSpec extends UnitSpec {

  def downstreamJson(incomeSourceType: String, claimType: String, lossType: String): JsValue = Json.parse(s"""
       |{
       |  "claimId": "123456789012345",
       |  "originatingClaimId": "123456789012346",
       |  "incomeSourceId": "123456789012347",
       |  "incomeSourceType": "$incomeSourceType",
       |  "taxYearClaimMade": 2020,
       |  "claimType": "$claimType",
       |  "mtdLoss": false,
       |  "taxYearLossIncurred": 2019,
       |  "lossAmountUsed": 123,
       |  "remainingLossValue": 456,
       |  "lossType": "$lossType"
       |}
       |""".stripMargin)

  def model(incomeSourceType: IncomeSourceType, claimType: ClaimType, lossType: LossType): ResultOfClaimsApplied =
    ResultOfClaimsApplied(
      claimId = Some("123456789012345"),
      originatingClaimId = Some("123456789012346"),
      incomeSourceId = "123456789012347",
      incomeSourceType = incomeSourceType,
      taxYearClaimMade = TaxYear.fromDownstream("2020"),
      claimType = claimType,
      mtdLoss = Some(false),
      taxYearLossIncurred = TaxYear.fromDownstream("2019"),
      lossAmountUsed = BigInt(123),
      remainingLossValue = BigInt(456),
      lossType = Some(LossType.`class4-nics`)
    )

  def mtdJson(incomeSourceType: IncomeSourceType, claimType: ClaimType, lossType: LossType): JsValue = Json.parse(s"""
       |{
       |  "claimId": "123456789012345",
       |  "originatingClaimId": "123456789012346",
       |  "incomeSourceId": "123456789012347",
       |  "incomeSourceType": "$incomeSourceType",
       |  "taxYearClaimMade": "2019-20",
       |  "claimType": "$claimType",
       |  "mtdLoss": false,
       |  "taxYearLossIncurred": "2018-19",
       |  "lossAmountUsed": 123,
       |  "remainingLossValue": 456,
       |  "lossType": "$lossType"
       |}
       |""".stripMargin)

  case class Test(downstreamIncomeSourceType: String, incomeSourceType: IncomeSourceType, downstreamClaimType: String, claimType: ClaimType, lossType: LossType)

  val testData: Seq[Test] = Seq[Test](
    Test("01", IncomeSourceType.`self-employment`, "CF", ClaimType.`carry-forward`, LossType.`class4-nics`),
    Test("02", IncomeSourceType.`uk-property`, "CSGI", ClaimType.`carry-sideways`, LossType.`class4-nics`),
    Test("03", IncomeSourceType.`foreign-property-fhl-eea`, "CFCSGI", ClaimType.`carry-forward-to-carry-sideways`, LossType.`class4-nics`),
    Test("04", IncomeSourceType.`uk-property-fhl`, "CSFHL", ClaimType.`carry-sideways-fhl`, LossType.`class4-nics`),
    Test("15", IncomeSourceType.`foreign-property`, "CB", ClaimType.`carry-backwards`, LossType.`class4-nics`),
    Test("01", IncomeSourceType.`self-employment`, "CBGI", ClaimType.`carry-backwards-general-income`, LossType.`class4-nics`)
  )

  "reads" should {
    "successfully read in a model" when {

      testData.foreach { case Test(downstreamIncomeSourceType, incomeSourceType, downstreamClaimType, claimType, lossType) =>
        s"provided downstream type of claim $downstreamClaimType" in {
          downstreamJson(downstreamIncomeSourceType, downstreamClaimType, lossType = "class4nics").as[ResultOfClaimsApplied] shouldBe
            model(incomeSourceType, claimType, lossType)
        }
      }
    }
  }

  "writes" should {
    "successfully write a model to json" when {

      testData.foreach { case Test(_, incomeSourceType, _, claimType, lossType) =>
        s"provided type of claim $claimType" in {
          Json.toJson(model(incomeSourceType, claimType, lossType)) shouldBe
            mtdJson(incomeSourceType, claimType, lossType)
        }
      }
    }
  }

}
