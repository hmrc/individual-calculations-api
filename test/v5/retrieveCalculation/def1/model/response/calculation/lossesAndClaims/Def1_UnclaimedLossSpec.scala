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

package v5.retrieveCalculation.def1.model.response.calculation.lossesAndClaims

import api.models.domain.TaxYear
import play.api.libs.json.{JsValue, Json}
import support.UnitSpec
import v5.retrieveCalculation.def1.model.response.common.Def1_IncomeSourceType

class Def1_UnclaimedLossSpec extends UnitSpec {

  def downstreamJson(incomeSourceType: String): JsValue = Json.parse(s"""
       |{
       |  "incomeSourceId": "123456789012345",
       |  "incomeSourceType": "$incomeSourceType",
       |  "taxYearLossIncurred": 2020,
       |  "currentLossValue": 456,
       |  "lossType": "income"
       |}
       |""".stripMargin)

  def model(incomeSourceType: Def1_IncomeSourceType): Def1_UnclaimedLoss =
    Def1_UnclaimedLoss(
      incomeSourceId = Some("123456789012345"),
      incomeSourceType = incomeSourceType,
      taxYearLossIncurred = TaxYear.fromDownstream("2020"),
      currentLossValue = BigInt(456),
      lossType = Some("income")
    )

  def mtdJson(incomeSourceType: Def1_IncomeSourceType): JsValue = Json.parse(s"""
       |{
       |  "incomeSourceId": "123456789012345",
       |  "incomeSourceType": "$incomeSourceType",
       |  "taxYearLossIncurred": "2019-20",
       |  "currentLossValue": 456,
       |  "lossType": "income"
       |}
       |""".stripMargin)

  case class Test(downstreamIncomeSourceType: String, incomeSourceType: Def1_IncomeSourceType)

  val testData: Seq[Test] = Seq[Test](
    Test("01", Def1_IncomeSourceType.`self-employment`),
    Test("02", Def1_IncomeSourceType.`uk-property-non-fhl`),
    Test("03", Def1_IncomeSourceType.`foreign-property-fhl-eea`),
    Test("04", Def1_IncomeSourceType.`uk-property-fhl`),
    Test("15", Def1_IncomeSourceType.`foreign-property`)
  )

  "reads" should {
    "successfully read in a model" when {

      testData.foreach { case Test(downstreamIncomeSourceType, incomeSourceType) =>
        s"provided downstream income source type $downstreamIncomeSourceType" in {
          downstreamJson(downstreamIncomeSourceType).as[Def1_UnclaimedLoss] shouldBe
            model(incomeSourceType)
        }
      }
    }
  }

  "writes" should {
    "successfully write a model to json" when {

      testData.foreach { case Test(_, incomeSourceType) =>
        s"provided income source type $incomeSourceType" in {
          Json.toJson(model(incomeSourceType)) shouldBe
            mtdJson(incomeSourceType)
        }
      }
    }
  }

}
