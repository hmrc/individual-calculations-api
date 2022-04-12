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
import v3.models.response.common.{IncomeSourceType, LossType}

class UnclaimedLossSpec extends UnitSpec {

  def downstreamJson(incomeSourceType: String, lossType: String): JsValue = Json.parse(s"""
       |{
       |  "incomeSourceId": "123456789012345",
       |  "incomeSourceType": "$incomeSourceType",
       |  "taxYearLossIncurred": 2020,
       |  "currentLossValue": 456,
       |  "lossType": "$lossType"
       |}
       |""".stripMargin)

  def model(incomeSourceType: IncomeSourceType, lossType: LossType): UnclaimedLoss =
    UnclaimedLoss(
      incomeSourceId = Some("123456789012345"),
      incomeSourceType = incomeSourceType,
      taxYearLossIncurred = TaxYear("2020"),
      currentLossValue = BigInt(456),
      lossType = Some(lossType)
    )

  def mtdJson(incomeSourceType: IncomeSourceType, lossType: LossType): JsValue = Json.parse(s"""
       |{
       |  "incomeSourceId": "123456789012345",
       |  "incomeSourceType": "$incomeSourceType",
       |  "taxYearLossIncurred": "2019-20",
       |  "currentLossValue": 456,
       |  "lossType": "$lossType"
       |}
       |""".stripMargin)

  case class Test(downstreamIncomeSourceType: String,
                  incomeSourceType: IncomeSourceType,
                  lossType: LossType)

  val testData: Seq[Test] = Seq[Test](
    Test("01", IncomeSourceType.`self-employment`, LossType.income),
    Test("02", IncomeSourceType.`uk-property-non-fhl`, LossType.class4nics),
    Test("03", IncomeSourceType.`foreign-property-fhl-eea`, LossType.income),
    Test("04", IncomeSourceType.`uk-property-fhl`, LossType.class4nics),
    Test("15", IncomeSourceType.`foreign-property`, LossType.income)
  )

  "reads" should {
    "successfully read in a model" when {

      testData.foreach { case Test(downstreamIncomeSourceType, incomeSourceType, lossType) =>
        s"provided downstream income source type $downstreamIncomeSourceType" in {
          downstreamJson(downstreamIncomeSourceType, lossType.toString).as[UnclaimedLoss] shouldBe
            model(incomeSourceType, lossType)
        }
      }
    }
  }

  "writes" should {
    "successfully write a model to json" when {

      testData.foreach { case Test(_, incomeSourceType, lossType) =>
        s"provided income source type $incomeSourceType" in {
          Json.toJson(model(incomeSourceType, lossType)) shouldBe
            mtdJson(incomeSourceType, lossType)
        }
      }
    }
  }

}
