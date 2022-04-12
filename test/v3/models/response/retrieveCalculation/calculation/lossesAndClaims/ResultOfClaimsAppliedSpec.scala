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
import v3.models.response.common.{IncomeSourceType, LossType, TypeOfClaim}

class ResultOfClaimsAppliedSpec extends UnitSpec {

  def downstreamJson(incomeSourceType: String, typeOfClaim: String, lossType: String): JsValue = Json.parse(s"""
       |{
       |  "claimId": "123456789012345",
       |  "originatingClaimId": "123456789012346",
       |  "incomeSourceId": "123456789012347",
       |  "incomeSourceType": "$incomeSourceType",
       |  "taxYearClaimMade": 2020,
       |  "claimType": "$typeOfClaim",
       |  "mtdLoss": false,
       |  "taxYearLossIncurred": 2019,
       |  "lossAmountUsed": 123,
       |  "remainingLossValue": 456,
       |  "lossType": "$lossType"
       |}
       |""".stripMargin)

  def model(incomeSourceType: IncomeSourceType, typeOfClaim: TypeOfClaim, lossType: LossType): ResultOfClaimsApplied =
    ResultOfClaimsApplied(
      claimId = Some("123456789012345"),
      originatingClaimId = Some("123456789012346"),
      incomeSourceId = "123456789012347",
      incomeSourceType = incomeSourceType,
      taxYearClaimMade = TaxYear("2020"),
      claimType = typeOfClaim,
      mtdLoss = Some(false),
      taxYearLossIncurred = TaxYear("2019"),
      lossAmountUsed = BigInt(123),
      remainingLossValue = BigInt(456),
      lossType = Some(lossType)
    )

  def mtdJson(incomeSourceType: IncomeSourceType, typeOfClaim: TypeOfClaim, lossType: LossType): JsValue = Json.parse(s"""
       |{
       |  "claimId": "123456789012345",
       |  "originatingClaimId": "123456789012346",
       |  "incomeSourceId": "123456789012347",
       |  "incomeSourceType": "$incomeSourceType",
       |  "taxYearClaimMade": "2019-20",
       |  "claimType": "$typeOfClaim",
       |  "mtdLoss": false,
       |  "taxYearLossIncurred": "2018-19",
       |  "lossAmountUsed": 123,
       |  "remainingLossValue": 456,
       |  "lossType": "$lossType"
       |}
       |""".stripMargin)

  case class Test(downstreamIncomeSourceType: String,
                  incomeSourceType: IncomeSourceType,
                  downstreamTypeOfClaim: String,
                  typeOfClaim: TypeOfClaim,
                  lossType: LossType)

  val testData: Seq[Test] = Seq[Test](
    Test("01", IncomeSourceType.`self-employment`, "CF", TypeOfClaim.`carry-forward`, LossType.income),
    Test("02", IncomeSourceType.`uk-property-non-fhl`, "CSGI", TypeOfClaim.`carry-sideways`, LossType.class4nics),
    Test("03", IncomeSourceType.`foreign-property-fhl-eea`, "CFCSGI", TypeOfClaim.`carry-forward-to-carry-sideways-general-income`, LossType.income),
    Test("04", IncomeSourceType.`uk-property-fhl`, "CSFHL", TypeOfClaim.`carry-sideways-fhl`, LossType.class4nics),
    Test("15", IncomeSourceType.`foreign-property`, "CB", TypeOfClaim.`carry-backwards`, LossType.income),
    Test("01", IncomeSourceType.`self-employment`, "CBGI", TypeOfClaim.`carry-backwards-general-income`, LossType.class4nics)
  )

  "reads" should {
    "successfully read in a model" when {

      testData.foreach { case Test(downstreamIncomeSourceType, incomeSourceType, downstreamTypeOfClaim, typeOfClaim, lossType) =>
        s"provided downstream type of claim $downstreamTypeOfClaim" in {
          downstreamJson(downstreamIncomeSourceType, downstreamTypeOfClaim, lossType.toString).as[ResultOfClaimsApplied] shouldBe
            model(incomeSourceType, typeOfClaim, lossType)
        }
      }
    }
  }

  "writes" should {
    "successfully write a model to json" when {

      testData.foreach { case Test(_, incomeSourceType, _, typeOfClaim, lossType) =>
        s"provided type of claim $typeOfClaim" in {
          Json.toJson(model(incomeSourceType, typeOfClaim, lossType)) shouldBe
            mtdJson(incomeSourceType, typeOfClaim, lossType)
        }
      }
    }
  }

}
